package servers;


import java.io.* ;
import java.net.* ;
import java.util.* ;


public final class NewServer {
    public static void main(String argv[]) throws Exception {
		// Listening at this port
		int port=8880;
		ServerSocket socket  = new ServerSocket(port); //creating server socket
		Mappings myMap = new Mappings();
		DosChecker dosChecker = new DosChecker(); 
		while (true) {
			System.out.println(myMap);
		    Socket connection = socket.accept(); 
		    if(dosChecker.checkDosAttack(connection)){
		    //if(false){
		    	connection.close();
		    	System.out.println("Connection Closed !! DOS ATTACK!!!");
		    	continue;
		    }
		    else {
			    //connection.setKeepAlive(true);
		    	System.out.println("NEW SOCKET OPENED");
			    HttpRequest request = new HttpRequest(connection, myMap);
			    Thread thread = new Thread(request);
			    // Starting the thread.
			    thread.start();
		    }
		}
    }
}

final class HttpRequest implements Runnable {
    Socket socket;
    Mappings myMap;
    
    public HttpRequest(Socket socket, Mappings myMap) throws Exception {
    	socket.setSoTimeout(1000);
    	this.socket = socket;
    	this.myMap = myMap; 
    }
    

    public void run() {
    	while(true){
		try {
				if(socket.isClosed())
					break;
				processRequest();
			} catch (Exception e) {
			    System.out.println(e);
			    break;
			}
    	}
    }
 
    private void processRequest() {
    	try{
	    	InputStream is = socket.getInputStream();
			String fileName = "";
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String requestLine = br.readLine();
			StringTokenizer tokens = new StringTokenizer(requestLine);
			String requestType = tokens.nextToken();
			if (requestType.equals("GET")) {  
			    fileName = tokens.nextToken(); 
			    String url = fileName;
			    RequestFile response = new RequestFile(os);
				if (fileName.equals("/")){ 
				      fileName =  Paths.slashHomeslash+"deependra/login/index.html";
				      response.filePath(fileName, url, "");
				}
				else if(fileName.startsWith("/login")){
					fileName = Paths.slashHomeslash+"deependra"+url;
				    response.filePath(fileName, url, "");
				}
				else if (fileName.startsWith("/") == true && fileName.charAt(1)=='~') {
					fileName  = fileName.substring(2);
					String username;
					if(fileName.indexOf('/')!=-1){
						username = fileName.substring(0, fileName.indexOf('/'));
					} else username = fileName;
					System.out.println("username in cookie: "+username);
					String cookie = getCookie(br);
					System.out.println("Cookie sent: "+cookie);
					RequestFile sendFile = new RequestFile(os, username);
					
					if((!cookie.equals("")) && myMap.loggedIn(cookie, username)){
						String path = Paths.slashHomeslash+username+"/public_html/";
						if(fileName.indexOf("/")!=-1)
							path+=fileName.substring(fileName.indexOf("/")+1);					
						if(url.startsWith("/~"+username+"/logout")){
							myMap.removeUserFromMap(username);
							path = Paths.slashHomeslash+"deependra/login/logout.html";
						}
						sendFile.filePath(path, url, "");
					}
					else {
						sendFile.permissionDenied();
					}
				}
				else if(url.split("\\?")[0].equals("/auth")){
			    	Authentication auth = new Authentication();
			    	if(auth.login(url)){
			    		RequestFile sendFile = new RequestFile(os);
			    		String cookie = UUID.randomUUID().toString();
			    		sendFile.filePath(Paths.slashHomeslash+auth.username+"/public_html/index.html", url, cookie);
			    		myMap.putCookie(cookie, auth.username);
			    	}
			    	else {
			    		System.out.println("denied");
			    		RequestFile sendError = new RequestFile(os);
			    		sendError.permissionDenied();
			    	}
			    }
			    else {
			    	System.out.println("wrong file");
			    	RequestFile sendError = new RequestFile(os);
			    	sendError.wrongUrl();
			    }
			}
			else { 
				System.out.println("Bad request message");
				   return;
			}

			System.out.println("Print header below");
			System.out.println(requestLine);
			String headerLine = null;
			boolean keepalive = true;
			while (!(headerLine = br.readLine()).equals("")) {
				if(headerLine.startsWith("Connection")){
					String arr[] = headerLine.split(" ");
					if(arr.length!=2 || !arr[1].equals("keep-alive")){
						keepalive = false;
					}
				}
			    System.out.println(headerLine);
			}
			System.out.println("Packet ends here");
			if(!keepalive)
				socket.close();
			
    	} catch(IOException ex){
    		try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
			//myMap.printAll();
	        //os.writeBytes("You are accessing from:"+socket.getRemoteSocketAddress());
	
	        // Close streams and socket.
	//        os.close();
	//        br.close();
	//        socket.close();
    }
    String getCookie(BufferedReader br){
		String headerLine = null;
		try {
			while (!(headerLine = br.readLine()).equals("")) {
				//System.out.println(headerLine);
			    if(headerLine.startsWith("Cookie: ")){
			    	return headerLine.substring(headerLine.indexOf("sid")+4);
			    }
			}
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
    }
}