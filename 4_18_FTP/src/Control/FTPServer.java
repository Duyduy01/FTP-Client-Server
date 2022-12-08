/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Model.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author olliv
 */
public class FTPServer extends Thread {

    ObjectInputStream oin;

    FileInputStream fin;
    BufferedInputStream bin;
    DataInputStream din;
    OutputStream ostream;

    FileOutputStream fout;
    BufferedOutputStream bout;
    InputStream istream;
    DataOutputStream dout;

    Socket client;

    String[] extensions = {".txt", ".mp3", ".mp4", ".wav", ".docx", ".jpg", ".jpeg"};
    String allow = "Allow";
    String reject = "Reject";

    String action = "";

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

    public FTPServer(Socket socket) {
        try {
            client = socket;
            oin = new ObjectInputStream(client.getInputStream());
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
            istream = client.getInputStream();
            ostream = client.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Login
    public boolean checkLogin() {
        try {
            User user = (User) oin.readObject();

            if (isValidUser(user)) {
                dout.writeUTF("Success");
                setJtaData("Client login success");
                return true;
            } else {
                dout.writeUTF("Wrong username | password");
                setJtaData("Client login false");
                return false;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public boolean isValidUser(User client) {
        UserCtr iofile = new UserCtr();

        return iofile.checkValidUser(client);
    }

    // Show server file
    public void showFile() {
        try {
            FileCtr fileCtr = new FileCtr();
            fileCtr.setDirectory("I:\\test");
            String server_dir = fileCtr.showFiles();
            dout.writeUTF(server_dir);
            setJtaData("Client show file");
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Send file
    public void receiveFile() {
        try {
            String file_path = din.readUTF();
            File file = new File(file_path);
            boolean is_allow_file = isAllowFile(file_path);

            // Init for write log
            String status = "Reject";
            String file_action = "upload file";

            if (is_allow_file) {
                setJtaData("Client add file : " + file_path);

                try {
                    if (receiveData(file)) {
                        setJtaData("Receive file success");
                        status = "Success";
                        actionLog(file_action, file_path, status);
                    } else {
                        status = "Not finished";
                        actionLog(file_action, file_path, status);
                    }
                } catch (Exception e) {
                    setJtaData("Create file error");
                }

            } else {
                setJtaData("Server not allow to receive this file :" + file_path);
                actionLog(file_action, file_path, status);
            }
            client.close();
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    public boolean receiveData(File file) {
        System.out.println("Runned");
        try {
            fout = new FileOutputStream(file);
            bout = new BufferedOutputStream(fout);

            String fileLen = din.readUTF();
            long file_length = new Long(fileLen);
            byte[] contents = new byte[100000];
            int byte_read = 0;

            while ((byte_read = istream.read(contents)) != -1) {
                bout.write(contents, 0, byte_read);
            }
            bout.close();
            fout.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public void sendFile() {
        try {
            String file_to_send_path = din.readUTF();
            setJtaData("Client want to receive file : " + file_to_send_path);

            // Init to write log
            String status = "Reject";
            String file_action = "Dowload file";

            boolean is_allow_file = false;
            File file_to_send = new File(file_to_send_path);
            if (file_to_send.exists()) {
                is_allow_file = isAllowFile(file_to_send_path);
            } else {
                dout.writeUTF(reject);
            }

            if (is_allow_file) {
                if (sendData(file_to_send)) {
                    status = "Success";
                    actionLog(file_action, file_to_send_path, status);
                } else {
                    status = "Not finished";
                    actionLog(file_action, file_to_send_path, status);
                }
            } else {
                setJtaData("Server not allow to send this file : " + file_to_send_path);
                actionLog(file_action, file_to_send_path, status);
            }
            client.close();
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    public boolean sendData(File file_to_send) {
        setJtaData("Sending ...");
        try {
            fin = new FileInputStream(file_to_send);
            bin = new BufferedInputStream(fin);

            byte[] buffer;
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
            }
            setJtaData("Send file complete!");
            ostream.flush();
            bin.close();
            fin.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public boolean isAllowFile(String file_path) {
        try {
            int extension_index = file_path.lastIndexOf(".");
            String file_extension = file_path.substring(extension_index);
            System.out.println(file_extension);

            for (int i = 0; i < extensions.length; i++) {
                if (file_extension.equals(extensions[i])) {
                    dout.writeUTF(allow);
                    return true;
                }
            }
            dout.writeUTF(reject);
            return false;
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
        return false;
    }

    public void actionLog(String file_action, String file_name, String status) {
        try {
            String time_now = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            String client_address = client.getInetAddress().toString();
            String file_name_b = file_name;
            String file_action_b = file_action;

            String log_file_path = "E:/log/" + file_action + "_" + time_now + "_log.txt";
            fout = new FileOutputStream(new File(log_file_path));

            String title = "Createed at : " + time_now + "\n";
            String content1 = "Client at address : " + client_address + "\n";
            String content2 = "Client action : " + file_action + " file : " + file_name + "\n";
            String action_status = file_action + " " + status;
            String data = title + content1 + content2 + action_status;

            fout.write(data.getBytes());
        } catch (IOException ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String type_request = din.readUTF();
            if ("Login".equals(type_request)) {
                checkLogin();
            } else if ("Show File".equals(type_request)) {
                showFile();
            } else if ("Send".equals(type_request)) {
                receiveFile();
            } else if ("Receive".equals(type_request)) {
                sendFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(SServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
