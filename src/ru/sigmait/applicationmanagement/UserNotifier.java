package ru.sigmait.applicationmanagement;

import ru.sigmait.timertasks.UserNotificationTimerTask;
import utils.BackgroundStatus;

import java.util.Timer;

public class UserNotifier {

    private final long NOTIFICATION_INTERVAL = 2L;
    private Timer _timer;
    private int _currentStatus;
    public int getStatus(){
        return _currentStatus;
    }

    public  void startUserNotification(){
        UserNotificationTimerTask task = new UserNotificationTimerTask();
        _timer = this.getNewTimer();
        _timer.scheduleAtFixedRate(task, 0, NOTIFICATION_INTERVAL );
        _currentStatus = BackgroundStatus.STARTED;
    }

    public  void stopUserNotification(){
        if(_timer != null){
            _timer.cancel();
            _timer = null;
        }
        _currentStatus = BackgroundStatus.STOPPED;
    }

    private Timer getNewTimer(){
        return new Timer();
    }
}
