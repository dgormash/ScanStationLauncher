package ru.sigmait.ftpmanagement;

public class FtpConfig {
    private String _ftpAddress;
    private int _ftpPort;
    private String _ftpLogin;
    private String _ftpPassword;
    private boolean _useProxy;
    private String _proxyAddress;
    private String _proxyLogin;
    private String _proxyPassword;
    private boolean _useBinary;
    private boolean _usePassive;
    private boolean _keepAlive;
    private boolean _enableSsl;
    private String _fileNameMask;
    private int _repeatAfter;
    private int _timeout;

    public String get_ftpAddress() {
        return _ftpAddress;
    }

    public void set_ftpAddress(String ftpAddress) {
        this._ftpAddress = ftpAddress;
    }

    public int get_ftpPort() {
        return _ftpPort;
    }

    public void set_ftpPort(int ftpPort) {
        this._ftpPort = ftpPort;
    }

    public String get_ftpLogin() {
        return _ftpLogin;
    }

    public void set_ftpLogin(String ftpLogin) {
        this._ftpLogin = ftpLogin;
    }

    public String get_ftpPassword() {
        return _ftpPassword;
    }

    public void set_ftpPassword(String ftpPassword) {
        this._ftpPassword = ftpPassword;
    }

    public boolean get_useProxy() {
        return _useProxy;
    }

    public void set_useProxy(boolean useProxy) {
        this._useProxy = useProxy;
    }

    public String get_proxyAddress() {
        return _proxyAddress;
    }

    public void set_proxyAddress(String proxyAddress) {
        this._proxyAddress = proxyAddress;
    }

    public String get_proxyLogin() {
        return _proxyLogin;
    }

    public void set_proxyLogin(String proxyLogin) {
        this._proxyLogin = proxyLogin;
    }

    public String get_proxyPassword() {
        return _proxyPassword;
    }

    public void set_proxyPassword(String proxyPassword) {
        this._proxyPassword = proxyPassword;
    }

    public boolean get_useBinary() {
        return _useBinary;
    }

    public void set_useBinary(boolean useBinary) {
        this._useBinary = useBinary;
    }

    public boolean get_usePassive() {
        return _usePassive;
    }

    public void set_usePassive(boolean usePassive) {
        this._usePassive = usePassive;
    }

    public boolean get_keepAlive() {
        return _keepAlive;
    }

    public void set_keepAlive(boolean keepAlive) {
        this._keepAlive = keepAlive;
    }

    public boolean get_enableSsl() {
        return _enableSsl;
    }

    public void set_enableSsl(boolean enableSsl) {
        this._enableSsl = enableSsl;
    }

    public String get_fileNameMask() {
        return _fileNameMask;
    }

    public void set_fileNameMask(String fileNameMask) {
        this._fileNameMask = fileNameMask;
    }

    public int get_repeatAfter() {
        return _repeatAfter;
    }

    public void set_repeatAfter(int repeatAfter) {
        this._repeatAfter = repeatAfter;
    }

    public int get_Timeout() {
        return _timeout;
    }

    public void set_Timeout(int timeout) {
        this._timeout = timeout;
    }
}
