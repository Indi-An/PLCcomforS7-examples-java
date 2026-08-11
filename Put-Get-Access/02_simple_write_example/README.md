# 02 – Simple Write Example (PUT/GET access) (Java)

This example demonstrates a **basic PUT/GET write** using PLCcom for S7 (Java).

It shows:
- how to set license credentials (optional): without them the library runs for 15 minutes during a debug session. For uninterrupted work, a free trial licence valid for 14 days is available from the PLCcom for S7 [download website](https://www.indi-an.com/en/plccom/for-s7/fors7-download/)
- how to connect using **TCP ISO on Ethernet** (`TCP_ISO_Device`)
- how to build a simple `WriteDataRequest`
- how to write raw bytes into a DB and evaluate the result (`Quality` / `Message`)

---

## What the sample does

In this sample:
- a `TCP_ISO_Device` is created (IP + rack/slot + PLC type)
- `AutoConnect` is enabled (automatic connect/disconnect)
- a `WriteDataRequest` is created to write into **DB100 starting at address 0**
- the sample writes **4 bytes**: `11, 12, 13, 14`
- the write result is checked and printed

---

## How to run

1. Open `PutGetSimpleWriteSample.java`
2. Enter your license credentials (optional):
   - `authentication.User("")`
   - `authentication.Serial("")`
3. Set the PLC IP address (default in the sample: `192.168.1.100`)
4. Verify rack/slot and PLC type (sample uses: rack `0`, slot `2`, `S7_300_400_compatibel`)
5. Make sure **DB100** exists and the PLC allows PUT/GET write access
6. Run the sample and check the console output

---

## Notes / Guidance

- Alternative connection types are shown in the code as commented examples (e.g., MPI / PPI).
- PUT/GET access may be restricted by PLC security/protection settings.
  If the write fails, verify:
  - PLC permissions for PUT/GET communication
  - correct rack/slot / PLC type
  - correct DB number and address range
- Always check `Quality` / `Message` before assuming the write was successful.
