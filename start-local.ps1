$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $root "load-env.ps1")
Import-EnvFiles -RootPath $root -FileNames @(".env", ".env.local")

Write-Host "Entorno local cargado."
Write-Host "1. Inicie config-server"
Write-Host "2. Inicie msvc-eureka con perfil local"
Write-Host "3. Inicie msvc-gateway con perfil local"
Write-Host ""
Write-Host "Ejemplos:"
Write-Host "mvn -f config spring-boot:run"
Write-Host "mvn -f eureka spring-boot:run"
Write-Host "mvn -f gateway spring-boot:run"
Write-Host ".\\start-service-local.ps1 pagosonline"
