package gsm_modem;

import java.util.ArrayList;
import java.util.Enumeration;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.helper.CommPortIdentifier;
import org.smslib.modem.SerialModemGateway;

/**
 *
 * @author arsalan
 */
public class GsmModem {

    public GsmModem() {
        this.bitrate = 115200;
        this.modem = "MyModem";
        this.modemPin = "0000";
        this.commPorts = new ArrayList<>();
    }

    /**
     * To send Sms to desired number
     *
     * @param phnNum
     * @param message
     * @return
     */
    public boolean sendSms(String phnNum, String message) {
        phoneNumber = phnNum;
        msgBody = message;
        boolean status = false;
        try {
            status = startSendingSms();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    /**
     * Method to start sending messages
     *
     * @return
     */
    private boolean startSendingSms() {
        boolean status = false;
        if (Service.getInstance().getGateways().isEmpty()) {
            try {
                setPort();
                outboundNotification = new OutboundNotification();
                gateway = new SerialModemGateway("modem." + port.toLowerCase(), port, bitrate, modem, "");
                gateway.setInbound(true);
                gateway.setOutbound(true);
                gateway.setSimPin(modemPin);
                Service.getInstance().setOutboundMessageNotification(outboundNotification);
                Service.getInstance().addGateway(gateway);
                try {
                    Service.getInstance().startService();
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                }

                OutboundMessage msg = new OutboundMessage(phoneNumber, msgBody);
                Service.getInstance().sendMessage(msg);
                if (msg.getMessageStatus().toString().equals("FAILED") || msg.getMessageStatus().toString().equals("UNSENT")) {
                    System.out.println("SMS " + msg.getMessageStatus().toString());
                } else {
                    status = true;
                    System.out.println("SMS " + msg.getMessageStatus().toString());
                }
                Service.getInstance().stopService();
                Service.getInstance().removeGateway(gateway);
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
            }
        }
        return status;
    }

    /**
     * Check weather the Modem is connected to port or not
     *
     * @return Modem Connection Status
     */
    public boolean isConnected() {
        boolean check = false;
        commPorts.clear();
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getName().contains("COM")) {
                commPorts.add(portId.getName());
            }
        }
        if (commPorts.size() > 0) {
            check = true;
        }

        return check;
    }

    /**
     * Automatically set the COM port for attached phone
     *
     * @return
     */
    public boolean setPort() {
        commPorts.clear();
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getName().contains("COM")) {
                commPorts.add(portId.getName());
            }
        }
        if (commPorts.size() > 1) {
            port = commPorts.get(commPorts.size() - 1);
            return true;
        } else {
            return false;
        }
    }
//    public static void main(String[] args) throws Exception {
//        // TODO code application logic here
//     
//        BufferedReader inp = new BufferedReader(new FileReader("inp.txt"));
//     GsmModem gsm = new GsmModem();
//     
//     gsm.setPort();
//     String num = "";
//   //  while((num = inp.readLine())!=null){
//         
//      //   System.out.println(num);
//    // 
//       boolean c =   gsm.sendSms(num, "The inductions of DevSoc are being held tomorrow in S1 in the 3rd and 4th slot (10 AM to 12 PM). All those who have filled the induction form are requested to be there on time. ");
//    if(c){
//        System.out.println("found and sent");
//    } else{
//         System.out.println("No Modem is Found");
//     }
//     }
    //  }
    private String port;
    private final int bitrate;
    private final String modem;
    private final String modemPin;
    private String phoneNumber;
    private String msgBody;
    private Enumeration portList;
    private CommPortIdentifier portId;
    private final ArrayList<String> commPorts;
    private OutboundNotification outboundNotification;
    private SerialModemGateway gateway;
}
