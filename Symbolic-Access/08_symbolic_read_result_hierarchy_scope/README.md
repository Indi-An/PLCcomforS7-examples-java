# 08 – Result Hierarchy Scope (Java) (Expert feature)

This example demonstrates result hierarchy scope using eResultHierarchyScope.Auto.

Goal:
- request only a container/root (e.g., DataBlock_1)
- then access child members by full variable name via Get/TryGet (e.g., DataBlock_1.ByteValue)

## License behavior (important)
- Expert license:
  - Auto is resolved to RequestedAndChildMembers
  - child member lookup works
- Standard license:
  - Auto is resolved to RequestedOnly
  - child member lookup returns false/null

If RequestedAndChildMembers is requested explicitly without an appropriate license,
ReadData(...) throws an exception (license restriction).

## Technical note
Scope does NOT change what is read from the PLC.
It only changes what the result may expose via Get/TryGet and internal indexing.
