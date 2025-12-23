
import com.indian.plccom.fors7.IServerCertificateValidator;
import com.indian.plccom.fors7.Tls13Device;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.TlsFatalAlert;

/**
 * Simple test program demonstrating custom server certificate validation.
 */
public final class ValidateServerCertificateSample {

    /**
     * Application entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Tls13Device device = new Tls13Device("192.168.1.10");

        try {
            // 1) Create validator
            IServerCertificateValidator validator = new MyCustomCertificateValidator();

            // 2) Assign validator to the device
            device.setServerCertificateValidator(validator);

            // 3) optional trusted certificates
            // device.setTrustedCertificates(null);

            // 4) optional accept all (not recommended)
            // device.setAcceptAllServerCertificates(true);

            // 5) connect
            var connectResult = device.connect();
            if (connectResult.isQualityGood()) {
                System.out.println("Connected successfully with valid server certificate.");
            } else {
                System.out.println("Connection failed: " + connectResult.toString());
            }

            System.out.println("Press Enter to exit...");
            System.in.read();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            device.disConnect();
        }
    }

    /**
     * Example custom validator (hook point for customer-specific rules).
     */
    public static final class MyCustomCertificateValidator implements IServerCertificateValidator {

        /**
         * Validates the server certificate chain.
         *
         * @param host The expected host name or IP.
         * @param certificateChain The presented certificate chain.
         * @param trustedCertificates Optional trust store certificates.
         * @param acceptAllServerCertificates If {@code true}, validation is bypassed.
         * @throws java.io.IOException If validation fails.
         */
        @Override
        public void validate(String host,
                             X509Certificate[] certs,
                             X509Certificate[] trustedCertificates,
                             boolean acceptAllServerCertificates) throws IOException {
            try {
                if (certs == null || certs.length == 0) {
                    throw new TlsFatalAlert(AlertDescription.no_certificate,
                            new Exception("Server did not send a certificate."));
                }

                if (acceptAllServerCertificates) {
                    return;
                }

                X509Certificate leaf = certs[0];

                // 1) Validity period
                leaf.checkValidity(new Date());

                // 2) Verify against trust store – only if provided 
                if (trustedCertificates != null && trustedCertificates.length > 0) {
                    for (X509Certificate presented : certs) {
                        boolean trusted = false;

                        for (X509Certificate trustedCert : trustedCertificates) {
                            try {
                                PublicKey pk = trustedCert.getPublicKey();
                                presented.verify(pk);
                                trusted = true;
                                break;
                            } catch (Exception ignore) {
                                // ignore and try next trustedCert
                            }
                        }

                        if (!trusted) {
                            throw new TlsFatalAlert(AlertDescription.unknown_ca,
                                    new Exception("Untrusted certificate: " + presented.getSubjectX500Principal()));
                        }
                    }
                }

                // 3) Compare host/IP against certificate
                if (!matchesHostNameOrIp(leaf, host)) {
                    throw new TlsFatalAlert(AlertDescription.bad_certificate,
                            new Exception("Certificate does not match host '" + host + "'."));
                }

            } catch (TlsFatalAlert e) {
                throw e;
            } catch (Exception e) {
                throw new TlsFatalAlert(AlertDescription.internal_error, e);
            }
        }

        private boolean matchesHostNameOrIp(X509Certificate cert, String expectedHost) {
            // Prefer SAN (DNS/IP)
            try {
                Collection<List<?>> altNames = cert.getSubjectAlternativeNames();
                if (altNames != null) {
                    for (List<?> entry : altNames) {
                        if (entry == null || entry.size() < 2) continue;

                        Object typeObj = entry.get(0);
                        Object valueObj = entry.get(1);
                        if (!(typeObj instanceof Integer) || !(valueObj instanceof String)) continue;

                        int type = (Integer) typeObj;
                        String value = (String) valueObj;

                        // 2 = dNSName, 7 = iPAddress
                        if ((type == 2 || type == 7) && value.equalsIgnoreCase(expectedHost)) {
                            return true;
                        }
                    }
                }
            } catch (Exception ignore) {
                // fall back to CN
            }

            // Fallback: CN from subject
    		try {

    			String dn = cert.getSubjectX500Principal().getName("RFC2253");
    			String cn = extractCnFromDn(dn);
    			return cn != null && cn.equalsIgnoreCase(expectedHost);
    		} catch (Exception ignore) {
    			// give up
    		}

    		return false;
    	}

    	// Extracts the first CN (Common Name) from a DN string without using
    	// javax.naming.ldap.
    	// This is a lightweight RFC2253-style parser good enough for typical
    	// certificate DNs.
    	// Example DN: "CN=example.com,OU=IT,O=Company,C=DE"
    	private static String extractCnFromDn(String dn) {
    		// Defensive checks: no input, no CN.
    		if (dn == null || dn.isEmpty())
    			return null;

    		// We'll scan the DN character-by-character and split it into RDN "parts" by
    		// commas.
    		// We *must* respect escaped commas (\,) and commas inside quotes ("...,..."),
    		// otherwise we'd split in the wrong place.
    		StringBuilder token = new StringBuilder(); // accumulates the current RDN text (e.g., "CN=example.com")
    		boolean escaped = false; // true if the previous char was '\' (escape marker)
    		boolean inQuotes = false; // true while inside double quotes

    		// Loop to dn.length(), plus one extra iteration to "flush" the last token as if
    		// a comma existed at the end.
    		for (int i = 0; i <= dn.length(); i++) {
    			// When i==dn.length(), we simulate a comma to force processing of the last
    			// accumulated token.
    			char c = (i == dn.length()) ? ',' : dn.charAt(i);

    			// If the previous character was a backslash, this character is escaped and must
    			// be taken literally.
    			if (escaped) {
    				token.append(c);
    				escaped = false;
    				continue;
    			}

    			// Start of an escape sequence. Keep the backslash in the token so
    			// unescapeDnValue can handle it later.
    			if (c == '\\') {
    				token.append(c);
    				escaped = true;
    				continue;
    			}

    			// Toggle quoted mode when encountering a double quote.
    			// Commas inside quotes must NOT be treated as separators.
    			if (c == '"') {
    				token.append(c);
    				inQuotes = !inQuotes;
    				continue;
    			}

    			// A comma ends the current RDN *only if* we are not inside quotes.
    			if (c == ',' && !inQuotes) {
    				// Grab the completed RDN string (e.g., "CN=example.com" or "OU=IT")
    				String part = token.toString().trim();
    				token.setLength(0); // reset for the next RDN

    				// Split the RDN into "attribute=value" at the first '='.
    				// We only care about CN=...
    				int eq = part.indexOf('=');
    				if (eq > 0) {
    					String attr = part.substring(0, eq).trim(); // attribute name (e.g., "CN")
    					String val = part.substring(eq + 1).trim(); // raw value (may be quoted/escaped)

    					// If this RDN is CN, normalize the value (strip quotes, unescape, trim) and
    					// return it.
    					if ("CN".equalsIgnoreCase(attr)) {
    						return unescapeDnValue(stripQuotes(val)).trim();
    					}
    				}
    			} else {
    				// Regular character: keep accumulating into the current token.
    				token.append(c);
    			}
    		}

    		// No CN attribute found in any RDN.
    		return null;
    	}

    	// Removes surrounding double quotes if present, e.g. "\"example.com\"" ->
    	// "example.com".
    	private static String stripQuotes(String s) {
    		if (s == null)
    			return null;
    		if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
    			return s.substring(1, s.length() - 1);
    		}
    		return s;
    	}

    	// Unescapes backslash-escaped sequences in a DN value.
    	// For our use-case we treat "\X" as "X" (e.g., "\," becomes "," and "\ "
    	// becomes " ").
    	// This keeps the CN readable for host matching.
    	private static String unescapeDnValue(String s) {
    		if (s == null)
    			return null;

    		StringBuilder out = new StringBuilder(s.length());
    		boolean esc = false;

    		for (int i = 0; i < s.length(); i++) {
    			char c = s.charAt(i);

    			if (esc) {
    				// Previous char was '\': take this char literally and drop the backslash.
    				out.append(c);
    				esc = false;
    			} else if (c == '\\') {
    				// Start escape mode; next character will be appended literally.
    				esc = true;
    			} else {
    				// Normal character.
    				out.append(c);
    			}
    		}

    		// If the string ends with a lone backslash, keep it as-is.
    		if (esc)
    			out.append('\\');

    		return out.toString();
    	}
    }
}
