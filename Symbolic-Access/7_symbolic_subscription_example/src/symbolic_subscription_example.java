
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.indian.plccom.fors7.*;
import com.indian.plccom.fors7.PlcSubscription.SubscriptionVariableChangeHandler;
import com.indian.plccom.fors7.UnsignedDatatypes.UShort;

public class symbolic_subscription_example
		implements IProjectImportProgressChangedCallback, SubscriptionVariableChangeHandler {

	public static void main(String[] args) {

		var instance = new symbolic_subscription_example();
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
			Tls13Device tlsDevice = new Tls13Device("192.168.1.10");

			try {

				// register project import progress event
				tlsDevice.addOnProjectImportProgressChangedListener(this);

				ConnectResult connectResult = tlsDevice.connect();

				if (connectResult.getQuality() == OperationResult.eQuality.GOOD) {
					System.out.println("Connected to PLC " + tlsDevice.getIPAdressOrHost());
				} else {
					System.out.println("Connect not successfull! Quality:  " + connectResult.getQuality() + " Message: "
							+ connectResult.getMessage());
					return;
				}
			} finally {
				// deregister project import progress event
				tlsDevice.removeOnProjectImportProgressChangedListener(this);
			}

			CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest("TestSubscription",
					new UShort(300));

			// Which variables do you want to subcribe?
			createSubscriptionRequest.addFullVariableName("Datenbaustein_1.ByteValue");
			createSubscriptionRequest.addFullVariableName("Datenbaustein_1.RealValue");
			createSubscriptionRequest.addFullVariableName("Datenbaustein_1.SIntValue");
			createSubscriptionRequest.addFullVariableName("Datenbaustein_1.UDIntValue");

			// create subscription
			CreateSubscriptionResult createSubResult = tlsDevice.createSubscription(createSubscriptionRequest);

			if (createSubResult.getQuality() == OperationResult.eQuality.GOOD) {

				PlcSubscription subscription = createSubResult.getSubscription();

				if (subscription != null) {
					System.out.println("Subscription " + subscription.getSubscriptionName() + " created!");
					subscription.addVariableChangeHandler(this);

					// register subscription
					RegisterSubscriptionResult registerSubscriptionResult = tlsDevice
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
					OperationResult dropSubcriptionResult = tlsDevice.dropSubscription(subscription);
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
		// print project import progress
		System.out.println("Import Project " + progress + "% done");
	}

	@Override
	public void onChangeVariable(Object sender, SubscriptionChangeVariableEventArgs e) {
		if (sender instanceof PlcSubscription && sender != null) {
			PlcSubscription subscription = (PlcSubscription) sender;

			System.out.println(
					"Incoming variable change notification for subscription: " + subscription.getSubscriptionName());
			for (var variable : e.getVariables()) {
				System.out.println("Variable: " + variable.getVariableDetails().getFullVariableName() + " Value: "
						+ variable.getValue());
			}

		}

	}

}
