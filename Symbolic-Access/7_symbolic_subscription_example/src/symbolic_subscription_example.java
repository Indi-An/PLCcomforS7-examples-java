
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.indian.plccom.fors7.*;
import com.indian.plccom.fors7.UnsignedDatatypes.UShort;

public class symbolic_subscription_example
		implements IProjectImportProgressChangedCallback, SubscriptionVariableChangeListener {

	public static void main(String[] args) {

		var instance = new symbolic_subscription_example();
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


			try {

				// register project import progress event
				device.addOnProjectImportProgressChangedListener(this);

				ConnectResult connectResult = device.connect();

				if (connectResult.getQuality() == OperationResult.eQuality.GOOD) {
					System.out.println("Connected to PLC " + device.getIPAdressOrHost());
				} else {
					System.out.println("Connect not successfull! Quality:  " + connectResult.getQuality() + " Message: "
							+ connectResult.getMessage());
					return;
				}
			} finally {
				// deregister project import progress event
				device.removeOnProjectImportProgressChangedListener(this);
			}

			CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest("TestSubscription",
					new UShort(300));

			// Which variables do you want to subcribe?
			createSubscriptionRequest.addFullVariableName("myDatablock1.ByteValue");
			createSubscriptionRequest.addFullVariableName("myDatablock1.RealValue");
			createSubscriptionRequest.addFullVariableName("myDatablock1.SIntValue");
			createSubscriptionRequest.addFullVariableName("myDatablock1.UDIntValue");

			// create subscription
			CreateSubscriptionResult createSubResult = device.createSubscription(createSubscriptionRequest);

			if (createSubResult.getQuality() == OperationResult.eQuality.GOOD) {

				PlcSubscription subscription = createSubResult.getSubscription();

				if (subscription != null) {
					System.out.println("Subscription " + subscription.getSubscriptionName() + " created!");
					subscription.addSubscriptionChangeListener(this);

					// register subscription
					RegisterSubscriptionResult registerSubscriptionResult = device
							.registerSubscription(new RegisterSubscriptionRequest(subscription));

					if (registerSubscriptionResult.getQuality() == OperationResult.eQuality.GOOD) {
						System.out.println("Subscription " + subscription.getSubscriptionName() + " registered!");
					} else {
						System.out
								.println("Error while register subscription! Message: " + createSubResult.getMessage());
					}

					System.out.println("Please enter any key for exit!");
					try {
						input.readLine();
					} catch (IOException ex) {
						System.out.println("Error " + ex.getMessage());
					}

					// Drop Subscription
					OperationResult dropSubcriptionResult = device.dropSubscription(subscription);
					if (dropSubcriptionResult.getQuality() == OperationResult.eQuality.GOOD) {
						System.out.println("Subscription " + subscription.getSubscriptionName() + " dropped!");
					} else {
						System.out.println("Error while dropping subscription! Message: " + dropSubcriptionResult);
					}

				} else {
					System.out.println("Error while creating subscription! Message: " + createSubResult.getMessage());
				}
			} else {
				System.out.println("Error while creating subscription! Message: " + createSubResult.getMessage());
			}

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

	@Override
	public void onVariableChange(Object sender, List<PlcCoreVariable> variables) {
	    if (!(sender instanceof PlcSubscription)) {
	        return; // invalid sender -> do nothing
	    }
	    PlcSubscription subscription = (PlcSubscription) sender;

	    System.out.println("Incoming variable change notification for subscription: "
	            + subscription.getSubscriptionName());

	    for (PlcCoreVariable variable : variables) {
	        handleVariable(variable); // process recursively
	    }
	}

	/** Processes a variable or struct of any depth. */
	private void handleVariable(PlcCoreVariable variable) {
	    if (variable == null) return;

	    if (variable.getVariableDetails().isStruct()) {
	    	System.out.println(
		            "Variable: " + variable.getVariableDetails().getFullVariableName());
	        PlcStructure struct = (PlcStructure) variable;
	        for (PlcCoreVariable child : struct.getAllChilds()) {
	            handleVariable(child); // Recursion for childknodes
	        }
	    } else {
	        String valueStr = valueToString(variable.getValue());
	        System.out.println(
	            "Variable: " + variable.getVariableDetails().getFullVariableName()
	            + " Value: " + valueStr
	        );
	    }
	}


    /**
     * Returns the value as a string.
     * Supports arrays (primitive + object arrays) and arbitrary objects.
     */
	    private static String valueToString(Object value) {
	        if (value == null) {
	            return "null";
	        }

	        Class<?> clazz = value.getClass();
	        if (clazz.isArray()) {
	            if (value instanceof int[]) {
	                return Arrays.toString((int[]) value);
	            } else if (value instanceof long[]) {
	                return Arrays.toString((long[]) value);
	            } else if (value instanceof double[]) {
	                return Arrays.toString((double[]) value);
	            } else if (value instanceof float[]) {
	                return Arrays.toString((float[]) value);
	            } else if (value instanceof boolean[]) {
	                return Arrays.toString((boolean[]) value);
	            } else if (value instanceof byte[]) {
	                return Arrays.toString((byte[]) value);
	            } else if (value instanceof short[]) {
	                return Arrays.toString((short[]) value);
	            } else if (value instanceof char[]) {
	                return Arrays.toString((char[]) value);
	            } else {
	            	// Object arrays or nested arrays
	                return Arrays.deepToString((Object[]) value);
	            }
	        }

	        return value.toString();
	    }


}
