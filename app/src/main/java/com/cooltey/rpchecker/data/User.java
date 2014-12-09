package com.cooltey.rpchecker.data;

import org.json.JSONObject;

public class User {
	public String token;
    public String user_id;
    public String user_name;
    public String user_email;
    public String user_lastlogin;
    public String user_ip;

    public static User fromJSON(String json) {
        User user = null;
        try {
            JSONObject userObject = new JSONObject(json);
            user = new User();
            user.token 				= userObject.getString("token");
            user.user_id 			= userObject.getString("user_id");
            user.user_name 			= userObject.getString("user_name");
            user.user_email 		= userObject.getString("user_email");
            user.user_lastlogin 	= userObject.getString("user_lastlogin");
            user.user_ip 			= userObject.getString("user_ip");
            
        } catch (Exception e) {
        }
        return user;
    }
}
