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
2. Enter license credentials. For execution, a (test) license is required. Users can request a trial license themselves via the PLCcom for S7 [download website](https://www.indi-an.com/en/plccom/for-s7/fors7-download/)
3. Set the PLC IP address.
4. Run.

## Notes
- Keep configuration simple when starting:
  - optimization = NONE
  - scope = RequestedOnly
- After this works, continue with 07 (optimization) or 08 (result hierarchy scope).
