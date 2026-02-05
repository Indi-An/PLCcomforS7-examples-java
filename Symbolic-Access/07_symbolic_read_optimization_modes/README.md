# 07 – Symbolic Read Optimization Modes (Java) (advanced)

This example explains symbolic read optimization modes and the trade-offs:
- NONE (best for debugging)
- OBJECT_BASED / CROSS_OBJECT (fewer read operations, better performance)
- SMART (TLS device only)

## Guidance
- Start with NONE while developing.
- Switch to OBJECT_BASED/CROSS_OBJECT once stable.
- SMART is for TLS-based devices only.
