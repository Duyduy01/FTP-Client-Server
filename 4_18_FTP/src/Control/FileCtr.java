/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.File;

/**
 *
 * @author olliv
 */
public class FileCtr extends Thread {

    public String folder_dir = "";
    String directory = "";
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    
    // Show file dir
    public String showFiles() {
        File folder = new File(directory);
        File[] files = folder.listFiles();

        int indent = 0;
        for (File file : files) {
            get_sub_path(file, indent);
        }
        return folder_dir;
    }

    public void get_sub_path(File file, int indent) {
        String indent_string = get_indent_string(indent);
        String file_name = indent_string + "|---" + file.getName();

        if (file.isFile()) {
            folder_dir += file_name + "\n";
        } else if (file.isDirectory()) {
            folder_dir += file_name + "\n";
            File[] files_in_dir = file.listFiles();
            for (File files_in_dir1 : files_in_dir) {
                get_sub_path(files_in_dir1, indent + 1);
            }
        }
    }

    public static String get_indent_string(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|    ");
        }
        return sb.toString();
    }

    public static String get_file_extension(File file) {
        try {
            String file_name = file.getName();
            int index = file_name.lastIndexOf('.');
            String extension = file_name.substring(index);
            return extension;
        } catch (Exception e) {

        }
        return null;
    }
}
