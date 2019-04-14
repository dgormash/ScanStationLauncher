package ru.sigmait.updatemanagement;

import ru.sigmait.ftpmanagement.FtpConfig;
import ru.sigmait.timertasks.FtpMonitoringTimerTask;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateManager {

    private FtpConfig _ftpConfig;

    public UpdateManager(FtpConfig ftpConfig){
        _ftpConfig = ftpConfig;
    }

    public void startCheckingUpdates(){
        TimerTask ftpMonitoringTimerTask = new FtpMonitoringTimerTask(_ftpConfig);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(ftpMonitoringTimerTask,0, _ftpConfig.get_repeatAfter());
    }

    public void stopCheckingUpdates(){

    }

    public void startUpdate(){

    }
}
