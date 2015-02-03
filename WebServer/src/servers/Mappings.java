package servers;

import java.util.HashMap;

public class Mappings {
	HashMap<String, String> cookieUserMap = new HashMap<String, String>();
	boolean loggedIn(String cookie, String username){
		//return false;
		String user = cookieUserMap.get(cookie);
		System.out.println(user);
		if(user==null){
			System.out.println("Cookie not mapped to any user.");
			return false;
		}
		else return (user.equals(username));
	}
	void putCookie(String cookie, String username){
		cookieUserMap.put(cookie, username);
	}
	void printAll(){
		System.out.println("hasmapppp \n");
		System.out.println(cookieUserMap);
	}
	void removeUserFromMap(String username){
		//cookieUserMap.remove(cookie);
		cookieUserMap.values().remove(username);
		System.out.println("after removing" + cookieUserMap);
	}
}
