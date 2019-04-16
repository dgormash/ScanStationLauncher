package ru.sigmait.utils;

import ru.sigmait.lilsteners.NotificationResultListener;
import ru.sigmait.timertasks.UserNotificationTimerTask;
import ru.sigmait.misc.BackgroundStatus;

import java.util.Timer;

public class UserNotifier {

    private final long NOTIFICATION_INTERVAL = 120000L;
    private final long DELAY = 120000L;
    private Timer _timer;
    private UserNotificationTimerTask _task = new UserNotificationTimerTask();
    private int _currentStatus;
    private NotificationResultListener _resultListener;

    public void setNotificationResultListener(NotificationResultListener listener){
        _resultListener = listener;
        _task.addListener(_resultListener);
    }

    public void removeListener(){
        _resultListener = null;
        _task.removeListeners();
    }
    public int getStatus(){
        return _currentStatus;
    }

    public  void startUserNotification(){
        _timer = this.getNewTimer();
        _timer.scheduleAtFixedRate(_task, DELAY, NOTIFICATION_INTERVAL );
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

    public void notifyOnce(){
        _task.run();
    }
}
