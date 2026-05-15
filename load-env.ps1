$ErrorActionPreference = "Stop"

function Import-EnvFiles {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RootPath,

        [Parameter(Mandatory = $true)]
        [string[]]$FileNames
    )

    foreach ($fileName in $FileNames) {
        $envFile = Join-Path $RootPath $fileName
        if (-not (Test-Path $envFile)) { continue }

        Get-Content $envFile | ForEach-Object {
            if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
            $parts = $_ -split '=', 2
            if ($parts.Length -eq 2) {
                [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
            }
        }
    }
}
