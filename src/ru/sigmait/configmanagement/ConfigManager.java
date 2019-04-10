package ru.sigmait.configmanagement;

import ru.sigmait.ftpmanagement.FtpConfig;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static FileInputStream _fileInputStream;
    private static Properties _properties;

    public ConfigManager() throws IOException {
        _fileInputStream = new FileInputStream("ScanStationLauncher.properties");
        _properties = new Properties();
        _properties.load(_fileInputStream);
    }

    public String getLaunchCommand(){
        return getPropertyValue("launchcommand");
    }

    public String getPrescriptCommand(){
        return getPropertyValue("prescript");
    }

    public String getPostScriptCommand(){
        return getPropertyValue("postscript");
    }

    public String getClassPath() {
        return getPropertyValue("classpath");
    }

    public FtpConfig getFtpParameters(){
        FtpConfig config = new FtpConfig();
        //todo Заполнить конфиг значениями
        return config;
    }

    private static String getPropertyValue(String property){
        String propertyValue;
        propertyValue = _properties.getProperty(property);
        return propertyValue;
    }

}
