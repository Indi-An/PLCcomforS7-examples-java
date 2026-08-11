# 03 – Optimized Reading and Writing (PUT/GET access) (Java)

This example demonstrates **combined and optimized** PUT/GET reading and writing using a `ReadWriteRequestSet`.

It shows:
- connecting via `TCP_ISO_Device` (alternatives: MPI / PPI are shown in the code)
- enabling AutoConnect (automatic connect/disconnect)
- using a `ReadWriteRequestSet` with an explicit operation order (`WRITE_BEVOR_READ`)
- configuring read/write optimization modes
- executing `readWriteData(...)` and evaluating read and write results

---

## What the sample does

The sample builds a mixed request set:

### Read requests
- read 10 BYTE values from **DB1** starting at byte address **0**
- read 78 BYTE values from **DB1** starting at byte address **45**
- read 20 DWORD values from **Markers (Flags)** starting at address **0**

### Write requests
- write 4 BYTE values to **DB100** starting at byte address **0** (`11, 12, 13, 14`)
- write 2 WORD values to **Markers (Flags)** starting at address **95** (`1111, 2222`)
- set one BIT at **M105.0** (true)

---

## Optimization modes (important)

This sample configures:

- Read optimization: `eReadOptimizationMode.AUTO`
  - **AUTO is available only in the Expert edition.**
  - If you do not have an Expert license, set this to `NONE`, `CROSS_AREAS`, or `COMBINE_AREAS`
    (depending on what you want to test).

- Write optimization: `eWriteOptimizationMode.CROSS_AREAS`
  - This mode merges write requests across areas to reduce overhead.

---

## How to run

1. Open `PutGetOptimizedReadingWriting.java`
2. Enter your license credentials (optional):
   - `authentication.User("")`
   - `authentication.Serial("")`
3. Set the PLC IP address (default in the sample: `192.168.1.100`)
4. Verify rack/slot and PLC type (sample uses: rack `0`, slot `2`, `S7_300_400_compatibel`)
5. Ensure the addressed areas exist and are accessible:
   - DB1 (read)
   - DB100 (write)
   - Marker area (read/write)
6. Run the program and check the printed read values and write status messages

---

## Notes / Guidance

- PUT/GET access may be restricted by PLC security/protection settings.
  If read/write fails, verify:
  - PUT/GET permissions on the PLC
  - correct rack/slot / PLC type
  - correct DB numbers and address ranges
- If you do not have an Expert license, switch `eReadOptimizationMode.AUTO` to a non-Expert mode before running.
