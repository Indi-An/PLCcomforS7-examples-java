import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.indian.plccom.fors7.*;

public class SymbolicWriteSample implements IProjectImportProgressChangedCallback {

	public static void main(String[] args) {

		var instance = new SymbolicWriteSample();
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

			List<PlcCoreVariable> writeVariables = new ArrayList<PlcCoreVariable>();

			/*
			 * Before you can write, you need the imported variable. Either you have
			 * determined it by a read operation or you have PLCCom output the empty
			 * variable (without values)
			 */
			var variableBody = getEmptyVariableBody(device, "myDatablock1.ByteValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(1);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(device, "myDatablock1.RealValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(123.456f);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(device, "myDatablock1.SIntValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(-123);
				writeVariables.add(variableBody);
			}

			variableBody = getEmptyVariableBody(device, "myDatablock1.UDIntValue");
			// Set the value and add the variable to the write list
			if (variableBody != null) {
				variableBody.setValue(123456);
				writeVariables.add(variableBody);
			}

			// create a write request
			WriteSymbolicRequest writeRequest = new WriteSymbolicRequest(writeVariables);

			// write to device
			System.out.println("write...");
			var writeResult = device.writeData(writeRequest);

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

	private PlcCoreVariable getEmptyVariableBody(SymbolicDevice device, String fullVariableName) {
		try {
			return device.getEmptyVariableBody(fullVariableName);
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
