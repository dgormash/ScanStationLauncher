package ru.sigmait.ftpmanagement;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class FtpManager {

private FtpClient _client;

    public FtpManager(FtpConfig config) throws IOException, FtpProtocolException {
        String address = config.get_ftpAddress();
        int port = Integer.parseInt(config.get_ftpPort());
        //Создали экземпляр ftp-клиента.
        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);
        initFtpClient(inetSocketAddress);
        initFtpClientConfiguration(config);
    }

    public List<String> dir(){
        List<String> directoryContents = new ArrayList<>();
        return directoryContents;
    }

    public void downloadFile(String filePath){

    }

    private void connect(){

    }

    private void disconnect(){

    }

    private void initFtpClient(InetSocketAddress inetSocketAddress) throws IOException, FtpProtocolException {
        _client = FtpClient.create(inetSocketAddress);
    }

    private void initFtpClientConfiguration(FtpConfig config){
//        String proxyAddress = config.get_proxyAddress();
//        if(proxyAddress != null && !proxyAddress.isEmpty()){
//            Proxy proxy = new Proxy(new InetSocketAddress());
//            _client.setProxy(proxy);
//        }

    }
}
