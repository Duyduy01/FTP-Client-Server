/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author olliv
 */
public class SServer extends Thread {

    ServerSocket ssock;
    int port = 2312;

    JTextArea jta = new JTextArea();

    public void setJtaData(String message) {
        Date date = new Date();
        String time = date.toString();
        String data = jta.getText();
        data += time + " : " + message + "\n";
        jta.setText(data);
    }

    public void setJta(JTextArea jta) {
        this.jta = jta;
    }

    public void startServer() {
        try {
            ssock = new ServerSocket(port);
            setJtaData("Start server success!");
        } catch (IOException ex) {
        }
    }
    
    @Override
    public void run() {
        while(true){
            try {
                FTPServer ftpServer = new FTPServer(ssock.accept());
                ftpServer.setJta(jta);
                ftpServer.start();
            } catch (IOException ex) {
                Logger.getLogger(SServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
