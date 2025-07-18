package optimized_reading_and_writing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.indian.plccom.fors7.*;
import com.indian.plccom.fors7.UnsignedDatatypes.UShort;

public class Optimized_reading_and_writing {

	public static void main(String[] args) {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		try {

			// Very important !!!!!!!!!!!!!!!!!!
			// Enter your Username + Serial here
			System.out.println("Please enter your user name");
			authentication.User(input.readLine());
			System.out.println("Please enter your user serial key");
			authentication.Serial(input.readLine());

			System.out.println("Start Connect to TCP_ISO_Device device...");
			System.out.println(System.getProperty("line.separator"));

			// create TCP_ISO_Device instance from PLCcomDevice
			PLCcomDevice Device = new TCP_ISO_Device("192.168.1.100", 0, 2, ePLCType.S7_300_400_compatibel);
			// or create MPI_Device instance from PLCcomDevice
			// PLCcomDevice Device = new MPI_Device("COM1", 0, 2, eBaudrate.b38400,
			// eSpeed.Speed187k, ePLCType.S7_300_400_compatibel);
			// or create PPI_Device instance from PLCcomDevice
			// PLCcomDevice Device = new PPI_Device("COM1", 0, 2, eBaudrate.b9600,
			// ePLCType.S7_200_compatibel);

			// set autoconnect to true and idle time till disconnect to 10000 milliseconds
			Device.setAutoConnect(true, 10000);

			ReadWriteRequestSet myRequestSet = new ReadWriteRequestSet();
			// set operation order to RequestSet
			myRequestSet.setOperationOrder(eOperationOrder.WRITE_BEVOR_READ);

			// choose your optimation mode for read and write operations
			// Explanation of the optimation mode

			// NONE:
			// No optimization, all read requests are read one after the other. Safe but
			// slow.

			// CROSS_AREAS:
			// In CROSS_AREAS mode, the requests are merged across areas. Advantage:
			// fragmented areas(e.g., data across multiple datablocks) can be read and
			// written simultaneously

			// COMBINE_AREAS:
			// In COMBINE_AREAS mode, read requests from the same areas are combined.
			// Advantage: Fast and high - performance access to data of the same areas(for
			// example, data in the same datablock)

			// AUTO:
			// PLCcom automatically selects the best optimization method. Only the minimum
			// required PLC read accesses are carried out.
			// Only in Expert edition available

			// Set read optimation mode, recommended eReadOptimizationMode.AUTO
			myRequestSet.setReadOptimizationMode(eReadOptimizationMode.AUTO);
			// Set write optimation mode, recommended eWriteOptimizationMode.CROSS_AREAS
			myRequestSet.setWriteOptimizationMode(eWriteOptimizationMode.CROSS_AREAS);

			// set the request parameters
			// in this case => read 10 Bytes from DB1 at Byte 0
			ReadDataRequest myReadDataRequest = new ReadDataRequest(eRegion.DataBlock, // Region
					1, // DB / only for datablock operations otherwise 0
					0, // read start adress
					eDataType.BYTE, // desired datatype
					10); // Quantity of reading values

			// add the read request to the requestset
			myRequestSet.addRequest(myReadDataRequest);

			// set new request parameters
			// in this case => read 78 Bytes from DB1 at address 45
			myReadDataRequest = new ReadDataRequest(eRegion.DataBlock, // Region
					1, // DB / only for datablock operations otherwise 0
					45, // read start adress
					eDataType.BYTE, // desired datatype
					78); // Quantity of reading values

			// add the read request to the requestset
			myRequestSet.addRequest(myReadDataRequest);

			// set new request parameters
			// in this case => read 20 DWord from marker at address 0
			myReadDataRequest = new ReadDataRequest(eRegion.Flags_Markers, // Region
					1, // DB / only for datablock operations otherwise 0
					0, // read start adress
					eDataType.DWORD, // desired datatype
					20); // Quantity of reading values

			// add the read request to the requestset
			myRequestSet.addRequest(myReadDataRequest);

			// declare a WriteRequest object and
			// set the request parameters
			WriteDataRequest myWriteRequest = new WriteDataRequest(eRegion.DataBlock, // Region
					100, // DB / only for datablock operations otherwise 0
					0); // write start adress
			// add writable Data here
			// in this case => write 4 bytes to DB100 at address 0
			myWriteRequest.addByte(new byte[] { 11, 12, 13, 14 });
			// add the read request to the requestset
			myRequestSet.addRequest(myWriteRequest);

			// declare a WriteRequest object and
			// set the request parameters
			myWriteRequest = new WriteDataRequest(eRegion.Flags_Markers, // Region
					0, // DB / only for datablock operations otherwise 0
					95); // write start adress
			// add writable Data here
			// in this case => write 2 Word to Marker at address 95
			myWriteRequest.addWord(new UShort[] { new UShort(1111), new UShort(2222) });
			// add the read request to the requestset
			myRequestSet.addRequest(myWriteRequest);

			// declare a WriteRequest object and
			// set the request parameters
			myWriteRequest = new WriteDataRequest(eRegion.Flags_Markers, // Region
					0, // DB / only for datablock operations otherwise 0
					105, // write start adress
					(byte) 0); // start bit
			// add writable Data here
			// in this case => set bit at address M105.0
			myWriteRequest.addBit(true);
			// add the read request to the requestset
			myRequestSet.addRequest(myWriteRequest);

			// read from device
			System.out.println("begin optimized reading and writing...");
			ReadWriteResultSet results = Device.readWriteData(myRequestSet);

			// evaluate results
			// the results of read operations...
			for (ReadDataResult res : results.getReadDataResults()) {
				if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
					int Position = 0;
					for (Object item : res.getValues()) {
						System.out.println("read Byte " + String.valueOf(Position) + " " + item.toString());
						Position++;
					}
				} else {
					System.out.println("read not successfull! Message: " + res.getMessage());
				}
			}

			// ...and the the results of write operations
			for (WriteDataResult res : results.getWriteDataResults()) {
				if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
					System.out.println("Write successfull! Message: " + res.getMessage());
				} else {
					System.out.println("Write not successfull! Message: " + res.getMessage());
				}
			}

		} catch (IOException ex) {
			System.out.println("Error " + ex.getMessage());
		} finally {
			System.out.println("Please enter any key for exit!");
			try {
				input.readLine();
			} catch (IOException ex) {
				System.out.println("Error " + ex.getMessage());
			}
		}

	}

}
