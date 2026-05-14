$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $root ".env.docker"
$composeFile = Join-Path $root "docker-compose.yml"

if (-not (Test-Path $envFile)) {
    throw "No existe el archivo $envFile"
}

if (-not (Test-Path $composeFile)) {
    throw "No existe el archivo $composeFile"
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker no esta instalado o no esta disponible en el PATH"
}

Write-Host "Levantando stack Docker con variables de $envFile"
Push-Location $root
try {
    & docker compose --env-file $envFile up -d --build
}
finally {
    Pop-Location
}
