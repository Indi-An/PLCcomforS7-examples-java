# 04 – Simple Dataserver (PUT/GET access) (Java) (Expert feature)

This example demonstrates a **simple PLCcom data server** based on classic **PUT/GET access**.

It shows:
- creating a PUT/GET device connection (`TCP_ISO_Device`)
- enabling AutoConnect with an idle disconnect time
- creating a `PLCComDataServer_TCP`
- adding multiple `ReadDataRequest` items to the server
- receiving read result updates via callback events
- adding a `LoggingConnector` (FileSystemConnector) to write log files / data images
- starting and stopping the server

---

## License note (important)

This example is an **Expert feature**.

---

## What the sample does

- Creates a `TCP_ISO_Device` with:
  - IP: `192.168.1.100`
  - rack: `0`, slot: `2`
  - PLC type: `S7_300_400_compatibel`
- Enables AutoConnect with idle disconnect time `10000 ms`
- Creates a data server instance:
  - name: `PLCDataServerTCP1`
  - cycle time: `500 ms`
- Registers event callbacks:
  - connection state changes
  - incoming log entries
  - read data result changes
- Adds two read requests to the data server:
  1) Read **4 BYTE** from **DB1** starting at byte address **0**
  2) Read **10 DWORD** from **Flags/Markers** starting at address **4**
- Adds a `FileSystemConnector` to:
  - write log files and a data image to the local filesystem
  - output format: `.dat`
  - with retention limits (max files / age / size)
- Starts the server and waits for user input (press ENTER)
- Stops the server on exit

---

## How to run

1. Open `Simple_Dataserver.java`
2. Enter your license credentials:
   - `authentication.User("")`
   - `authentication.Serial("")`
3. Set the PLC IP address and verify rack/slot/PLC type:
   - default: `192.168.1.100`, rack `0`, slot `2`, `S7_300_400_compatibel`
4. Ensure PUT/GET access is allowed by PLC security/protection settings
5. Run the program
6. Watch the console output:
   - connection state changes
   - read results (item key, quality, values)
7. Press ENTER to stop the server

---

## Notes / Guidance

- If you need to use a different connection type, the code shows alternatives (commented):
  - `MPI_Device`
  - `PPI_Device`
- If read/write fails, verify:
  - PUT/GET permissions on the PLC
  - correct connection parameters (IP / rack / slot)
  - correct DB numbers and address ranges
- The `FileSystemConnector` writes output into the current folder (the example uses the current directory).
  Ensure the process has write permissions.
