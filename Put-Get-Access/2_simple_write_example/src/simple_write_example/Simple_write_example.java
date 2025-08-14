package simple_write_example;

import com.indian.plccom.fors7.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * a simple plccom example
 *
 * @author Indi.Systems GmbH
 */
public class Simple_write_example {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {

			// Very important !!!!!!!!!!!!!!!!!!
			// Enter your Username + Serial here! Please note: Without a license key (empty
			// fields), the runtime is limited to 10 minutes
			authentication.User("");
			authentication.Serial("");

            System.out.println("Start Connect to TCP_ISO_Device device...");
            System.out.println(System.getProperty("line.separator"));

            //create TCP_ISO_Device instance from PLCcomDevice
            PLCcomDevice Device = new TCP_ISO_Device("192.168.1.100", 0, 2, ePLCType.S7_300_400_compatibel);
            //or create MPI_Device instance from PLCcomDevice
            //PLCcomDevice Device = new MPI_Device("COM1", 0, 2, eBaudrate.b38400, eSpeed.Speed187k, ePLCType.S7_300_400_compatibel);
            //or create PPI_Device instance from PLCcomDevice
            //PLCcomDevice Device = new PPI_Device("COM1", 0, 2, eBaudrate.b9600, ePLCType.S7_200_compatibel);

            //set autoconnect to true and idle time till disconnect to 10000 milliseconds
            Device.setAutoConnect(true, 10000);

            //declare a WriteRequest object and
            //set the request parameters
            WriteDataRequest myWriteRequest = new WriteDataRequest(eRegion.DataBlock, //Region
                                                                    100, //DB / only for datablock operations otherwise 0
                                                                    0);               //write start adress

            //add writable Data here
            //in  this case => write 4 bytes in DB100
            myWriteRequest.addByte(new byte[]{11, 12, 13, 14});

            //write
            System.out.println("begin write...");
            WriteDataResult res = Device.writeData(myWriteRequest);

            //evaluate results
            if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
                System.out.println("Write successfull! Message: " + res.getMessage());
            } else {
                System.out.println("Write not successfull! Message: " + res.getMessage());
            }
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
