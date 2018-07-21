package com.example.pcc.chatting;
import java.io.Serializable;


public class User implements Serializable {
    private String userName;
    private String password;
    private String Email;
    private long  id;
    private String Mac_Address;
    private boolean SignedIn;
    private static final long serialVersionUID = 1L;

    public User() {

    }
    public User(String userName , String email , String password) {
        this.userName = userName;
        this.password = password;
        Email = email;
    }

    public User(String username) {
        this.userName=username;
    }

    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        Email = email;
    }

}
