package servers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class RequestFile {
	DataOutputStream os;
	String username;
	public RequestFile(DataOutputStream os) {
		this.os = os;
		// TODO Auto-generated constructor stub
	}
	public RequestFile(DataOutputStream os, String username) {
		this.username = username;
		this.os = os;
		// TODO Auto-generated constructor stub
	}	
	void permissionDenied(){
		try {
			System.out.println("Errrrrrrr");
			os.writeBytes("HTTP/1.1 403 Forbidden\r\n");
			os.writeBytes("Content-Type: text/html\r\n\r\n");
			os.writeBytes("<HTML><BODY>Enter valid uername/passwd</BODY></HTML>");
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	void wrongUrl(){
		try {
			System.out.println("Wrong Url");
			os.writeBytes("HTTP/1.1 403 Forbidden\r\n");
			os.writeBytes("Content-Type: text/html\r\n\r\n");
			os.writeBytes("<HTML><BODY>Put Valid Url.</BODY></HTML>");	
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	void folderPrintList(String path, String requestedPath){
		System.out.println("Printing all files/folders of "+path);
		try {
			os.writeBytes("HTTP/1.1 200 OK\r\n");
			os.writeBytes("Content-Type: text/html\r\n\r\n");
			String response = "<HTML><HEAD>DROPBOX</HEAD><BODY>";
			response = "<br><br><a href='/~"+username+"/logout'>LOGOUT</a><h2>Your Folders</h2><ol>";
			File[] listOfFiles = (new File(path)).listFiles();
			for(File fileF : listOfFiles){
				if(fileF.isDirectory()){
					response+="<li><a href='"+requestedPath+"/"+fileF.getName()+"'>"+fileF.getName()+"</a></li>";
				}
			}
			response += "<hr><h2>Your Files</h2><ol>";
			for(File fileF : listOfFiles){
				if(fileF.isFile()){				
					response+="<li><a href='"+requestedPath+"/"+fileF.getName()+"'>"+fileF.getName()+"</a></li>";
				}
			}
			response+="</ol></BODY></HTML>";
			os.writeBytes(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	void filePath(String path, String requestedPath, String cookie){
		if(path.endsWith("public_html/"))
			path+="index.html";
		else if(path.endsWith("public_html"))
			path+="/index.html";
		System.out.println(path);
		File folderFile = new File(path);
		if(folderFile.isDirectory()){
			folderPrintList(path, requestedPath);
			return;
		}
		int bytes = (int) folderFile.length();
        FileInputStream fis = null ;
        boolean fileExists = true;
        try {
        	fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
        	fileExists = false ;
        }
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null; 
        String cookieInfo = "";
        String CRLF = "\r\n";
        if (fileExists) {
		    statusLine = "HTTP/1.1 200 OK" + CRLF;
		    contentTypeLine = "Content-Type: " + contentType(path) + CRLF;
		    contentTypeLine = "Content-Length: " + bytes + CRLF;
		    if(!cookie.equals(""))
		    	cookieInfo = "Set-Cookie: sid="+cookie+";path=/;" + CRLF;
        } else {
		    statusLine = "HTTP/1.1 404 Not Found" + CRLF;
		    contentTypeLine = "Content-Type: text/html" + CRLF;
		    entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>File Not Found</BODY></HTML>";
        }
	// Send the status line.
        try {
			os.writeBytes(statusLine);
	        os.writeBytes(contentTypeLine);
	        os.writeBytes(cookieInfo);
	        os.writeBytes(CRLF);
	        if (fileExists) {
			    try {
					sendBytes(fis, os);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    fis.close();
	        } else {
	        	os.writeBytes(entityBody) ;
	        }			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
	// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		while ((bytes = fis.read(buffer)) != -1) {
		    os.write(buffer, 0, bytes);
		}
    }
    private static String contentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
		    return "text/html";
		}
		if(fileName.endsWith(".js")) {
		    return "text/javascript";  
		}
		if(fileName.endsWith(".css")) {
		    return "text/css";
		}
		if(fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
		    return "audio/x-pn-realaudio";
		}
		return "application/octet-stream" ;
    }
}
