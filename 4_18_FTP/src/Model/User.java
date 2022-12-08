/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author olliv
 */
public class User implements Serializable{
    private String user_name;
    private String password;

    public User() {
    }

    public User(String user_name, String password ) {
        this.user_name = user_name;
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
