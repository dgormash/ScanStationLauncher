package ru.sigmait.managers;

import java.io.File;

public class EnvironmentManager {
    public boolean isPathExists(String path){
        boolean result = false;

        File file = new File(path);
        if(file.exists() && file.isFile()){
            result = true;
        }
        return  result;
    }
}
