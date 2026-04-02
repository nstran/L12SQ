param(
    [string]$SourceJar = "d:\L12SQ\loan-12-su-quan.jar",
    [string]$OutputJar = "d:\L12SQ\loan-12-su-quan-online-lan.jar",
    [string]$LanIp = "192.168.1.226",
    [string]$AuthPort = "7236",
    [string]$GamePort = "7238"
)

$ErrorActionPreference = "Stop"

function Replace-AsciiExact {
    param(
        [string]$Path,
        [string]$Search,
        [string]$Replace
    )

    if ($Replace.Length -gt $Search.Length) {
        throw "Replacement '$Replace' is longer than '$Search' for $Path"
    }

    if ($Replace.Length -lt $Search.Length) {
        $Replace = $Replace.PadRight($Search.Length, ' ')
    }

    $bytes = [System.IO.File]::ReadAllBytes($Path)
    $searchBytes = [System.Text.Encoding]::ASCII.GetBytes($Search)
    $replaceBytes = [System.Text.Encoding]::ASCII.GetBytes($Replace)
    $count = 0

    for ($i = 0; $i -le $bytes.Length - $searchBytes.Length; $i++) {
        $matched = $true
        for ($j = 0; $j -lt $searchBytes.Length; $j++) {
            if ($bytes[$i + $j] -ne $searchBytes[$j]) {
                $matched = $false
                break
            }
        }

        if ($matched) {
            [Array]::Copy($replaceBytes, 0, $bytes, $i, $replaceBytes.Length)
            $count++
            $i += $searchBytes.Length - 1
        }
    }

    if ($count -eq 0) {
        throw "Pattern '$Search' not found in $Path"
    }

    [System.IO.File]::WriteAllBytes($Path, $bytes)
    return $count
}

function Replace-BytesExact {
    param(
        [string]$Path,
        [byte[]]$Search,
        [byte[]]$Replace
    )

    if ($Replace.Length -ne $Search.Length) {
        throw "Binary replacement length mismatch for $Path"
    }

    $bytes = [System.IO.File]::ReadAllBytes($Path)
    $count = 0

    for ($i = 0; $i -le $bytes.Length - $Search.Length; $i++) {
        $matched = $true
        for ($j = 0; $j -lt $Search.Length; $j++) {
            if ($bytes[$i + $j] -ne $Search[$j]) {
                $matched = $false
                break
            }
        }

        if ($matched) {
            [Array]::Copy($Replace, 0, $bytes, $i, $Replace.Length)
            $count++
            $i += $Search.Length - 1
        }
    }

    if ($count -eq 0) {
        throw "Binary pattern not found in $Path"
    }

    [System.IO.File]::WriteAllBytes($Path, $bytes)
    return $count
}

if (-not (Test-Path -LiteralPath $SourceJar)) {
    throw "Source jar not found: $SourceJar"
}

$tempDir = Join-Path $env:TEMP ("l12sq-online-lan-" + [guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Path $tempDir | Out-Null

try {
    Copy-Item -LiteralPath $SourceJar -Destination $OutputJar -Force

    Push-Location $tempDir
    & jar xf $SourceJar eh.class km.class dv.class kq.class

    $patched = @()

    foreach ($ip in @(
        "210.211.116.129",
        "222.255.121.164",
        "210.211.116.131",
        "123.30.108.85",
        "210.211.116.132",
        "123.30.108.163",
        "210.211.116.134",
        "123.30.108.168",
        "210.211.116.139",
        "123.30.108.233",
        "210.211.116.175"
    )) {
        $count = Replace-AsciiExact -Path (Join-Path $tempDir "eh.class") -Search $ip -Replace $LanIp
        $patched += "eh.class: $ip -> $LanIp ($count)"
    }

    foreach ($ip in @(
        "210.211.116.155",
        "210.211.116.156",
        "210.211.116.157",
        "210.211.116.158"
    )) {
        $count = Replace-AsciiExact -Path (Join-Path $tempDir "km.class") -Search $ip -Replace $LanIp
        $patched += "km.class: $ip -> $LanIp ($count)"
    }

    $patched += "dv.class: 1236 -> $AuthPort (" + (Replace-AsciiExact -Path (Join-Path $tempDir "dv.class") -Search "1236" -Replace $AuthPort) + ")"

    $kqSearch = [byte[]](0x2A, 0x11, 0x04, 0xD6, 0xB5, 0x00, 0x4A, 0x2A)
    $kqReplace = [byte[]](0x2A, 0x11, 0x1C, 0x46, 0xB5, 0x00, 0x4A, 0x2A)
    $patched += "kq.class: 1238 -> $GamePort (" + (Replace-BytesExact -Path (Join-Path $tempDir "kq.class") -Search $kqSearch -Replace $kqReplace) + ")"

    & jar uf $OutputJar eh.class km.class dv.class kq.class

    Write-Host "Patched JAR written to: $OutputJar"
    $patched | ForEach-Object { Write-Host $_ }
    Write-Host "Note: fallback domain 'ocs.ola.vn' remains unchanged inside eh.class."
}
finally {
    Pop-Location
    Remove-Item -LiteralPath $tempDir -Recurse -Force -ErrorAction SilentlyContinue
}
