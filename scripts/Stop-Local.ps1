$ErrorActionPreference = 'Stop'
$raizProyecto = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$archivoRegistro = Join-Path $raizProyecto 'tmp/local-services.json'
if (!(Test-Path $archivoRegistro)) { Write-Output 'No hay servicios registrados por Start-Local.'; exit }
foreach ($servicioProyecto in (Get-Content $archivoRegistro -Raw | ConvertFrom-Json)) {
    $procesoProyecto = Get-Process -Id $servicioProyecto.pid -ErrorAction SilentlyContinue
    if ($procesoProyecto -and $procesoProyecto.ProcessName -eq 'java' -and [math]::Abs(($procesoProyecto.StartTime - [datetime]$servicioProyecto.inicio).TotalSeconds) -lt 2) {
        Stop-Process -Id $servicioProyecto.pid
        Write-Output "Detenido $($servicioProyecto.modulo)"
    }
}
