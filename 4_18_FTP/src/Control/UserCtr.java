/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Model.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author olliv
 */
public class UserCtr {
    
    public static void createUser(){
        try {
            FileOutputStream fos = new FileOutputStream(new File("E:\\user.txt"));
            ObjectOutputStream oout = new ObjectOutputStream(fos);

            User user1 = new User("user", "user");
            User user2 = new User("admin", "admin");
            User user3 = new User("duy", "duy123");
            User user4 = new User("loc", "loc123");
            oout.writeObject(user1);
            oout.writeObject(user2);
            oout.writeObject(user3);
            oout.writeObject(user4);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } 
    }
    
    public boolean checkValidUser(User client){
        try {
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(new File("E:\\user.txt")));
            User user;
            System.out.println("runned");
            while((user=(User) oin.readObject())!=null){
                if(user==null){
                    break;
                }
                boolean isValidUsername = client.getUser_name().equals(user.getUser_name());
                boolean isValidPassword = client.getPassword().equals(user.getPassword());
                if(isValidUsername && isValidPassword){
                    return true;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    public static void main(String[] args) {
        createUser();
    }
}
