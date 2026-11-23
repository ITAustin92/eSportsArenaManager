param([switch]$InMemory)
$ErrorActionPreference = 'Stop'
$raizProyecto = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$carpetaTemporal = Join-Path $raizProyecto 'tmp'
$puertosServicios = @(8761, 8080, 8099) + (8000..8010)
if (Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object LocalPort -in $puertosServicios) {
    throw 'Un puerto del proyecto está ocupado. No se reemplazará ningún servicio existente.'
}
$javaProyecto = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { (Get-Command java).Source }
$modulosProyecto = @('msvc-eureka') + @(Get-ChildItem $raizProyecto -Directory -Filter 'msvc-*' | Where-Object Name -ne 'msvc-eureka' | Select-Object -ExpandProperty Name)
$ejecutablesProyecto = foreach ($moduloProyecto in $modulosProyecto) {
    $archivoJar = Get-ChildItem (Join-Path $raizProyecto "$moduloProyecto/target") -Filter '*.jar' | Select-Object -First 1
    if (!$archivoJar) { throw "Falta compilar $moduloProyecto. Ejecuta primero ./mvnw.cmd package." }
    @{ modulo = $moduloProyecto; jar = $archivoJar.FullName }
}
New-Item -ItemType Directory -Path $carpetaTemporal -Force | Out-Null
$procesosProyecto = @()
foreach ($ejecutableProyecto in $ejecutablesProyecto) {
    $moduloProyecto = $ejecutableProyecto.modulo
    $argumentosProyecto = '-Xms48m -Xmx256m -XX:MaxMetaspaceSize=192m -jar "' + $ejecutableProyecto.jar + '" --server.address=127.0.0.1 --eureka.instance.hostname=localhost --eureka.instance.prefer-ip-address=true --eureka.instance.ip-address=127.0.0.1'
    if ($InMemory -and $moduloProyecto -notin @('msvc-eureka', 'msvc-gateway')) {
        $argumentosProyecto += ' --spring.datasource.url=jdbc:h2:mem:demo'
    }
    $procesoProyecto = Start-Process -FilePath $javaProyecto -ArgumentList $argumentosProyecto -WindowStyle Hidden -PassThru -WorkingDirectory (Join-Path $raizProyecto $moduloProyecto) -RedirectStandardOutput "$carpetaTemporal/$moduloProyecto.out.log" -RedirectStandardError "$carpetaTemporal/$moduloProyecto.err.log"
    $procesosProyecto += @{ modulo = $moduloProyecto; pid = $procesoProyecto.Id; inicio = $procesoProyecto.StartTime.ToString('o') }
    $procesosProyecto | ConvertTo-Json | Set-Content "$carpetaTemporal/local-services.json" -Encoding UTF8
    Write-Output "Iniciando $moduloProyecto"
}
Write-Output 'Esperar a que http://localhost:8080/api/v1/games responda antes de usar el frontend. Los logs están en tmp/.'
