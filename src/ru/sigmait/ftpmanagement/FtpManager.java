package ru.sigmait.ftpmanagement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

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

    public void downloadFile(String source, String destination) throws IOException {
        boolean isLoggedin =  this.connect();
        if(isLoggedin){
            boolean useBinary = _config.get_useBinary();
            if(useBinary){
                _client.setFileType(FTP.BINARY_FILE_TYPE);
            }
            FileOutputStream destFileOutputStream = new FileOutputStream(destination);
            _client.retrieveFile(source, destFileOutputStream);
        }

        this.disconnect();
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

    private FTPClient getNewClient() throws IOException {
        FTPClient client = new FTPClient();
        int connectTimeOut = _config.get_Timeout();
        client.setConnectTimeout(connectTimeOut);

        return client;
    }
}
