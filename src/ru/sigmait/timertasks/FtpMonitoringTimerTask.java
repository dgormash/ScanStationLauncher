package ru.sigmait.timertasks;

import ru.sigmait.environmentmanagement.FileDownloadedListener;
import ru.sigmait.ftpmanagement.FtpConfig;
import ru.sigmait.ftpmanagement.FtpErrorListener;
import ru.sigmait.ftpmanagement.FtpManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpMonitoringTimerTask extends TimerTask {
    private FtpConfig _config;
    private String _currentVersion;
    private String _copiedFileDestinationFolder;
    private List<FileDownloadedListener> _ftpEventsListeners = new ArrayList<>();
    private List<FtpErrorListener> _errorListeners = new ArrayList<>();

    public void setCopiedFileDestinationFolder(String path){
        _copiedFileDestinationFolder = path;
    }

    public FtpMonitoringTimerTask(FtpConfig config){
        _config = config;
    }

    @Override
    public void run() {
        try {
            startTask();
        } catch (IOException e) {
            notifyErrorListeners(e.getMessage());
        }
    }

    private void startTask() throws IOException {
        //todo Передать сюда текущую версию скан-станции для сравнения с найденными обновлениями
        FtpManager ftpManager = new FtpManager(_config);
        List<String> files = ftpManager.dir();
        String fullInstallerFileMask = _config.get_fullInstallerMask();
        String patchFileMask = _config.get_patchInstallerMask();
        Pattern fullVersionNumberPattern = Pattern.compile( "\\d+\\.\\d+");
        TreeMap<Float, String> fullInstallers = new TreeMap<>();

        String fullInstallerPattern = "(?i)" + fullInstallerFileMask.replaceAll("\\[:version\\]", "\\\\d+\\\\.\\\\d+");

        for (String fileName:files) {
            if(Pattern.matches(fullInstallerPattern, fileName)){

                Matcher match = fullVersionNumberPattern.matcher(fileName);
                float version = 0;
                while(match.find()){
                    version = Float.parseFloat(fileName.substring(match.start(), match.end()));
                }

                fullInstallers.put(version, fileName);
            }
        }

        float maxFullInstallerVersionFloat = fullInstallers.lastKey();
        String maxFullInstallerVersionString = Float.toString(maxFullInstallerVersionFloat);

        if(maxFullInstallerVersionString.equals(_currentVersion))
            return;

        String searchedPatchInstallerFileName = patchFileMask.replaceFirst("\\[:currentVersion\\]", /*_currentVersion*/"1.0")
                .replaceFirst("\\[:newVersion\\]", maxFullInstallerVersionString);

        String destinationFilePath;
        if (files.contains(searchedPatchInstallerFileName)) {
            destinationFilePath = _copiedFileDestinationFolder  + searchedPatchInstallerFileName;
        }else{
            destinationFilePath = _copiedFileDestinationFolder + fullInstallers.get(maxFullInstallerVersionFloat);
        }

        File updateFile = new File(destinationFilePath);
        if(updateFile.exists()) return;

        boolean downloadResult = ftpManager.downloadFile(searchedPatchInstallerFileName, destinationFilePath);
            if(downloadResult){
                notifyListeners(destinationFilePath);
            }

    }

    public  void addListener(FileDownloadedListener listener){
        _ftpEventsListeners.add(listener);
    }

    private void notifyListeners(String filePath){
        for (FileDownloadedListener listener: _ftpEventsListeners) {
            listener.FileDownloaded(filePath);
        }
    }

    public void addErrorListener(FtpErrorListener listener){
        _errorListeners.add(listener);
    }

    private void notifyErrorListeners(String message){
        for (FtpErrorListener listener: _errorListeners) {
            listener.ErrorOccurred(message);
        }
    }
}
