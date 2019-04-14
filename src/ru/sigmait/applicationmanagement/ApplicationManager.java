package ru.sigmait.applicationmanagement;

import ru.sigmait.configmanagement.ConfigManager;
import ru.sigmait.ftpmanagement.FtpConfig;
import ru.sigmait.ftpmanagement.FtpManager;
import ru.sigmait.updatemanagement.UpdateManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ApplicationManager {

    private ConfigManager _configManager;

//    public ApplicationManager() throws IOException {
//
//    }

    public void start() throws IllegalAccessException, IOException, InvocationTargetException {
        _configManager = new ConfigManager();

        startTmpFolderMonitoring();
        startUpdateMonitoring();
        startScanStation();
    }

    private void startUpdateMonitoring() throws IOException, InvocationTargetException, IllegalAccessException {

        ConfigManager configManager = new ConfigManager();
        FtpConfig config = configManager.getFtpParameters();
        UpdateManager updateManager = new UpdateManager(config);
        updateManager.startCheckingUpdates();
    }

    private void startTmpFolderMonitoring(){

    }

    private void startScanStation(){

    }
}
