import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import com.indian.plccom.fors7.*;

/**
 * SymbolicReadSample (Java 11)
 *
 * Demonstrates:
 *  1) Connecting to a PLC via PLCcom (TLS or legacy)
 *  2) Building a symbolic read request
 *  3) Using symbolic read optimization modes to reduce round-trips / improve throughput
 *  4) Reading variables and printing values (or per-variable errors)
 */
public class SymbolicReadSample implements IProjectImportProgressChangedCallback {

    public static void main(String[] args) {
        var instance = new SymbolicReadSample();
        instance.program(args);
    }

    private void program(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            // =========================================================================
            // License / Authentication (IMPORTANT)
            // =========================================================================
            // Enter your Username + Serial here!
            // Please note: Without a license key (empty fields), the runtime is limited to 10 minutes.
            authentication.User("");    // Please enter your user name
            authentication.Serial("");  // Please enter your user serial key

            // =========================================================================
            // Device selection (TLS vs Legacy)
            // =========================================================================
            // Use Tls13Device for modern PLCs with TLS 1.3 support (recommended when available).
            // Use LegacySymbolicDevice for legacy access to older PLCs/firmware.
            //
            // IMPORTANT for optimization mode SMART:
            // - SMART is supported ONLY with TLS connections (Tls13Device).
            // - Do NOT use SMART with LegacySymbolicDevice.
            SymbolicDevice device = new Tls13Device("192.168.1.100");
            // SymbolicDevice device = new LegacySymbolicDevice("192.168.1.100");

            // Register project import progress event (useful for large projects).
            device.addOnProjectImportProgressChangedListener(this);

            // Connect to the PLC
            ConnectResult connectResult = device.connect();
            if (connectResult.getQuality() != OperationResult.eQuality.GOOD) {
                System.out.println("Connect not successful! Quality: " + connectResult.getQuality()
                        + " Message: " + connectResult.getMessage());
                return;
            }

            // =========================================================================
            // Symbolic Read Optimization (IMPORTANT)
            // =========================================================================
            //
            // PLCcom can optimize symbolic read operations by reducing the number of PLC read
            // operations needed to fetch multiple symbolic variables.
            //
            // Why optimization helps:
            // - Reading many variables individually can produce many network round-trips.
            // - Each round-trip adds latency + protocol overhead.
            // - Optimization modes allow PLCcom to group variables into fewer read operations
            //   when beneficial.
            // - Result: typically improved performance (lower cycle time / higher throughput),
            //   especially when reading larger sets of variables.
            //
            // Trade-off / guidance:
            // - NONE provides maximum transparency and deterministic behavior:
            //   each variable is read individually. This is ideal for troubleshooting.
            // - Higher modes are more aggressive (fewer PLC operations) and may improve performance,
            //   but the internal request splitting/grouping becomes less transparent.
            //
            // Available modes (in increasing optimization strength):
            //   NONE         (0) -> No optimization, read each variable individually.
            //   OBJECT_BASED (1) -> PLCcom may group variables within ONE root object
            //                       (e.g., within the same DB/structure/array).
            //   CROSS_OBJECT (2) -> Like OBJECT_BASED, but PLCcom may also group reads across
            //                       multiple root objects (e.g., multiple datablocks).
            //   SMART        (3) -> Smart optimization, TLS connections only (Tls13Device).
            //
            // IMPORTANT notes for customers:
            // - Optimization modes other than NONE are available in the PLCcom Expert edition.
            // - SMART must only be used with TLS (Tls13Device). It is not supported with legacy devices.
            // - This setting affects symbolic READ operations only. WRITE operations are not affected.
            //
            // How to apply:
            // - Pass the optimization mode when creating the ReadSymbolicRequest:
            //     new ReadSymbolicRequest(eSymbolicReadOptimizationMode.CROSS_OBJECT)
            //
            // Practical recommendation:
            // - Start with NONE while developing/debugging to keep behavior explicit.
            // - Switch to OBJECT_BASED or CROSS_OBJECT once stable to reduce read cycle time.
            // - Use SMART (Tls13Device only) if you want PLCcom to choose an execution plan
            //   dynamically for the requested variable set.
            // =========================================================================

            // Default mode for the sample (safe baseline).
            eSymbolicReadOptimizationMode optimizationMode = eSymbolicReadOptimizationMode.NONE;

            // Enforce SMART constraint to avoid confusion:
            // SMART is ONLY valid with Tls13Device. If the user selected SMART but the device is legacy,
            // we fall back to CROSS_OBJECT (still shows optimization benefits without requiring TLS).
            if (optimizationMode == eSymbolicReadOptimizationMode.SMART && !(device instanceof Tls13Device)) {
                System.out.println("WARNING: SMART optimization requires Tls13Device (TLS). Falling back to CROSS_OBJECT.");
                optimizationMode = eSymbolicReadOptimizationMode.CROSS_OBJECT;
            }

            System.out.println("Using symbolic read optimization mode: " + optimizationMode);

            // =========================================================================
            // Build the read request (THIS is where the optimization mode is applied)
            // =========================================================================
            // The constructor now accepts an optimization mode. Higher modes generally mean:
            // - more grouping of variables
            // - fewer PLC read operations
            // - better performance for larger variable sets
            //
            // For small sets, differences may be minor; for large sets, the impact can be significant.
            ReadSymbolicRequest readRequest = new ReadSymbolicRequest(optimizationMode);

            // Which variables do you want to read?
            readRequest.addFullVariableName("myDatablock1.ByteValue");
            readRequest.addFullVariableName("myDatablock1.RealValue");
            readRequest.addFullVariableName("myDatablock1.SIntValue");
            readRequest.addFullVariableName("myDatablock1.UDIntValue");

            // Read from device
            System.out.println("Begin Read...");
            var readResult = device.readData(readRequest);

            // Evaluate results:
            // - GOOD: all values OK
            // - WARNING_PARTITIAL_BAD: some values OK, some failed (per-variable error objects)
            if (readResult.getQuality() == OperationResult.eQuality.GOOD
                    || readResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {

                for (PlcCoreVariable variable : readResult.getVariables()) {

                    // PLCcom returns per-variable errors as PlcErrorValue objects.
                    if (variable instanceof PlcErrorValue) {
                        PlcErrorValue error = (PlcErrorValue) variable;
                        System.out.println("Error: " + error.getVariableDetails().getFullVariableName()
                                + " " + error.toString());
                    } else {
                        System.out.println(variable.getVariableDetails().getFullVariableName()
                                + " Value: " + variable.getValue());
                    }
                }
            } else {
                System.out.println("Read not successful! Message: " + readResult.getMessage());
            }

            // Deregister project import progress event (good hygiene).
            device.removeOnProjectImportProgressChangedListener(this);

            // Disconnect
            device.disConnect();

        } finally {
            System.out.println("Please press ENTER to exit!");
            try {
                input.readLine();
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onProjectImportProgressChanged(int progress) {
        // Print project import progress.
        System.out.println("Import Project " + progress + "% done");
    }
}
