package servers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Authentication {
	String username;
	String passwd;
	private boolean checkAccess(final String username, final String passwd){ 
		try{
			String userPasswdFile = Paths.networks+"user_passwd.txt";
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(userPasswdFile));
			String line;
			boolean verify = false;
			while((line = br.readLine()) != null){
				int indexOfSpace = line.indexOf(' ');
				if(indexOfSpace!=-1 && username.equals(line.substring(0, indexOfSpace)) && passwd.equals(line.substring(indexOfSpace+1))){
					verify = true;
					break;
				}
			}
			br.close();
			if(verify)
				return true;
			else return false;
		}
		catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	boolean login(String url){
	        String query = url.split("\\?")[1];
	        String[] pairs = query.split("\\&");
	        if(pairs.length!=2){
	        	return false;
	        }
	        else {
	        	int fi = pairs[0].indexOf("=");
	        	int si = pairs[1].indexOf("=");
	        	if(pairs[0].substring(0, fi).equals("username") && pairs[1].substring(0, si).equals("passwd")){
					try {
						username = URLDecoder.decode(pairs[0].substring(fi+1), "UTF-8");
		        		passwd = URLDecoder.decode(pairs[1].substring(si+1), "UTF-8");
		        		System.out.println(username+"---"+passwd);
		        		return checkAccess(username, passwd);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
	        	}
	        }
       return false;
	}
}
