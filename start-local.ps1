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

Write-Host "Entorno local cargado."
Write-Host "1. Inicie config-server"
Write-Host "2. Inicie msvc-eureka con perfil local"
Write-Host "3. Inicie msvc-gateway con perfil local"
Write-Host ""
Write-Host "Ejemplos:"
Write-Host "mvn -f config spring-boot:run"
Write-Host "mvn -f eureka spring-boot:run -Dspring-boot.run.profiles=local"
Write-Host "mvn -f gateway spring-boot:run -Dspring-boot.run.profiles=local"

