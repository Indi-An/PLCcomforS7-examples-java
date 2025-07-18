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
			
        	//important !!!!!!!!!!!!!!!!!!
            //please enter your Username + Serial here first
            authentication.User("Please enter here your user name");
            authentication.Serial("Please enter here your user serial key");
			
			//create a device instance
			Tls13Device tlsDevice = new Tls13Device("192.168.1.10");

			// register project import progress event
			tlsDevice.addOnProjectImportProgressChangedListener(this);

			ConnectResult connectResult = tlsDevice.connect();

			if (connectResult.getQuality() != OperationResult.eQuality.GOOD) {
				System.out.println("Connect not successfull! Quality:  " + connectResult.getQuality() + " Message: "
						+ connectResult.getMessage());
				return;
			}

			//which variables do you want to read?
			ReadSymbolicRequest readRequest = new ReadSymbolicRequest();
			readRequest.addFullVariableName("Datenbaustein_1.ByteValue");
			readRequest.addFullVariableName("Datenbaustein_1.RealValue");
			readRequest.addFullVariableName("Datenbaustein_1.SIntValue");
			readRequest.addFullVariableName("Datenbaustein_1.UDIntValue");

			// read from device
			System.out.println("begin Read...");
			var readResult = tlsDevice.readData(readRequest);

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
			tlsDevice.removeOnProjectImportProgressChangedListener(this);

			//disconnect
			tlsDevice.disConnect();

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
		//print project import progress
		System.out.println("Import Project " + progress + "% done");
	}

}
