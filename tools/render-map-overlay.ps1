param(
    [string]$MapSpec = "d:\L12SQ\server\assets\maps\hoalu\hoalu.json",
    [string]$Background = "d:\L12SQ\server\assets\maps\hoalu\background.png",
    [string]$Output = "d:\L12SQ\server\assets\maps\hoalu\debug\hoalu_walkable_overlay.png"
)

$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

$spec = Get-Content $MapSpec -Raw | ConvertFrom-Json
$backgroundImage = [System.Drawing.Image]::FromFile($Background)

$tileSize = [int]$spec.tileSize
$gridWidth = [int]$spec.width
$gridHeight = [int]$spec.height
$canvasWidth = [Math]::Max($backgroundImage.Width, $gridWidth * $tileSize)
$canvasHeight = [Math]::Max($backgroundImage.Height, $gridHeight * $tileSize)

$bitmap = New-Object System.Drawing.Bitmap $canvasWidth, $canvasHeight
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.Clear([System.Drawing.Color]::Black)
$graphics.DrawImage($backgroundImage, 0, 0, $backgroundImage.Width, $backgroundImage.Height)

$walkableBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(96, 52, 199, 89))
$spawnBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(180, 255, 204, 0))
$gridPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(90, 255, 255, 255))
$roomPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(220, 255, 59, 48), 2)
$legendBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(170, 8, 17, 28))
$legendTextBrush = [System.Drawing.Brushes]::White
$font = New-Object System.Drawing.Font("Consolas", 9, [System.Drawing.FontStyle]::Regular)

foreach ($range in $spec.walkableRanges) {
    $x = [int]$range.startCol * $tileSize
    $y = [int]$range.row * $tileSize
    $w = (([int]$range.endCol - [int]$range.startCol + 1) * $tileSize)
    $h = $tileSize
    $graphics.FillRectangle($walkableBrush, $x, $y, $w, $h)
}

foreach ($cell in $spec.spawnCells) {
    $x = ([int]$cell.col * $tileSize) + 4
    $y = ([int]$cell.row * $tileSize) + 4
    $graphics.FillRectangle($spawnBrush, $x, $y, $tileSize - 8, $tileSize - 8)
}

for ($col = 0; $col -le $gridWidth; $col++) {
    $x = $col * $tileSize
    $graphics.DrawLine($gridPen, $x, 0, $x, $gridHeight * $tileSize)
}

for ($row = 0; $row -le $gridHeight; $row++) {
    $y = $row * $tileSize
    $graphics.DrawLine($gridPen, 0, $y, $gridWidth * $tileSize, $y)
}

$room = $spec.roomEntry
$roomX = [int]$room.centerX - ([int]$room.width / 2)
$roomY = [int]$room.centerY - ([int]$room.height / 2)
$graphics.DrawRectangle($roomPen, $roomX, $roomY, [int]$room.width, [int]$room.height)

$legendX = 8
$legendY = 8
$legendW = 170
$legendH = 92
$graphics.FillRectangle($legendBrush, $legendX, $legendY, $legendW, $legendH)

$lines = @(
    "Map: $($spec.mapName)",
    "Grid: $gridWidth x $gridHeight",
    "Tile: ${tileSize}px",
    "Green = walkable",
    "Yellow = spawn",
    "Red = room entry"
)

$textY = $legendY + 8
foreach ($line in $lines) {
    $graphics.DrawString($line, $font, $legendTextBrush, $legendX + 8, $textY)
    $textY += 14
}

$outputDir = Split-Path -Parent $Output
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
$bitmap.Save($Output, [System.Drawing.Imaging.ImageFormat]::Png)

$font.Dispose()
$legendBrush.Dispose()
$roomPen.Dispose()
$gridPen.Dispose()
$spawnBrush.Dispose()
$walkableBrush.Dispose()
$graphics.Dispose()
$backgroundImage.Dispose()
$bitmap.Dispose()

Write-Host $Output
