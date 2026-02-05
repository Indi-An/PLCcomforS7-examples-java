# 01 – Basic Symbolic Read (Java) (works with all licenses)

This is the minimal starting point for PLCcom symbolic reads.

It demonstrates:
- creating a symbolic device
- Connect()
- creating a ReadSymbolicRequest
- adding variables using full symbolic names
- ReadData(...)
- printing result quality and values
- disconnecting cleanly

## How to run
1. Open the project and run Program/Main class.
2. Enter license credentials (optional). Without credentials the runtime is limited.
3. Set the PLC IP address.
4. Run.

## Notes
- Keep configuration simple when starting:
  - optimization = NONE
  - scope = RequestedOnly
- After this works, continue with 07 (optimization) or 08 (result hierarchy scope).
