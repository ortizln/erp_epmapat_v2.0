$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $root ".env.local"

Get-Content $envFile | ForEach-Object {
    if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
    $parts = $_ -split '=', 2
    if ($parts.Length -eq 2) {
        [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
    }
}

$services = @(
    @{ Name = "config"; Dir = "config"; Args = "spring-boot:run" },
    @{ Name = "eureka"; Dir = "eureka"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" },
    @{ Name = "gateway"; Dir = "gateway"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" },
    @{ Name = "login"; Dir = "login"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" },
    @{ Name = "recaudacion"; Dir = "recaudacion"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" },
    @{ Name = "comercializacion"; Dir = "comercializacion"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" },
    @{ Name = "sri"; Dir = "sri-files"; Args = "spring-boot:run -Dspring-boot.run.profiles=local" }
)

foreach ($svc in $services) {
    $workdir = Join-Path $root $svc.Dir
    $command = "Set-Location '$workdir'; " +
        "[System.Environment]::SetEnvironmentVariable('CONFIG_SERVER_URL','http://localhost:8888','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('EUREKA_SERVICEURL','http://localhost','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('SPRING_PROFILES_ACTIVE','local','Process'); " +
        "mvn $($svc.Args)"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
}
