package ru.sigmait.managers;

import ru.sigmait.misc.BackgroundStatus;
import ru.sigmait.utils.UserNotifier;
import ru.sigmait.lilsteners.FileDownloadedListener;
import ru.sigmait.misc.FtpConfig;
import ru.sigmait.lilsteners.FtpErrorListener;
import ru.sigmait.lilsteners.NotificationResultListener;
import ru.sigmait.lilsteners.ScanStationErrorListener;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationManager implements FileDownloadedListener, ScanStationErrorListener, FtpErrorListener, NotificationResultListener {

    private ConfigManager _configManager;
    private String _currentVersion;
    private FtpConfig _ftpConfig;
    private UpdateManager _updateManager;
    private ScanStationManager _scanStationManager;
    private UserNotifier _userNotifier;
    private File _fileOfUpdate;

    private CountDownLatch _countDownLatch = new CountDownLatch(1);
    private File _workingDirectory =  new File(System.getProperty("user.dir"));
    private List<String> _launchCommandData = new ArrayList<>();


    public ApplicationManager() throws IllegalAccessException, IOException, InvocationTargetException, InterruptedException {
        _configManager = new ConfigManager();
        _ftpConfig = _configManager.getFtpParameters();
        _updateManager = new UpdateManager(_ftpConfig);
        _updateManager.set_preScriptCommand(_configManager.getPreScriptCommand());
        _updateManager.set_postScriptCommand(_configManager.getPostScriptCommand());
        _userNotifier = new UserNotifier();
        _currentVersion = getCurrentVersion();
        registerToEvents();
        prepareScanStationData();
        _scanStationManager = new ScanStationManager(_workingDirectory, _launchCommandData);

        if(_currentVersion != null){
            _updateManager.setCurrentVersion(_currentVersion);
            //Проверилили, есть ли неустановленное обновление в каталоге temp. Если есть, то мы его устанавливаем. Скан-станция ещё не запущена.
            //Мониторинг ftp-ещё не запущен. Можем смело запускать процесс обновления.
            checkLocalDirectoryForUpdates();
            startUpdateMonitoring();
        }

        startScanStation();
        _countDownLatch.await();
    }

    private void registerToEvents(){
        _userNotifier.setNotificationResultListener(this::NotifyAboutUsersChoice);
        _updateManager.setFileDownloadedListener(this::FileDownloaded);
        _updateManager.setFtpErrorListener(this::ScanStationErrorOccurred);
    }

    public void stopBackgroundOperations(){
        if(_updateManager != null && _updateManager.getStatus() == BackgroundStatus.STARTED){
            _updateManager.stopCheckingUpdates();
            _updateManager.removeListener();
        }


        if(_scanStationManager!= null && _scanStationManager.getStatus() == BackgroundStatus.STARTED){
            _scanStationManager.terminateApplication();
        }

        if(_userNotifier != null && _userNotifier.getStatus() == BackgroundStatus.STARTED){
            _userNotifier.stopUserNotification();
            _userNotifier.removeListener();
        }
    }

    private void startUpdateMonitoring(){
        _updateManager.startCheckingUpdates();
    }

    private void checkLocalDirectoryForUpdates() {

        String fullInstallerMask = _ftpConfig.get_fullInstallerMask().replaceFirst("\\[:version\\]", _currentVersion);
        String patchInstallerMask = _ftpConfig.get_patchInstallerMask().replaceFirst("\\[:currentVersion\\]", _currentVersion);

        File tmp = new File(System.getProperty("java.io.tmpdir"));
        String[] files = tmp.list();

        String patchNameForContains = patchInstallerMask.split("-")[0];
        for(int i = 0; i <= files.length - 1; i ++){
            if (files[i].contains(fullInstallerMask) || files[i].contains(patchNameForContains)) {
                File fileOfUpdate = new File(tmp + File.separator + files[i]);

                if(fileOfUpdate.isDirectory())
                    continue;
                _fileOfUpdate = fileOfUpdate;
                _userNotifier.notifyOnce();
            }
        }
    }

    private void startScanStation(){
        _scanStationManager.setLatch(_countDownLatch);
        _scanStationManager.setErrorListener(this::ScanStationErrorOccurred);
        Thread t = new Thread(()-> startScanStationInNewThread());
        t.start();
    }

    private void prepareScanStationData(){
        EnvironmentManager environmentManager = new EnvironmentManager();

        _launchCommandData.addAll(Arrays.asList(_configManager.getLaunchCommand().split("\\s")));
        String javaExecutable =  _launchCommandData.get(0);
        String jarFile = _launchCommandData.get(2);

        if (!environmentManager.isPathExists(_workingDirectory + "/" + jarFile)) {
            //throw new FileNotFoundException("Не найден файл " + jarFile);
        }

        if (!javaExecutable.equalsIgnoreCase("java")) {
            if (!environmentManager.isPathExists(_workingDirectory + "/" + javaExecutable + ".exe")) {
                _launchCommandData.set(0, "java");
            }
        }

        //Добавляем classpath
        String classpathValue = _configManager.getClassPath();
        _launchCommandData.add(1, "-classpath");
        _launchCommandData.add(2, "\"" + classpathValue + "\"");
    }

    private  void startScanStationInNewThread(){
        _scanStationManager.runApplication();
    }


    @Override
    public void FileDownloaded(String filePath){
        _fileOfUpdate = new File(filePath);
        _userNotifier.notifyOnce();
    }

    private String getCurrentVersion(){
        String version = null;

        try(BufferedReader reader = new BufferedReader( new FileReader("app.version")))
        {
            String line;
            while((line = reader.readLine()) != null){
                version = line;
            }
        }catch(IOException e)
        {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка определения текущей версии приложения",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return version;
    }

    @Override
    public void ScanStationErrorOccurred(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Ошибка работы скан-станции",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void NotifyAboutUsersChoice(int dialogResult) {
        if(dialogResult == JOptionPane.YES_OPTION){
            this.startUpdate();
        }
        else{
            _userNotifier.startUserNotification();
        }
    }

    private void startUpdate(){
        this.stopBackgroundOperations();

        _updateManager.startUpdate(_fileOfUpdate);
        updateCurrentVersionInfo();
        JOptionPane.showMessageDialog(null,
                "Скан-станция обновлена до версии " + _currentVersion,
                "Результат обновления",
                JOptionPane.INFORMATION_MESSAGE);
        startBackgroundOperations();
    }

    private void updateCurrentVersionInfo(){
        _currentVersion = getCurrentVersion();
        _updateManager.setCurrentVersion(_currentVersion);
    }

    private void startBackgroundOperations(){
        if(_updateManager.getStatus() == BackgroundStatus.STOPPED){
            _updateManager.startCheckingUpdates();
        }

        if(_scanStationManager.getStatus() == BackgroundStatus.STOPPED){
            this.startScanStation();
        }

        registerToEvents();
    }

    @Override
    public void FtpManagerErrorOccurred(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Ошибка FTP",
                JOptionPane.ERROR_MESSAGE);
    }
}
