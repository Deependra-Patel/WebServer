package servers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DosChecker {
	static long diffHistory = 1000; 
	static long minTimeForNewConnection = 20;
	static long grantNewConnection = 100;
	static int historySize = 10;
	
	static String logFile;
	static String blackListedIpFile; 
	
	Set<String> blackListedIp = new HashSet<String>();
	HashMap<String, IpData> IpDataMap = new HashMap<String, IpData>();

	long time; //time in ms
	String ip;
	
	DosChecker(){
		logFile = Paths.networks + "log.txt";
		blackListedIpFile = Paths.networks + "blackListedIps.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(blackListedIpFile));
			String line;
			while((line = br.readLine()) != null){
				blackListedIp.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void writeLog(Socket curConnection){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		ip = curConnection.getInetAddress().getHostAddress();
		if(ip.startsWith("/"))
			ip = ip.substring(1);
		time = new Date().getTime();
		try{
			FileWriter fw = new FileWriter(logFile, true);
			fw.write(ip+","+String.valueOf(time)+","+sdf.format(new Date())+"\n");
			fw.close();
		}
		catch(IOException ioe){
			System.out.println("log file not found");
		}	
	}
	boolean blacklisted(){
		boolean blackListed = blackListedIp.contains(ip);
		if(blackListed)
			System.out.println("Blacklisted IP---"+ip);
		return blackListed;
	}
	boolean checkHistory(){
		IpData ipData;
		if((ipData = IpDataMap.get(ip))!=null){
			if(!ipData.isFull()){
				ipData.insertTime(time);
				return false;
			}
			else if(time - ipData.getFirst() < minTimeForNewConnection){
				ipData.print();
				System.out.println("Asking for connection too soon");
				return true;
			}
			else if(time-ipData.getFirst()>=grantNewConnection){
				ipData.insertTime(time);
				return false;
			}			
			else if(ipData.getLast()-ipData.getFirst() >= diffHistory){
				ipData.insertTime(time);
				return false;
			}
			else return true;
		}
		else {
			System.out.println("IP not found in history");
			IpData newIp = new IpData(historySize, ip);
			IpDataMap.put(ip, newIp);
			return false;
		}
	}
	boolean checkDosAttack(Socket newConnection){
		writeLog(newConnection);
		if(blacklisted()){
			return true;
		}
		return checkHistory();
	}
}
