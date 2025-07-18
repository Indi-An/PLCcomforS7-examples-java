package simple_dataserver;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Calendar;

import com.indian.plccom.fors7.*;

public class Simple_Dataserver
		implements IConnectionStateChangeCallback, IReadDataResultChangeCallback, IIncomingLogEntryCallback {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Simple_Dataserver frame = new Simple_Dataserver();
					frame.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void run() {

		PLCComDataServer myDataServer = null;
		PLCcomDevice Device = null;
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			// Very important !!!!!!!!!!!!!!!!!!
			// Enter your Username + Serial here
			System.out.println("Please enter your user name");
			authentication.User(input.readLine());
			System.out.println("Please enter your user serial key");
			authentication.Serial(input.readLine());

			System.out.println("Start Connect to TCP_ISO_Device device...");
			System.out.println(System.getProperty("line.separator"));

			// create TCP_ISO_Device instance from PLCcomDevice
			Device = new TCP_ISO_Device("192.168.1.100", 0, 2, ePLCType.S7_300_400_compatibel);
			// or create MPI_Device instance from PLCcomDevice
			// Device = new MPI_Device("COM1", 0, 2, eBaudrate.b38400, eSpeed.Speed187k,
			// ePLCType.S7_300_400_compatibel);
			// or create PPI_Device instance from PLCcomDevice
			// Device = new PPI_Device("COM1", 0, 2, eBaudrate.b9600,
			// ePLCType.S7_200_compatibel);

			// set autoconnect to true and idle time till disconnect to 10000 milliseconds
			Device.setAutoConnect(true, 10000);

			// Create an instance depending on the device type
			System.out.println("Create DataServer PLCDataServerTCP1...");
			myDataServer = new PLCComDataServer_TCP("PLCDataServerTCP1", (TCP_ISO_Device) Device, 500);

			// register incoming events
			// register Connection state change event
			myDataServer.connectionStateChangeNotifier = new ConnectionStateChangeNotifier(this);

			// register incoming log event
			myDataServer.incomingLogEntryEventNotifier = new IncomingLogEntryEventNotifier(this);

			// register change ReadDataResult event
			myDataServer.readDataResultChangeNotifier = new ReadDataResultChangeNotifier(this);

			// define new request
			System.out.println("Create new Request Read 4 Bytes from DB1 at address 0 ...");
			ReadDataRequest RequestItem1 = new ReadDataRequest(eRegion.DataBlock, // Region
					1, // datablock
					0, // startAdress
					eDataType.BYTE, // target data type
					4); // Quantity

			// add new request to plccom data server
			myDataServer.addReadDataRequest(RequestItem1);

			// define new request
			System.out.println("Create new Request Read 10 DWords from Flags_Markers at address 4 ...");
			ReadDataRequest RequestItem2 = new ReadDataRequest(eRegion.Flags_Markers, // Region
					0, // datablock
					4, // startAdress
					eDataType.DWORD, // target data type
					10); // Quantity

			// add new request to plccom data server
			myDataServer.addReadDataRequest(RequestItem2);

			// add on or more Loggingkonnektoren with logging and writing of a data image
			// into filesystem or database
			// in this case create a new FileSystemConnector instance
			LoggingConnector con = new FileSystemConnector(new File(".").getAbsolutePath(), // Target folder
					"FileSystemConnector1", // unique connector name
					';', // text separator recommendation ';'
					true, // activate progressive logging
					true, // activate image writing
					eImageOutputFormat.dat, // output format .dat or .xml
					10, // restrict the maximum number of files. When the value is exceeded the old
						// files are automatically deleted. -1 = Disabled.
					24, // restrict the maximum age of files. When the value is exceeded the old files
						// are automatically deleted. -1 = Disabled.
					30, // You can restrict the maximum size of files. When the value is exceeded the
						// old files are automatically deleted. -1 = Disabled.
					""); // If you enter an encryption password, the data is stored in encrypted form.
							// You can read the data using the supplied decryption tool again.

			// add Connector to Dataserver
			myDataServer.addOrReplaceLoggingConnector(con);

			// start PLCcom data server
			myDataServer.startServer();
			input.readLine();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			// stop PLCcom data server
			myDataServer.stopServer();
		}
	}

	@Override
	public void On_ReadDataResult(ReadDataResult res) {

		System.out.println(Calendar.getInstance().getTime().toString() + " ItemKey: " + res.getItemkey() + " Quality: "
				+ res.getQuality().toString() + " Values: " + ArrayToString(res.getValues()));
	}

	private String ArrayToString(Object[] value) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			try {
				sb.append(String.valueOf(value[i]));
			} catch (Exception ex) {
				sb.append(String.valueOf(ex.getMessage()));
			}
			if (i < value.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	@Override
	public void OnIncomingLogEntry(LogEntry[] value) {
		// incoming OnIncomingLogEntry event
		// loading Logentrys in ListView
		// write LogEntry into listview
		for (LogEntry le : value) {
			if (le.getLogLevel().equals(eLogLevel.Error))
				System.out.println(le.toString());
		}
	}

	@Override
	public void On_ConnectionStateChange(eConnectionState value) {
		System.out.println("Connectionstate changed to: " + value.toString());
	}
}
