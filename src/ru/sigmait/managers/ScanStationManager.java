package ru.sigmait.managers;

import ru.sigmait.lilsteners.ScanStationErrorListener;
import ru.sigmait.misc.BackgroundStatus;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class ScanStationManager extends Thread {
    private static Process _process;
    private static ProcessBuilder _processBuilder;
    private static List<ScanStationErrorListener> _errorListeners = new ArrayList<>();
    private static int _currentStatus;
    private static Semaphore _semaphore;
    private static CountDownLatch _countDownLatch;

    public void setSemaphore(Semaphore semaphore){
        _semaphore = semaphore;
    }

    public void setLatch(CountDownLatch latch){
        _countDownLatch = latch;
    }

    public int getStatus(){
        return _currentStatus;
    }

    public ScanStationManager(File workingDirectory, List<String> processBuilderParams) {
        _processBuilder = new ProcessBuilder(processBuilderParams);
        _processBuilder.directory(workingDirectory);
    }

    @Override
    public void run()
    {
        startProcess();
    }

    public void runApplication() {
        _suppressErrorMessage = false;
        _currentStatus = BackgroundStatus.STARTED;
        startProcess();
    }

    public void terminateApplication(){
        this.stopProcess();
    }


    private static void startProcess()  {
        try {
            //_semaphore.acquire();
            Instant startTime = Instant.now();
            _process = _processBuilder.start();

            BufferedReader bufferReader = new BufferedReader(
                    new InputStreamReader(
                            _process.getInputStream()));

            String line = null;
            while ((line = bufferReader.readLine()) != null) {
            }
            int exitResult = _process.waitFor();

            Instant endTime = Instant.now();
            long timeDifference = Duration.between(startTime, endTime).getSeconds();

            if(_currentStatus == BackgroundStatus.STOPPED){
                return;
            }
            if (exitResult == 400 || _suppressErrorMessage) {
                _currentStatus = BackgroundStatus.STOPPED;;
                _countDownLatch.countDown();
                return;
            }

            if (exitResult == 407 || timeDifference >= 600) {
                startProcess();
            } else {
                _currentStatus = BackgroundStatus.STOPPED;
                notifyErrorListeners("Ошибка в процессе работы программы \"Скан-станция\".\nПожалуйста, обратитесь к Администратору.");
                _countDownLatch.countDown();
                //_semaphore.release();
            }
        }catch (IOException e){

        }catch (InterruptedException e){

        }
    }

    private static boolean _suppressErrorMessage;
    private void stopProcess(){
        _suppressErrorMessage = true;
        _currentStatus = BackgroundStatus.STOPPED;
        if(_process == null) return;

        _process.destroy();
    }

    private static void notifyErrorListeners(String message){
        for (ScanStationErrorListener listener:_errorListeners) {
            listener.ScanStationErrorOccurred(message);
        }
    }

    public static void setErrorListener(ScanStationErrorListener listener){
        _errorListeners.add(listener);
    }
}
