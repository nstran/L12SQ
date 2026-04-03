# J2ME Resource Constraints and Best Practices

This skill defines the limitations and standards for developing J2ME (Java Micro Edition) games like "Loan 12 Su Quan".

## Resource Limits

### 1. Background Images
- **Maximum Height**: 160px - 200px (standard screen height).
- **Maximum Width**: Unlimited, but must be divided into **Rooms (Khu)**.
- **Color Depth**: Use **Indexed Color (8-bit / 256 colors)**. Avoid 24-bit/32-bit images as they occupy 4x memory when uncompressed.
- **File Size**: Individual room chunks should be under **50KB** if possible.

### 2. Map Configuration
- **Tile Size**: 32x32 pixels.
- **Room Width**: Typically **8 tiles (256px)**. 
- **Room Cache**: The client can usually only store 1 or 2 rooms at a time.

### 3. Memory (Heap)
- Most J2ME devices (and KEmulator defaults) have a Heap size between **2MB and 4MB**.
- A 256x256 RGBA image in memory is **256KB**. 
- A 1088x160 total map and its logic layers should be managed carefully.

## Workflow

1. **Process Assets**: Use `process_map.py` to resize, crop, and convert images to 8-bit.
2. **Define Logic**: Update `hoalu.json` ensuring coordinates are within height limits (0-4).
3. **Build & Test**: Compile the server and start in a new window to monitor logs.
4. **KEmulator JVM**: If OOM persists, check if the emulator is configured for `SonyEricssonK800` or a device with at least 5MB RAM.
