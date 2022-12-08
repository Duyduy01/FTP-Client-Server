/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Model.User;
import View.ClientFrm;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author olliv
 */
public class FTPClient extends Thread {

    Socket client;
    String host = "";
    int port = 2312;

    String userName = "";
    String password = "";
    static boolean isLogin = false;

    String file_to_read;
    String file_to_write;

    Scanner sc = new Scanner(System.in);
    DataInputStream din;
    DataOutputStream dout;
    ObjectOutputStream oout;

    FileInputStream fin;
    BufferedInputStream bin;
    OutputStream ostream;

    FileOutputStream fout;
    BufferedOutputStream bout;
    InputStream istream;

    String action = "";

    JTextArea jta = new JTextArea();
    JProgressBar jpb = new JProgressBar();
    JLabel jlabel = new JLabel();
    JButton jbutton = new JButton();
    JPanel jPanel = new JPanel();

    // Init
    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }

    public void setJbutton(JButton jbutton) {
        this.jbutton = jbutton;
    }

    public void setJlabel(JLabel jlabel) {
        this.jlabel = jlabel;
    }

    public void setJLbData(String message) {
        jlabel.setText(message);
    }

    public void setJtaData(String message) {
        Date date = new Date();
        String time = date.toString();
        String data = jta.getText();
        data += time + " : " + message + "\n";
        jta.setText(data);
    }

    public void setJpbData(int percent) {
        jpb.setMaximum(100);
        jpb.setValue(percent);
    }

    public void setJpb(JProgressBar jpb) {
        this.jpb = jpb;
    }

    public void setJta(JTextArea jta) {
        this.jta = jta;
    }

    public void setLoginInfo(String host, int port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFile_to_read(String file_to_read) {
        this.file_to_read = file_to_read;
    }

    public void setFile_to_write(String file_to_write) {
        this.file_to_write = file_to_write;
    }

    // Show component when send or receive file: Jprogress bar, JLabel, JButton
    public void showOrCloseComponent(String status) {
        if ("close".equals(status)) {
            jPanel.remove(jpb);
            jPanel.remove(jlabel);
            jPanel.remove(jbutton);
        } else {
            jpb.setVisible(true);
            jlabel.setVisible(true);
            jbutton.setVisible(true);
        }
    }

    public void initClient() {
        try {
            client = new Socket(host, port);
            istream = client.getInputStream();
            ostream = client.getOutputStream();
            oout = new ObjectOutputStream(client.getOutputStream());
            dout = new DataOutputStream(client.getOutputStream());
            din = new DataInputStream(client.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Login 
    public boolean login() {
        try {
            client = new Socket(host, port);
            oout = new ObjectOutputStream(client.getOutputStream());
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());

            dout.writeUTF("Login");
            User user = new User(userName, password);
            oout.writeObject((Object) user);

            String allow_login = din.readUTF();
            if (allow_login.equals("Success")) {
                setJtaData("Login success to server");
                return true;
            } else {
                setJtaData("Login false");
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Show server
    public void showServerFile() {
        try {
            String server_dir = din.readUTF();
            setJtaData(server_dir);
        } catch (IOException ex) {
        }
    }

    // Send file
    public void sendFile() {
        try {
            File file_to_send = new File(file_to_read);
            if (file_to_send.exists()) {
                boolean allowed_send = isAllowSend();
                if (allowed_send) {
                    sendData(file_to_send);
                } else {
                    setJtaData("Server not allow you to send your file");
                    showOrCloseComponent("close");
                    ClientFrm.countThread--;
                }
                client.close();
            } else {
                setJtaData("File path error");
                client.close();
            }

        } catch (IOException ex) {
            setJtaData("An error occur");
        }
    }

    public boolean isAllowSend() {
        try {
            String file_des_path = file_to_write;

            dout.writeUTF(file_des_path);

            String is_allowed = din.readUTF();
            if (is_allowed.equals("Allow")) {
                return true;
            } else if (is_allowed.equals("Reject")) {
                return false;
            }
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public void sendData(File file_to_send) {
        try {
            setJtaData("Sending file");
            // Show copponent
            showOrCloseComponent("show");

            fin = new FileInputStream(file_to_send);
            bin = new BufferedInputStream(fin);
            byte[] buffer = null;
            long current = 0;
            long file_length = file_to_send.length();

            String fileLen = String.valueOf(file_length);
            dout.writeUTF(fileLen);
            while (current != file_length) {
                int size = 1000000;
                if (file_length - current >= size) {
                    current += size;
                } else {
                    size = (int) (file_length - current);
                    current = file_length;
                }

                buffer = new byte[size];
                bin.read(buffer, 0, size);
                ostream.write(buffer);

                caculateTime(current, file_length, size);
                sleep(100);
            }
            //close component
            showOrCloseComponent("close");
            setJtaData("Sending finish");
            ClientFrm.countThread--;
            ostream.flush();
            fin.close();
            bin.close();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    // Receive file
    public void receiveFile() {
        try {
            boolean is_allowed_receive = isAllowReceive();
            if (is_allowed_receive) {
                String file_path = file_to_write;
                File des_to_receive = new File(file_path);

                receiveData(des_to_receive);
                setJtaData("Receive Success");
                client.close();

            } else {
                setJtaData("Server not allow you to receive this file");
                showOrCloseComponent("close");
                ClientFrm.countThread--;
            }
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    public boolean isAllowReceive() {
        try {
            String receive_file_path = file_to_read;
            dout.writeUTF(receive_file_path);

            String is_allowed = din.readUTF();
            if (is_allowed.equals("Allow")) {
                return true;
            } else if (is_allowed.equals("Reject")) {
                return false;
            }
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public void receiveData(File des_file) {
        try {
            // Show copponent
            showOrCloseComponent("show");

            fout = new FileOutputStream(des_file);
            bout = new BufferedOutputStream(fout);

            String fileLen = din.readUTF();
            long file_length = new Long(fileLen);
            long current = 0;

            int size = 1000000;
            byte[] contents = new byte[size];
            int byte_to_read = 0;

            while ((byte_to_read = istream.read(contents)) != -1) {
                bout.write(contents, 0, byte_to_read);
                current += byte_to_read;
                sleep(100);
                caculateTime(current, file_length, size);
            }
            // Close component
            showOrCloseComponent("close");
            ClientFrm.countThread--;
            setJtaData("Received completed");
            istream.close();
            bout.close();
            fout.close();
        } catch (IOException | InterruptedException | NumberFormatException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    public void caculateTime(long current, long file_length, int size) {

        // Set progress
        int percent = (int) ((current * 100) / file_length);
        setJpbData(percent);

        // Set time left
        long timesSend = (file_length - current) / size;

        // Assume the sending time is 100ms for size 1000000
        // So total time : 200 ms => can send 5 times in a second
        int timesInSecond = (int) (timesSend / 5);
        int minutes = (int) (timesInSecond / 60);
        int seconds = (int) (timesInSecond - minutes * 60);

        String times = " Time left estimate : " + minutes + "minutes and " + seconds + " seconds";
        setJLbData(times);
    }

    // Run
    @Override
    public void run() {
        if ("Login".equals(action)) {
            setJtaData("Login");
            if (login()) {
                isLogin = true;
            }
        } else if (isLogin && action != null) {
            initClient();
            if ("Show File".equals(action)) {
                try {
                    setJtaData("Show file");
                    dout.writeUTF("Show File");
                    showServerFile();
                } catch (IOException ex) {
                    Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("Send".equals(action)) {
                try {
                    setJtaData("Send file");
                    dout.writeUTF("Send");
                    sendFile();
                } catch (IOException ex) {
                    Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("Receive".equals(action)) {
                try {
                    setJtaData("Receive file");
                    dout.writeUTF("Receive");
                    receiveFile();
                } catch (IOException ex) {
                    Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (action != null && !isLogin) {
            setJtaData("You must login to do this");
        }
    }
}
