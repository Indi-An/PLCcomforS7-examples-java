# 03 – Symbolic Subscription Example (Java) (works with all licenses)

This example demonstrates subscriptions for continuous monitoring.

It shows:
- creating a subscription
- adding variables
- receiving updates / checking current values
- stopping/disposing the subscription cleanly

## Notes
- Prefer subscriptions over tight polling loops for frequent updates.
- Keep update rates reasonable to reduce PLC and network load.
