package ru.sigmait.scanstationmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ScanStationManager {
    private Process _process;
    private ProcessBuilder _processBuilder;
    private List<ScanStationErrorListener> _errorListeners = new ArrayList<>();

    public ScanStationManager(File workingDirectory, List<String> processBuilderParams) {
        _processBuilder = new ProcessBuilder(processBuilderParams);
        _processBuilder.directory(workingDirectory);
    }

    public void runApplication() throws IOException, InterruptedException {
        this.startProcess();
    }

    public void terminateApplication(){
        this.stopProcess();
    }


    private void startProcess() throws IOException, InterruptedException {
        Instant startTime = Instant.now();
        _process = _processBuilder.start();

        BufferedReader bufferReader = new BufferedReader(
                new InputStreamReader(
                        _process.getInputStream()));

        String line = null;
        while((line = bufferReader.readLine()) != null){
        }
        int exitResult = _process.waitFor();

        Instant endTime = Instant.now();
        long timeDifference = Duration.between(startTime, endTime).getSeconds();

        if(exitResult == 400) return;

        if(exitResult == 407 || timeDifference >= 600){
            this.startProcess();
        }
        else{
            notifyErrorListeners("Ошибка в процессе работы программы \"Скан-станция\".\nПожалуйста, обратитесь к Администратору.");
        }
    }

    private void stopProcess(){
        if(_process == null) return;

        _process.destroy();
    }

    private void notifyErrorListeners(String message){
        for (ScanStationErrorListener listener:_errorListeners) {
            listener.ErrorOccurred(message);
        }
    }

    public void addListeners(ScanStationErrorListener listener){
        _errorListeners.add(listener);
    }
}
