import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.indian.plccom.fors7.*;

public class symbolic_write_example implements IProjectImportProgressChangedCallback {

	public static void main(String[] args) {

		var instance = new symbolic_write_example();
		instance.program();

	}

	private void program() {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		try {

			// important !!!!!!!!!!!!!!!!!!
			// please enter your Username + Serial here first
			authentication.User("Please enter here your user name");
			authentication.Serial("Please enter here your user serial key");

			// create a new device instance
			Tls13Device tlsDevice = new Tls13Device("192.168.1.100");

			// register project import progress event
			tlsDevice.addOnProjectImportProgressChangedListener(this);

			ConnectResult connectResult = tlsDevice.connect();

			if (connectResult.getQuality() != OperationResult.eQuality.GOOD) {
				System.out.println("Connect not successfull! Quality:  " + connectResult.getQuality() + " Message: "
						+ connectResult.getMessage());
				return;
			}

			List<PlcCoreVariable> writeVariables = new ArrayList<PlcCoreVariable>();

			/*
			 * Before you can write, you need the imported variable. Either you have
			 * determined it by a read operation or you have PLCCom output the empty
			 * variable (without values)
			 */
			var variableBody = getEmptyVariableBody(tlsDevice, "Datenbaustein_1.ByteValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(1);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(tlsDevice, "Datenbaustein_1.RealValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(123.456f);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(tlsDevice, "Datenbaustein_1.SIntValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(-123);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(tlsDevice, "Datenbaustein_1.UDIntValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(123456);
				writeVariables.add(variableBody);
			}

			// create a write request
			WriteSymbolicRequest writeRequest = new WriteSymbolicRequest(writeVariables);

			// write to device
			System.out.println("write...");
			var writeResult = tlsDevice.writeData(writeRequest);

			// evaluate results
			if (writeResult.getQuality() == OperationResult.eQuality.GOOD)
				System.out.println("write successfull!");
			else if (writeResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {
				System.out.println("write partialy not successfull!");
				for (var singleResult : writeResult.getWriteOperationResults()) {
					if (singleResult.isQualityGood())
						System.out.println("write " + singleResult.getFullVariableName() + " successfull!");
					else
						System.out.println("write " + singleResult.getFullVariableName() + " not successfull!");
				}
			} else {
				System.out.println("write not successfull! Message: " + writeResult.getMessage());
			}

			// deregister project import progress event
			tlsDevice.removeOnProjectImportProgressChangedListener(this);

			// disconnect
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

	private PlcCoreVariable getEmptyVariableBody(Tls13Device tlsDevice, String fullVariableName) {
		try {
			return tlsDevice.getEmptyVariableBody(fullVariableName);
		} catch (Exception ex) {
			System.out.println("cannot found variable " + fullVariableName);
			return null; // return null if error occur
		}

	}

	@Override
	public void onProjectImportProgressChanged(int progress) {
		// print project import progress
		System.out.println("Import Project " + progress + "% done");
	}

}
