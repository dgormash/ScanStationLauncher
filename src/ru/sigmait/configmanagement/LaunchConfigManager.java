package ru.sigmait.configmanagement;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LaunchConfigManager {

    private static FileInputStream _fileInputStream;

    public LaunchConfigManager() throws FileNotFoundException {
        _fileInputStream = new FileInputStream("./ScanStationLauncher.properties");
    }

    public String getLaunchCommand() throws IOException, NullPointerException {
        return getPropertyValue("launchcommand");
    }

    public String getPrescriptCommand() throws IOException{
        return getPropertyValue("prescript");
    }

    public String getPostScriptCommand()throws IOException{
        return getPropertyValue("postscript");
    }

    public String getClassPath() throws IOException {
        return getPropertyValue("classpath");
    }

    private static String getPropertyValue(String property) throws IOException{
        String propertyValue;

        Properties properties = new Properties();
        try{
            properties.load(_fileInputStream);
            propertyValue = properties.getProperty(property);
        }catch(IIOException e)
        {
            throw new IOException(e.getMessage());
        }

        return propertyValue;
    }

}
