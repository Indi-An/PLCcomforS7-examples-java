import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.indian.plccom.fors7.*;

/**
 * SymbolicReadResultHierarchyScope (Java 11)
 *
 * Demonstrates eResultHierarchyScope.Auto with a container/root request:
 * - request only the container/root (e.g., "myDatablock1")
 * - try to access child members by full variable name via Get/TryGet
 *
 * License behavior:
 * - Expert license: Auto -> RequestedAndChildMembers -> child lookup works
 * - Standard license: Auto -> RequestedOnly -> child lookup returns false/null
 */
public class SymbolicReadHierarchyScope implements IProjectImportProgressChangedCallback {

    public static void main(String[] args) {
        var instance = new SymbolicReadHierarchyScope();
        instance.program(args);
    }

    private void program(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            // License / Authentication (optional)
            authentication.User("");
            authentication.Serial("");

            // Device selection (TLS vs Legacy)
            SymbolicDevice device = new Tls13Device("192.168.1.100");
            // SymbolicDevice device = new LegacySymbolicDevice("192.168.1.100");

            device.addOnProjectImportProgressChangedListener(this);

            // Connect
            ConnectResult connectResult = device.connect();
            if (connectResult.getQuality() != OperationResult.eQuality.GOOD) {
                System.out.println("Connect not successful! Quality: " + connectResult.getQuality()
                        + " Message: " + connectResult.getMessage());
                return;
            }

            // =========================================================================
            // Result Hierarchy Scope (AUTO)
            // =========================================================================
            //
            // Goal:
            // - We request only the container/root: "myDatablock1"
            // - Then we try to access child members by full variable name, e.g. "myDatablock1.ByteValue".
            //
            // IMPORTANT LICENSE BEHAVIOR:
            //
            // 1) Auto (recommended default):
            //    - With an Expert license:
            //        Auto is resolved to RequestedAndChildMembers -> child lookup works.
            //    - With a Standard license:
            //        Auto is resolved to RequestedOnly -> child lookup is NOT available.
            //        In this case TryGet(...) returns false (and Get(...) returns null).
            //
            // 2) Explicit RequestedAndChildMembers:
            //    - If you explicitly request RequestedAndChildMembers without an appropriate license,
            //      readData(...) throws an exception (license restriction).
            //
            // IMPORTANT TECHNICAL NOTE:
            // - The scope does NOT change what is read from the PLC.
            // - It only changes what the result may expose via Get/TryGet and internal indexing.
            // =========================================================================

            ReadSymbolicRequest readRequest = new ReadSymbolicRequest(eResultHierarchyScope.Auto);

            // Request only the container/root
            readRequest.addFullVariableName("myDatablock1");

            System.out.println("Begin Read...");
            var readResult = device.readData(readRequest);

            if (readResult.getQuality() == OperationResult.eQuality.GOOD
                    || readResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {

                for (PlcCoreVariable variable : readResult.getVariables()) {
                    if (variable instanceof PlcErrorValue) {
                        PlcErrorValue error = (PlcErrorValue) variable;
                        System.out.println("Error: " + error.getVariableDetails().getFullVariableName()
                                + " " + error.toString());
                    } else {
                        System.out.println("Root returned: " + variable.getVariableDetails().getFullVariableName());
                    }
                }

                System.out.println("TryGet child members:");

                // NOTE: Adjust these calls to your Java API naming if needed.
                // The intention is identical to the C# sample.
                PlcCoreVariable v1 = readResult.getVariableByFullVariableName("myDatablock1.ByteValue");
                System.out.println(v1 != null ? "myDatablock1.ByteValue Value: " + v1.getValue()
                                              : "Child lookup failed: myDatablock1.ByteValue");

                PlcCoreVariable v2 = readResult.getVariableByFullVariableName("myDatablock1.RealValue");
                System.out.println(v2 != null ? "myDatablock1.RealValue Value: " + v2.getValue()
                                              : "Child lookup failed: myDatablock1.RealValue");

            } else {
                System.out.println("Read not successful! Message: " + readResult.getMessage());
            }

            device.removeOnProjectImportProgressChangedListener(this);
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
        System.out.println("Import Project " + progress + "% done");
    }
}
