package ru.sigmait.configmanagement;

import ru.sigmait.ftpmanagement.FtpConfig;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    public FtpConfig getFtpParameters() throws InvocationTargetException, IllegalAccessException, NumberFormatException {
        FtpConfig config = new FtpConfig();

        ArrayList<?> propertyNames = Collections.list(_properties.propertyNames());
        Method[] methods = config.getClass().getMethods();
        for (Object propertyName:propertyNames) {

            for (Method method : methods) {
                String methodName = method.getName();
                if(methodName.toLowerCase().contains("set_" + propertyName.toString().trim().toLowerCase())){
                    String value = getPropertyValue(propertyName.toString());
                    Object propertyValue;
                    Class<?>[] types = method.getParameterTypes();
                    String typeName = types[0].getSimpleName();

                    switch (typeName){
                       default:
                            propertyValue = value;
                            break;
                        case "boolean":
                            propertyValue = value.equalsIgnoreCase("yes");
                            break;
                        case "int":
                            try{
                            propertyValue = Integer.parseInt(value);
                            }catch (NumberFormatException e){
                                throw new NumberFormatException(String.format("Неверное числовое значение в файле ScanStationLauncher.properties. \nИмя параметра: %s; значение: %s", propertyName, value));
                            }
                            break;
                    }
                    method.invoke(config, propertyValue);
                }
            }
        }
        return config;
    }

    private static String getPropertyValue(String property){
        String propertyValue;
        propertyValue = _properties.getProperty(property);
        return propertyValue.trim();
    }

}
