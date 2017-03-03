package com.example.utils;

import java.io.File;

public class util {

    public static void delete(File file) {  
        if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
                return;  
            }  
      
            for (int i = 0; i < childFiles.length; i++) {  
                delete(childFiles[i]);  
            }  
            file.delete();  
        }  
    } 
}
