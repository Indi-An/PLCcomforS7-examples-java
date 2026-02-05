# Symbolic Access – Examples (Java)

This folder contains step-by-step examples for **symbolic access** with PLCcom for S7 (Java).
Each subfolder is a standalone sample project.

If you are new to PLCcom: start with **01_basic_symbolic_read** and then continue in order.

---

## ✅ Recommended order

### 01_basic_symbolic_read (works with all licenses)
Minimal “hello world” for symbolic reads:
- connect to the PLC
- read a few variables by full symbolic name
- print values and disconnect

### 02_symbolic_write_example (works with all licenses)
Minimal example for symbolic writes:
- write one or more values by full symbolic name
- check returned quality/message

### 03_symbolic_subscription_example (works with all licenses)
Subscriptions for continuous monitoring:
- subscribe to variables
- receive updates / check latest values
- stop subscription cleanly

### 04_browse_plc_address_space (works with all licenses)
Browse the symbolic namespace:
- explore DBs/structs/arrays/variables
- find full symbolic variable names for read/write/subscription

### 05_validate_server_certificate (works with all licenses)
TLS security example:
- validate the server/PLC certificate when using TLS-based symbolic access

### 06_alarms (works with all licenses)
Alarm handling example:
- read alarm states
- detect changes
- optionally acknowledge alarms (depending on PLC program/config)

### 07_symbolic_read_optimization_modes (advanced / performance)
Symbolic read optimization modes:
- NONE / OBJECT_BASED / CROSS_OBJECT
- SMART (TLS device only)

### 08_symbolic_read_result_hierarchy_scope (advanced / Expert feature)
Result hierarchy scope (child member access):
- request only a container/root (e.g., a DB)
- access child members by full variable name via Get/TryGet
- uses eResultHierarchyScope.Auto (resolved based on license)

Without an appropriate license, PLCcom falls back to RequestedOnly and child member lookup returns false/null.

---

## 🔧 Common prerequisites

- Java 11 runtime (recommended as required by PLCcom for S7)
- Configure PLC IP address in the sample
- Enter license credentials (optional); without credentials runtime is limited (trial)
- Choose the correct device type (TLS / legacy) as shown in each sample
