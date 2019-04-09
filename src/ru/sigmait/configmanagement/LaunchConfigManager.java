package ru.sigmait.configmanagement;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LaunchConfigManager {

    public String getLaunchCommand() throws IOException, NullPointerException {
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        try{
            fileInputStream = new FileInputStream("./ScanStationLauncher.properties");
            properties.load(fileInputStream);
            return properties.getProperty("launchcommand");
        }catch(IIOException e)
        {
            throw new IOException(e.getMessage());
        }catch(NullPointerException e)
        {
            throw new NullPointerException("Ошибка при указании файла конфигурации ScanStationLauncher.properties");
        }
    }
}
