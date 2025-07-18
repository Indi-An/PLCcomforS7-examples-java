package simple_read_example;

import com.indian.plccom.fors7.*;
import com.indian.plccom.fors7.UnsignedDatatypes.UByte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * a simple plccom example
 *
 * @author Indi.Systems GmbH
 */
public class Simple_read_example {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
        	
        	//Very important !!!!!!!!!!!!!!!!!!
            //Enter your Username + Serial here
            System.out.println("Please enter your user name");
            authentication.User(input.readLine());
            System.out.println("Please enter your user serial key");
            authentication.Serial(input.readLine());
  
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
            
            //set the request parameters
            //in this case => read 10 Bytes from DB1
            ReadDataRequest myReadDataRequest = new ReadDataRequest(eRegion.DataBlock,  //Region
                                                                    1,                  //DB / only for datablock operations otherwise 0
                                                                    0,                  //read start adress
                                                                    eDataType.BYTE,     //desired datatype
                                                                    10);                //Quantity of reading values
            
            //read from device
            System.out.println("begin Read...");
            ReadDataResult res = Device.readData(myReadDataRequest);

            //evaluate results
            if (res.getQuality() == OperationResult.eQuality.GOOD) {
                int Position = 0;
                for (Object item : res.getValues()) {
                    System.out.println("read Byte " + String.valueOf(Position) + " " + item.toString());
                    Position++;
                }
            } else {
                System.out.println("read not successfull! Message: " + res.getMessage());
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
