package ru.sigmait.timertasks;

import ru.sigmait.applicationmanagement.NotificationResultListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class UserNotificationTimerTask extends TimerTask {

    private List<NotificationResultListener> _listeners = new ArrayList<>();

    @Override
    public void run() {
        notifyUser();
    }

    private  void notifyUser(){
        Object [] dialogOptions = {"Установить", "Напомнить позже"};
        int dialogResult =  JOptionPane.showOptionDialog(null,
                "Загружены обновления для приложения \"Скан-станция\"",
                "Обновление системы",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, dialogOptions, dialogOptions[0]);
        for (NotificationResultListener listener:_listeners) {
            listener.NotifyAboutUsersChoice(dialogResult);
        }
    }

    public void addListener (NotificationResultListener listener){
        _listeners.add(listener);
    }
}

