package ru.sigmait.managers;

import ru.sigmait.lilsteners.FileDownloadedListener;
import ru.sigmait.lilsteners.FtpErrorListener;
import ru.sigmait.misc.FtpConfig;
import ru.sigmait.timertasks.FtpMonitoringTimerTask;
import ru.sigmait.misc.BackgroundStatus;
import ru.sigmait.utils.ZipUtility;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateManager {

    private int _currentStatus;
    public int getStatus(){
        return _currentStatus;
    }

    private FtpConfig _ftpConfig;
    private String _currentVersion;
    public void setCurrentVersion(String version){
        _currentVersion = version;
    }
    private Timer _timer;
    private FileDownloadedListener _fileDownloadedListener;
    private FtpErrorListener _ftpErrorListener;

    public void setFtpErrorListener(FtpErrorListener listener){
        _ftpErrorListener = listener;
    }

    public void removeListener(){
        _ftpErrorListener = null;
    }

    private final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    private final String HOME_DIRECTORY = System.getProperty("user.dir");

    private String _preScriptCommand;
    public void set_preScriptCommand(String command){
        _preScriptCommand = command;
    }

    private String _postScriptCommand;
    public void set_postScriptCommand (String command){
        _postScriptCommand = command;
    }

    public void setFileDownloadedListener(FileDownloadedListener listener){
        _fileDownloadedListener = listener;
    }

    public UpdateManager(FtpConfig ftpConfig){
        _ftpConfig = ftpConfig;
    }

    public void startCheckingUpdates(){
        TimerTask ftpMonitoringTimerTask = new FtpMonitoringTimerTask(_ftpConfig);
        ((FtpMonitoringTimerTask) ftpMonitoringTimerTask).setCurrentVersion(_currentVersion);
        ((FtpMonitoringTimerTask) ftpMonitoringTimerTask).setCopiedFileDestinationFolder(TEMP_DIRECTORY);

        ((FtpMonitoringTimerTask) ftpMonitoringTimerTask).addListener(_fileDownloadedListener);
        ((FtpMonitoringTimerTask) ftpMonitoringTimerTask).addErrorListener(_ftpErrorListener);
        _timer = new Timer();
        _timer.scheduleAtFixedRate(ftpMonitoringTimerTask,0, _ftpConfig.get_repeatAfter());
        _currentStatus = BackgroundStatus.STARTED;
    }

    public void stopCheckingUpdates(){
        stopCheckingTask();
        _currentStatus = BackgroundStatus.STOPPED;
    }

    public void startUpdate(File fileOfUpdate){
        //Разархивируем содержимое архива в каталог
        try {
            ZipUtility zipUtility = new ZipUtility();
            String directoryOfUpdateContents = TEMP_DIRECTORY + getFileNameWithoutExtension(fileOfUpdate);
            String destDirectory = HOME_DIRECTORY;
            zipUtility.unzip(String.format(fileOfUpdate.toString()),String.format(directoryOfUpdateContents));
            //Получаем комманду pre-скрипта.
            if (_preScriptCommand != null) {
                Path preScriptPath = Paths.get(directoryOfUpdateContents + File.separator + _preScriptCommand);
                Path preScriptDestPath = Paths.get(destDirectory + File.separator + _preScriptCommand);
                File preScript = new File(preScriptPath.toUri());

                if (preScript.exists()) {
                    Files.copy(preScriptPath, preScriptDestPath, StandardCopyOption.REPLACE_EXISTING);
                    startProcess(preScriptDestPath.toString());
                    Files.deleteIfExists(preScriptPath);
                    Files.deleteIfExists(preScriptDestPath);
                }
            }

            File sourceLocation = new File(directoryOfUpdateContents);
            File targetLocation = new File(destDirectory);

            FileUtils.copyDirectory(sourceLocation, targetLocation);

            if (_postScriptCommand != null) {
                Path postScriptDestPath = Paths.get(destDirectory + File.separator + _postScriptCommand);
                File postScript = new File(postScriptDestPath.toUri());

                if (postScript.exists()) {
                    startProcess(postScriptDestPath.toString());
                    Files.deleteIfExists(postScriptDestPath);
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    private void stopCheckingTask(){
        if(_timer == null) return;
        _timer.cancel();
    }

    private String getFileNameWithoutExtension(File file){
        String fileNameWithoutExtension = null;
        try {
            String name = file.getName();
            fileNameWithoutExtension = name.replaceFirst("[.][^.]+$", "");
        } catch (Exception e) {
            //todo Вызвать показ ошбики
        }
        return fileNameWithoutExtension;
    }

    private void startProcess(String filePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(filePath);
        try{
        Process process = processBuilder.start();
        process.waitFor();
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
