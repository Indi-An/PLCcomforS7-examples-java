# 01 – Simple Read Example (PUT/GET access) (Java)

This example demonstrates a **basic PUT/GET read** using PLCcom for S7 (Java).

It shows:
- how to set license credentials (optional): without them the library runs for 15 minutes during a debug session. For uninterrupted work, a free trial licence valid for 14 days is available from the PLCcom for S7 [download website](https://www.indi-an.com/en/plccom/for-s7/fors7-download/)
- how to connect using **TCP ISO on Ethernet** (`TCP_ISO_Device`)
- how to build a simple `ReadDataRequest`
- how to read values and evaluate `Quality` / `Message`

---

## What the sample does

In this sample:
- a `TCP_ISO_Device` is created (IP + rack/slot + PLC type)
- `AutoConnect` is enabled (automatic connect/disconnect)
- a `ReadDataRequest` is created to read **10 BYTE values from DB1 starting at address 0**
- the result is printed byte by byte

See `PutGetSimpleReadSample.java` for the exact parameters.  
(DB = 1, start = 0, datatype = BYTE, quantity = 10)

---

## How to run

1. Open `PutGetSimpleReadSample.java`
2. Enter your license credentials (optional):
   - `authentication.User("")`
   - `authentication.Serial("")`
3. Set the PLC IP address (default in the sample: `192.168.1.100`)
4. Verify rack/slot and PLC type (sample uses: rack `0`, slot `2`, `S7_300_400_compatibel`)
5. Run the sample and check the printed bytes

---

## Notes / Guidance

- Alternative connection types are shown in the code as commented examples:
  - `MPI_Device`
  - `PPI_Device`
- PUT/GET access may be restricted by PLC security/protection settings.
  If a read fails, verify:
  - PLC permissions for PUT/GET communication
  - correct rack/slot / PLC type
  - correct DB number and address range
- Always check `Quality` and `Message` in the result before using the values.
