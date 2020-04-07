package com.example.animusnetworkmonitor;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

//import snmp.commands.HexStrConver;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;

public class SNMPManager implements Runnable {

Snmp snmp = null;
static String address = ""; //keep empty to auto detect IP
static ArrayList<String> ipAddress=new ArrayList<String>();
public MainActivity m;
/**
* Constructor
* @param add
*/
public SNMPManager(String add)
{
	address = add;
}
public SNMPManager(String add,MainActivity m)
{
address = add;
this.m=m;
}
//*******************COPY THESE FUNCTION*****************************
	public static String trial(){

		return address;
	}

//**********Function to get Current IP of System**********
public String getAllIp() throws Exception
{
	String host;
    InetAddress IP = InetAddress.getLocalHost();

    if(address.isEmpty())
	{
		address=IP.getHostAddress();
		host="return line 54";
		return host;
	}
    System.out.println("IP of my system is := "+address);

        int first_point=(address.indexOf('.', 0));
        int second_point=(address.indexOf('.', first_point+1));
        int third_point=(address.indexOf('.', second_point+1));
        host=(String) address.subSequence(0, third_point);
    
    //System.out.println(host+" -host");
	return host;

    //System.out.println(getARPTable(ARP_GET_IP_HW ));
}

//**********Function to get All IP on network**********
public ArrayList<String> checkHosts(String subnet) throws UnknownHostException, IOException {

	ArrayList<String> ip_List = new ArrayList<>();
	   int timeout=100;
	   for (int i=1;i<10;i++){
		   //System.out.println(i+" line 67 "+address);
	       String host=subnet + "." + i;
	       if (InetAddress.getByName(host).isReachable(timeout)){
	           System.out.println(host + " is reachable");
	           ipAddress.add(host);
	           ip_List.add(host);
	       }
	   }
	   return ip_List;
	}


/*
//**********To Get ARP Table**********
private static final String ARP_GET_IP_HW = "arp -a";

@SuppressWarnings("resource")
public static String getARPTable(String cmd) throws IOException {
           Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
    }
*/

//**********************UNTIL THIS LINE*********************************

public /*static*/ String snmp_main() throws Exception {
/**
* Port 161 is used for Read and Other operations
* Port 162 is used for the trap generation
*/
//	String host= getAllIp(); //prints host and calls checkHost(host) to get all IP on Local Network
//	checkHosts(host);
//	ping ping_thread = new ping(ipAddress);
//	ping_thread.run();
	
	SNMPManager client = new SNMPManager("udp:"+address+"/161");
	client.start();
	//System.out.println("\n");
	String sysDescr11="";
	try {
		 sysDescr11 = client.getAsString(new OID("1.3.6.1.2.1.1.1.0"));
	}catch (Error|Exception e){
		m.print(" for: "+address+"SNMPManager126 :"+e);
		return "";
	}
	final String toprint=sysDescr11+"sysDesc\n";
	//System.out.println(sysDescr11+"sysDesc\n");
	m.runOnUiThread(new Runnable() {

		@Override
		public void run() {

			// Stuff that updates the UI
			//m.print(toprint);
		}
	});
	return sysDescr11;
/*
	//**********To Get IfIndex Number**********
		String ifNumber = client.getAsString(new OID(".1.3.6.1.2.1.2.1.0"));
		int IfNum = Integer.parseInt(ifNumber);
		//System.out.println(IfNum + " -IfNum\n");
		Integer IfInd = 0;
		String aa="0";
		for (int a = 1; a < IfNum; a++) 
		{	
			aa = String.valueOf(a);
			String IfInOctet = client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.10." + aa));
			//System.out.println(IfInOctet + " -IfInOctet "+aa+" -aa");
			//System.out.println(Integer.parseInt(IfInOctet));
			
			if (!IfInOctet.equals("0")) 
			{
				IfInd = a;
				//System.out.println(IfInOctet + " -IfInOctet\n");
				break;
			}
		}
		//**********To Get dCPUload**********
		calcCPULoad ccl = new calcCPULoad(address);
		
		//**********To Get dMemUtil**********
		ramdiskUtil ru = new ramdiskUtil(address);
		
		//**********Bandwidth Utilization********** 
		bandwidthUtil_calc c = new bandwidthUtil_calc(IfInd, address,2000); //(index for OID,ip_address,polling_interval)
		
	//while(true)
	{
		//ping_thread.run();
		System.out.println("\n");
		
		//**********To Get dCPUload**********
		ccl.run();
		System.out.println("\n");
		
		//**********To Get dMemUtil**********
		ru.run();
		System.out.println("\n");
		
		//**********Bandwidth Utilization********** 
		c.run();
		System.out.println("\n");

	}
*/

}

/**
* Start the Snmp session. If you forget the listen() method you will not
* get any answers because the communication is asynchronous
* and the listen() method listens for answers.
* @throws IOException
*/
public void start() throws IOException {
TransportMapping transport = new DefaultUdpTransportMapping();
snmp = new Snmp(transport);
// Do not forget this line!
transport.listen();
}

/**
* Method which takes a single OID and returns the response from the agent as a String.
* @param oid
* @return
* @throws IOException
*/
public String getAsString(OID oid) throws IOException {
ResponseEvent event = get(new OID[] { oid });
return event.getResponse().get(0).getVariable().toString();
}

/**
* This method is capable of handling multiple OIDs
* @param oids
* @return
* @throws IOException
*/
public ResponseEvent get(OID oids[]) throws IOException {
PDU pdu = new PDU();
for (OID oid : oids) {
pdu.add(new VariableBinding(oid));
}
pdu.setType(PDU.GET);
ResponseEvent event = snmp.send(pdu, getTarget(), null);
if(event != null) {
return event;
}
throw new RuntimeException("GET timed out");
}

/**
* This method returns a Target, which contains information about
* where the data should be fetched and how.
* @return
*/
private Target getTarget() {
Address targetAddress = GenericAddress.parse(address);
CommunityTarget target = new CommunityTarget();
target.setCommunity(new OctetString("public"));
target.setAddress(targetAddress);
target.setRetries(2);
target.setTimeout(1500);
target.setVersion(SnmpConstants.version2c);
return target;
}


	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		try{snmp_main();}
		catch (Exception|Error e)
		{
			m.print("getDataNetwork.java-111 :"+e);
			e.printStackTrace();
		}
	}
}//main end

//**********To Get dCPUload**********
class calcCPULoad implements Runnable {

	Thread cpu_thread;
	static String ip;
	public void NewThread() {
		cpu_thread = new Thread(this, "Ping Thread");
		System.out.println("cpu_thread:" + cpu_thread);
		cpu_thread.start();
	}
	public calcCPULoad(String address) {
		ip=address;
	}
	
	
	@Override
	public void run() {

		try {
			SNMPManager client_1 = new SNMPManager(ip);
			client_1.start();
			//System.out.println(" line 222 IP: "+ip);
			//String  hrProcessorLoad = client_1.getAsString(new OID(".1.3.6.1.2.1.25.3.3.1.2.7"));
			//System.out.println(hrProcessorLoad+" -hrProcessorLoad");
			
			Double dCPUUtil=0.0;
			int processorLoadIndex=0;
			String cpu_perf="";
			for(int i=0;i<=10;i++) 
			{
					cpu_perf = client_1.getAsString(new OID(".1.3.6.1.2.1.25.3.3.1.2."+i));
					if(cpu_perf!="Null") 
					{
						//System.out.println(cpu_perf+"-cpu_perf");
						dCPUUtil+= Double.parseDouble(cpu_perf);
						processorLoadIndex++;
					}
			}
			//System.out.println(dCPUUtil+" -Double dcpu_perf\n"+processorLoadIndex+" -ProcessorLoadIndex");
			if(dCPUUtil/processorLoadIndex<100)
				System.out.println(dCPUUtil/processorLoadIndex+"% -****CPU_Utilization****");
			else
				System.out.println("100% -****CPU_Utilization****");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(" line 240");
			e.printStackTrace();
		}
	}
}

//**********To Get DiskRamMemoruy Utilization**********
class ramdiskUtil implements Runnable {

	Thread ramdisk_thread;
	static String ip,hrStorageDesc;
	static int ramIndex;
	static String strMemSize,strMemUsed,strStorageAlloc;
	public void NewThread() {
		ramdisk_thread = new Thread(this, "Ping Thread");
		System.out.println("cpu_thread:" + ramdisk_thread);
		ramdisk_thread.start();
	}
	public ramdiskUtil(String address) {
		ip=address;
	}
	
	
	@Override
	public void run() {

		try {
			SNMPManager client_1 = new SNMPManager(ip);
			client_1.start();
			//System.out.println(" line 222 IP: "+ip);
			
			Double dDiskMemSize=0.0,dDiskMemUsed=0.0,dRAMMemSize=0.0,dRAMMemUsed=0.0;
			int memUtilIndex=0;
			String hrStorageIndex="";
			for(int i=0;i<=10;i++) 
			{												
				hrStorageIndex = client_1.getAsString(new OID(".1.3.6.1.2.1.25.2.3.1.1."+i));
					if(hrStorageIndex!="Null") 
					{
						//System.out.println(hrStorageIndex+"-hrStorageIndex");
					
						strStorageAlloc = client_1.getAsString(new OID(".1.3.6.1.2.1.25.2.3.1.4."+i));
						strMemSize = client_1.getAsString(new OID(".1.3.6.1.2.1.25.2.3.1.5."+i));
						strMemUsed = client_1.getAsString(new OID(".1.3.6.1.2.1.25.2.3.1.6."+i));
						hrStorageDesc =  client_1.getAsString(new OID(".1.3.6.1.2.1.25.2.3.1.3."+i));
						if(hrStorageDesc.equals("Physical Memory")) {
							ramIndex=i;
							dRAMMemSize+= Double.valueOf(strMemSize)* Double.valueOf(strStorageAlloc);
							dRAMMemUsed+= Double.valueOf(strMemUsed)* Double.valueOf(strStorageAlloc);
						}
						else 
						{
							dDiskMemSize+= Double.valueOf(strMemSize)* Double.valueOf(strStorageAlloc);
							dDiskMemUsed+= Double.valueOf(strMemUsed)* Double.valueOf(strStorageAlloc);
							memUtilIndex++;
						}
					} 
			}

			//System.out.println(dRAMMemSize+" -Double dRAMMemSize\n"+dRAMMemUsed+" -Double dRAMMemUsed\n"+ramIndex+" -ramIndex");
			if((dRAMMemUsed*100)/dRAMMemSize<100)
				System.out.println((dRAMMemUsed*100)/dRAMMemSize+"% -****RAM_Utilization****");
			else
				System.out.println("100% -****RAM_Utilization****");
			
			//System.out.println(dDiskMemSize+" -Double dDiskMemSize\n"+dDiskMemUsed+" -Double dDiskMemUsed\n"+memUtilIndex+" -ProcessorLoadIndex");
			if((dDiskMemUsed*100)/dDiskMemSize<100)
				System.out.println((dDiskMemUsed*100)/dDiskMemSize+"% -****Disk_Utilization****");
			else
				System.out.println("100% -****RAM_Utilization****");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(" line 240");
			e.printStackTrace();
		}
	}
}

//**********Ping**********

class ping implements Runnable {
//	private final String url = "jdbc:postgresql://localhost:5432/devicetable";
//	private final String user = "postgres";
//	private final String password = "root";
	Thread ping_thread;
	ArrayList<String> ipAddress = new ArrayList<String>();
	
	public ping(ArrayList<String> ipAddress2) {
		// TODO Auto-generated constructor stub
		ipAddress=ipAddress2;
	}

	public void NewThread(String ipAddress1[]) {
		ping_thread = new Thread(this, "Ping Thread");
		System.out.println("ping_thread:" + ping_thread);
		ping_thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
/*		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			// System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
*/		try {
			for (int i = 0; i < ipAddress.size(); i++) {
				InetAddress geek = InetAddress.getByName(ipAddress.get(i));
				System.out.println("Sending Ping Request to " + ipAddress.get(i));
				if (geek.isReachable(5000)) {
					System.out.println("Host is reachable");

			/*		String SQL = "INSERT INTO details(ip,status) " + "VALUES(?,?)";

					PreparedStatement pstmt = conn.prepareStatement(SQL);

					pstmt.setString(1, ipAddress[i]);
					pstmt.setString(2, "up");

					int affectedRows = pstmt.executeUpdate();
*/
				}

				else
					System.out.println("Sorry ! We can't reach to this host");

		/*		String SQL = "INSERT INTO details(ip,status) " + "VALUES(?,?)";

				PreparedStatement pstmt = conn.prepareStatement(SQL);

				pstmt.setString(1, ipAddress[i]);
				pstmt.setString(2, "down");

				int affectedRows = pstmt.executeUpdate();
		*/	}
		} catch (Exception E) {
			System.out.print("Ping Thread Ended");
		}
	}

}


//**********Bandwidth Utilization**********

class bandwidthUtil_calc extends Thread
{
	String ip;
	static long polling_interval;
	static long ifSpeed=0;
	static double ifUtilRate=0;
	static long old_inoctet=0, old_outoctet=0, del_outoctet=0;
	static long new_inoctet=0, new_outoctet=0, del_inoctet=0;
	Integer ifIndex,flag=0;

	public bandwidthUtil_calc(Integer ifInd, String ip2, long interval) {
		ip = ip2;
		ifIndex = ifInd;
		polling_interval=interval;
	}

	public void run() {
		try {
			SNMPManager client = new SNMPManager(ip);
			client.start();

			Double d = 0.0;

			String IfSpeed = client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.5." + ifIndex));
			ifSpeed = Long.parseLong(IfSpeed);
			//System.out.println(ifSpeed + "Mb :IfSpeed\n");

			// loop infinitely every polling_interval for 15 seconds
			//while (true) {

				String IfInOctets = client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.10." + ifIndex));
				new_inoctet = Long.parseLong(IfInOctets);
				//System.out.println(new_inoctet + " -IfInOctets");
				String IfOutOctets = client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.16." + ifIndex));
				new_outoctet = Long.parseLong(IfOutOctets);
				//System.out.println(new_outoctet + " -IfOutOctets");

				//System.out.println(del_inoctet + " -del_inoctet before");
				//System.out.println(del_outoctet + " -del_outoctet before");

				// if new Octet value greater than old
				del_inoctet = new_inoctet - old_inoctet;
				Math.abs(del_inoctet);
				del_outoctet = new_outoctet - old_outoctet;
				Math.abs(del_outoctet);

				//System.out.println(del_inoctet + " -del_inoctet after");
				//System.out.println(del_outoctet + " -del_outoctet after");

				//System.out.println(old_inoctet + " -old_inoctet");
				//System.out.println(old_outoctet + " -old_outoctet");

				old_inoctet = new_inoctet;
				old_outoctet = new_outoctet;

				//System.out.println(old_inoctet + " -old_inoctet");
				//System.out.println(old_outoctet + " -old_outoctet");
				//
				ifUtilRate = (double) ((del_inoctet + del_outoctet) * 8 * 100) / ((polling_interval / 1000) * ifSpeed);
				String string_ifUtilRate = String.format("%.4f", ifUtilRate);
				if(flag!=0)
					System.out.println(string_ifUtilRate + "% -******Bandwidth Utilization*****");
				else
				{	
					flag++;
					System.out.println("0.0% -******Bandwidth Utilization*****");
				}
				Thread.sleep(polling_interval);
			//}
		}

		catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

