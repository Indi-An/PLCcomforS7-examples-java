import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.indian.plccom.fors7.*;

public class symbolic_read_example implements IProjectImportProgressChangedCallback {

	public static void main(String[] args) {

		var instance = new symbolic_read_example();
		instance.program();

	}

	private void program() {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		try {

			// Very important !!!!!!!!!!!!!!!!!!
			// Enter your Username + Serial here! Please note: Without a license key (empty
			// fields), the runtime is limited to 10 minutes
			authentication.User("");
			authentication.Serial("");

			// create a Tls13Device instance for access to modern PLCs with TLS 1.3 support
			SymbolicDevice device = new Tls13Device("192.168.1.100");
			// or create a LegacySymbolicDevice instance for a legacy access to older PLCs
			//SymbolicDevice device = new LegacySymbolicDevice("192.168.1.100");

			// register project import progress event
			device.addOnProjectImportProgressChangedListener(this);

			ConnectResult connectResult = device.connect();

			if (connectResult.getQuality() != OperationResult.eQuality.GOOD) {
				System.out.println("Connect not successfull! Quality:  " + connectResult.getQuality() + " Message: "
						+ connectResult.getMessage());
				return;
			}

			// which variables do you want to read?
			ReadSymbolicRequest readRequest = new ReadSymbolicRequest();
			readRequest.addFullVariableName("myDatablock1.ByteValue");
			readRequest.addFullVariableName("myDatablock1.RealValue");
			readRequest.addFullVariableName("myDatablock1.SIntValue");
			readRequest.addFullVariableName("myDatablock1.UDIntValue");

			// read from device
			System.out.println("begin Read...");
			var readResult = device.readData(readRequest);

			// evaluate results
			if (readResult.getQuality() == OperationResult.eQuality.GOOD
					|| readResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {
				for (PlcCoreVariable variable : readResult.getVariables()) {
					if (variable instanceof PlcErrorValue) {
						PlcErrorValue error = (PlcErrorValue) variable;
						System.out.println(
								"Error: " + error.getVariableDetails().getFullVariableName() + " " + error.toString());
					} else {
						System.out.println(
								variable.getVariableDetails().getFullVariableName() + " Value: " + variable.getValue());
					}
				}
			} else {
				System.out.println("read not successfull! Message: " + readResult.getMessage());
			}

			// deregister project import progress event
			device.removeOnProjectImportProgressChangedListener(this);

			// disconnect
			device.disConnect();

		} finally {
			System.out.println("Please enter any key for exit!");
			try {
				input.readLine();
			} catch (IOException ex) {
				System.out.println("Error " + ex.getMessage());
			}
		}
	}

	@Override
	public void onProjectImportProgressChanged(int progress) {
		// print project import progress
		System.out.println("Import Project " + progress + "% done");
	}

}
