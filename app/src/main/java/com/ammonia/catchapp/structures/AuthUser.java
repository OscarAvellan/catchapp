package com.ammonia.catchapp.structures;

/**
 * Created by Dalzy on 13/10/2017.
 */

public class AuthUser {

    // Returned by the server as {'id': user.id, 'name': user.username, 'userRole': 'user', 'token': token.key}
    private int id;
    private String userName;
    private String token;

    public AuthUser(int id, String userName, String token){
        this.id = id;
        this.userName = userName;
        this.token = token;
    }

    public int getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getToken(){
        return token;
    }

    @Override
    public String toString(){
        return id + " " + userName + " " + token;
    }
}
