package ru.sigmait.scanstationmanagement;

import ru.sigmait.exceptions.ProcessException;
import ru.sigmait.timertasks.ScanStationTimerTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanStationManager {
    Timer _timer = new Timer();
    TimerTask _task;

    public ScanStationManager(File workingDirectory, List<String> processBuilderParams) {
        _task = new ScanStationTimerTask(workingDirectory, processBuilderParams);
    }

    public void runApplication() {
        _timer.schedule(_task, 0);
    }

    public void terminateApplication(){
        if(_timer == null) return;

        _timer.cancel();
    }
}
