$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $root ".env.docker"
$composeFile = Join-Path $root "docker-compose.yml"

function Get-ComposeCommand {
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        try {
            & docker compose version | Out-Null
            return @("docker", "compose")
        }
        catch {
        }
    }

    if (Get-Command docker-compose -ErrorAction SilentlyContinue) {
        return @("docker-compose")
    }

    throw "No se encontro ni 'docker compose' ni 'docker-compose' en el PATH"
}

if (-not (Test-Path $envFile)) {
    throw "No existe el archivo $envFile"
}

if (-not (Test-Path $composeFile)) {
    throw "No existe el archivo $composeFile"
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker no esta instalado o no esta disponible en el PATH"
}

$composeCmd = Get-ComposeCommand
Write-Host "Levantando stack Docker con variables de $envFile"
Write-Host "Comando detectado: $($composeCmd -join ' ')"

Push-Location $root
try {
    if ($composeCmd.Count -eq 2) {
        & $composeCmd[0] $composeCmd[1] --env-file $envFile down --remove-orphans
        & $composeCmd[0] $composeCmd[1] --env-file $envFile up -d --build
    }
    else {
        Write-Warning "Se detecto docker-compose v1. Si aparece 'ContainerConfig', migra a 'docker compose' v2 o limpia el contenedor conflictivo."
        & $composeCmd[0] --env-file $envFile down --remove-orphans
        & $composeCmd[0] --env-file $envFile up -d --build
    }
}
finally {
    Pop-Location
}
