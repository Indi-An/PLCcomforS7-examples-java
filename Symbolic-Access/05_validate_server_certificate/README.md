# 05 – Validate Server Certificate (TLS) (Java) (works with all licenses)

This example demonstrates certificate validation for TLS-based symbolic access.

It shows:
- registering a certificate validation callback/handler
- accepting/rejecting the server certificate
- connect behavior when validation fails

## Guidance
- Production: do not blindly accept all certificates.
- Prefer fingerprint pinning or a trusted validation strategy.
