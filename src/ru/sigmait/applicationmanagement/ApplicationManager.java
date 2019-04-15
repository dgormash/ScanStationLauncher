package ru.sigmait.applicationmanagement;

import ru.sigmait.configmanagement.ConfigManager;
import ru.sigmait.environmentmanagement.EnvironmentManager;
import ru.sigmait.environmentmanagement.FileDownloadedListener;
import ru.sigmait.ftpmanagement.FtpConfig;
import ru.sigmait.ftpmanagement.FtpErrorListener;
import ru.sigmait.scanstationmanagement.ScanStationErrorListener;
import ru.sigmait.scanstationmanagement.ScanStationManager;
import ru.sigmait.updatemanagement.UpdateManager;

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

    public ApplicationManager() throws IllegalAccessException, IOException, InvocationTargetException, InterruptedException {
        _configManager = new ConfigManager();
        _ftpConfig = _configManager.getFtpParameters();

        _updateManager = new UpdateManager(_ftpConfig);
        _updateManager.set_fileDownloadedListener(this);

        _updateManager.set_preScriptCommand(_configManager.getPreScriptCommand());
        _updateManager.set_postScriptCommand(_configManager.getPostScriptCommand());

        _currentVersion = getCurrentVersion();
        _userNotifier = new UserNotifier();


        if(_currentVersion != null){
            _updateManager.setCurrentVersion(_currentVersion);
            //Проверилили, есть ли неустановленное обновление в каталоге temp. Если есть, то мы его устанавливаем. Скан-станция ещё не запущена.
            //Мониторинг ftp-ещё не запущен. Можем смело запускать процесс обновления.
            checkLocalDirectoryForUpdates();
            startUpdateMonitoring();
        }
        startScanStation();
    }

    public void stop(){
        if(_updateManager != null){
            _updateManager.stopCheckingUpdates();
        }

        if(_scanStationManager != null){
            _scanStationManager.terminateApplication();
        }
    }

    private void startUpdateMonitoring(){
        _updateManager.startCheckingUpdates();
    }

    private void checkLocalDirectoryForUpdates() throws IOException, InterruptedException {

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

                int dialogResult = notifyUser();

                if(dialogResult == JOptionPane.YES_OPTION){
                    _updateManager.startUpdate(fileOfUpdate);
                }
                //todo Обработска ситуации, когда пользователь ответил "Напомнить позднее". Запускаем процесс нотификации
                //todo пользователя в таймере с интервалом в 10 минут
            }
        }
    }

    private void startScanStation() throws IOException,  InterruptedException {
        EnvironmentManager environmentManager = new EnvironmentManager();
        File workingDirectory = new File(System.getProperty("user.dir"));
        List<String> launchCommandData = new ArrayList();
        launchCommandData.addAll(Arrays.asList(_configManager.getLaunchCommand().split("\\s")));
        String javaExecutable = launchCommandData.get(0);
        String jarFile = launchCommandData.get(2);

        if(!environmentManager.isPathExists(workingDirectory + "/" + jarFile)){
            throw new FileNotFoundException("Не найден файл " + jarFile);
        }

        if(!javaExecutable.equalsIgnoreCase("java")){
            if(!environmentManager.isPathExists(workingDirectory + "/" + javaExecutable + ".exe")){
                launchCommandData.set(0, "java");
            }
        }

        //Добавляем classpath
        String classpathValue = _configManager.getClassPath();
        launchCommandData.add(1, "-classpath");
        launchCommandData.add(2, "\"" + classpathValue + "\"");

        _scanStationManager = new ScanStationManager(workingDirectory, launchCommandData);
        _scanStationManager.addListeners(this::ErrorOccurred);
        _scanStationManager.runApplication();
    }

    private int notifyUser (){
        Object [] dialogOptions = {"Установить", "Напомнить позже"};
        int dialogResult =  JOptionPane.showOptionDialog(null,
                "Загружены обновления для приложения \"Скан-станция\"",
                "Обновление системы",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, dialogOptions, dialogOptions[0]);
        return dialogResult;
    }

    @Override
    public void FileDownloaded(String filePath){
        int dialogResult = notifyUser();

        if(dialogResult == JOptionPane.YES_OPTION) {
            try {
                //Если скачан файл обновления, то мы останавливаем мониторинг ftp - сервера
                if (_updateManager != null) {
                    _updateManager.stopCheckingUpdates();
                }
                //Останавливаем процесс скан-станции
                if (_scanStationManager != null) {
                    _scanStationManager.terminateApplication();
                }
                //Запускаем обновление
                File fileOfUpdate = new File(filePath);
                _updateManager.startUpdate(fileOfUpdate);
                //Запускаем мониторинг ftp-сервера
                _updateManager.startCheckingUpdates();
                //Запускаем скан-станцию
                _scanStationManager.runApplication();
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        "Ошибка в процессе чтения конфигурации",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        "Ошибка ввода-вывода",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
    public void ErrorOccurred(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Ошибка работы скан-станции",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void NotifyAboutUsersChoice(int dialogResult) {
        if(dialogResult == JOptionPane.YES_OPTION){
            _userNotifier.stopUserNotification();

        }
    }
}
