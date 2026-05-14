$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $root ".env.local"
$logsDir = Join-Path $root "logs\\local"

function Import-EnvFile {
    param([string]$Path)

    Get-Content $Path | ForEach-Object {
        if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
        $parts = $_ -split '=', 2
        if ($parts.Length -eq 2) {
            [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
        }
    }
}

function Start-ServiceWindow {
    param(
        [hashtable]$Service,
        [string]$BaseDir,
        [string]$LogsPath
    )

    $workdir = Join-Path $BaseDir $Service.Dir
    $serviceLogDir = Join-Path $LogsPath $Service.Name
    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $logFile = Join-Path $serviceLogDir "$timestamp.log"
    $currentLogFile = Join-Path $serviceLogDir "current.log"

    if (-not (Test-Path $serviceLogDir)) {
        New-Item -ItemType Directory -Path $serviceLogDir -Force | Out-Null
    }

    $profileAssignment = if ($Service.Name -eq "config") {
        "Remove-Item Env:SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue"
    }
    else {
        "[System.Environment]::SetEnvironmentVariable('SPRING_PROFILES_ACTIVE','local','Process')"
    }

    $mavenArgs = ($Service.Args -split ' ') | ForEach-Object { "'" + $_ + "'" }
    $mavenCommand = "& mvn " + ($mavenArgs -join ' ')

    $command = @"
Set-Location '$workdir'
[System.Environment]::SetEnvironmentVariable('CONFIG_SERVER_URL','http://localhost:8888','Process')
[System.Environment]::SetEnvironmentVariable('EUREKA_SERVICEURL','http://localhost','Process')
[System.Environment]::SetEnvironmentVariable('SPRING_CLOUD_CONFIG_PROFILE','local','Process')
$profileAssignment
[System.Environment]::SetEnvironmentVariable('TZ','America/Guayaquil','Process')
[System.Environment]::SetEnvironmentVariable('JAVA_TOOL_OPTIONS','-Duser.timezone=America/Guayaquil','Process')
`$logFile = '$logFile'
`$currentLogFile = '$currentLogFile'
"[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] Iniciando $($Service.Name)" | Tee-Object -FilePath `$logFile -Append | Tee-Object -FilePath `$currentLogFile
$mavenCommand 2>&1 | Tee-Object -FilePath `$logFile -Append | Tee-Object -FilePath `$currentLogFile -Append
"@

    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
}

function Wait-Url {
    param(
        [string]$Url,
        [string]$Name,
        [int]$MaxAttempts = 45,
        [int]$DelaySeconds = 2
    )

    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        try {
            Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 | Out-Null
            Write-Host "$Name disponible en $Url"
            return
        }
        catch {
            Start-Sleep -Seconds $DelaySeconds
        }
    }

    throw "No fue posible validar $Name en $Url"
}

Import-EnvFile -Path $envFile

if (-not (Test-Path $logsDir)) {
    New-Item -ItemType Directory -Path $logsDir -Force | Out-Null
}

$coreServices = @(
    @{ Name = "config"; Dir = "config"; Args = "spring-boot:run" },
    @{ Name = "eureka"; Dir = "eureka"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" },
    @{ Name = "gateway"; Dir = "gateway"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" }
)

$microservices = @(
    @{ Name = "comercializacion"; Dir = "comercializacion"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" },
    @{ Name = "sri"; Dir = "sri-files"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" },
    @{ Name = "emails"; Dir = "emails"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" },
    @{ Name = "pagosonline"; Dir = "pagosonline"; Args = "spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--spring.profiles.active=local,--spring.cloud.config.profile=local" }
)

Write-Host "Iniciando servicios base del stack local..."
Start-ServiceWindow -Service $coreServices[0] -BaseDir $root -LogsPath $logsDir
Wait-Url -Url "http://localhost:8888/actuator/health" -Name "Config Server"

Start-ServiceWindow -Service $coreServices[1] -BaseDir $root -LogsPath $logsDir
Wait-Url -Url "http://localhost:8761" -Name "Eureka"

Start-ServiceWindow -Service $coreServices[2] -BaseDir $root -LogsPath $logsDir
Wait-Url -Url "http://localhost:8888/msvc-gateway/local" -Name "Config local de Gateway"

Write-Host "Iniciando microservicios con perfil local..."
foreach ($svc in $microservices) {
    Start-ServiceWindow -Service $svc -BaseDir $root -LogsPath $logsDir
    Start-Sleep -Seconds 2
}

Write-Host "Stack local lanzado. Todos los procesos fueron abiertos en ventanas separadas."
Write-Host "Logs disponibles en $logsDir"
