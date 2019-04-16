package ru.sigmait.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import ru.sigmait.misc.FtpConfig;

public class FtpManager {

private FTPClient _client;
private FtpConfig _config;
private String _serverAddress;
private String _login;
private String _password;
private int _serverPort;

    public FtpManager(FtpConfig config) {
        _config = config;
        _login = config.get_ftpLogin();
        _password = config.get_ftpPassword();
        _serverAddress = _config.get_ftpAddress();
        _serverPort = config.get_ftpPort();
    }

    public List<String> dir() throws IOException {

       boolean isLoggedin =  this.connect();
        List<String> directoryContents = null;
        if(isLoggedin){
           _client.list(_serverAddress);

       directoryContents = Arrays.asList(_client.listNames());

       this.disconnect();
       }
       return directoryContents;
    }

    public boolean downloadFile(String source, String destination) throws IOException {
        boolean isLoggedin =  this.connect();
        boolean downloadResult = false;
        if(isLoggedin){
            boolean useBinary = _config.get_useBinary();
            if(useBinary){
                _client.setFileType(FTP.BINARY_FILE_TYPE);
                _client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            }

            boolean usePassive =  _config.get_usePassive();
            if(usePassive){
                _client.enterLocalPassiveMode();
            }
            _client.setControlEncoding("UTF-8");
            File destFile = new File(destination.trim());
            FileOutputStream destFileOutputStream = new FileOutputStream(destFile);
            InputStream inputStream = _client.retrieveFileStream(destFile.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(destFile);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.flush();

            downloadResult = _client.completePendingCommand();

            this.disconnect();
            destFileOutputStream.close();
        }

        return downloadResult;
    }

    private boolean connect() throws IOException {
        if(_client == null) {
            _client = getNewClient();
        }
        _client.connect(_serverAddress, _serverPort);
        return _client.login(_login, _password);
    }

    private void disconnect() throws IOException {
        if(_client == null)
            return;

        if(_client.isConnected()){
            _client.disconnect();
            _client = null;
        }
    }

    private FTPClient getNewClient(){
        FTPClient client = new FTPClient();
        int connectTimeOut = _config.get_Timeout();
        client.setConnectTimeout(connectTimeOut);
        client.setAutodetectUTF8(true);

        return client;
    }
}
