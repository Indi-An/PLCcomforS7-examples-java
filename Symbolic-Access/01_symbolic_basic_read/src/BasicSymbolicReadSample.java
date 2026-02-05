import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.indian.plccom.fors7.*;

/**
 * BasicSymbolicReadSample (Java 11)
 *
 * Minimal "hello world" for symbolic read access:
 * 1) Connect to PLC
 * 2) Create ReadSymbolicRequest
 * 3) Add variable names
 * 4) Read and print values
 */
public class BasicSymbolicReadSample implements IProjectImportProgressChangedCallback {

    public static void main(String[] args) {
        var instance = new BasicSymbolicReadSample();
        instance.program(args);
    }

    private void program(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            // License / Authentication (optional)
            authentication.User("");    // Please enter your user name
            authentication.Serial("");  // Please enter your user serial key

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

            // Build request (no optimization)
            ReadSymbolicRequest readRequest = new ReadSymbolicRequest();
            readRequest.addFullVariableName("myDatablock1.ByteValue");
            readRequest.addFullVariableName("myDatablock1.RealValue");
            readRequest.addFullVariableName("myDatablock1.SIntValue");
            readRequest.addFullVariableName("myDatablock1.UDIntValue");

            // Read
            System.out.println("Begin Read...");
            var readResult = device.readData(readRequest);

            // Print results
            if (readResult.getQuality() == OperationResult.eQuality.GOOD
                    || readResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {

                for (PlcCoreVariable variable : readResult.getVariables()) {
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
