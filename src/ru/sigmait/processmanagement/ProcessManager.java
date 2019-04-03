package ru.sigmait.processmanagement;

import ru.sigmait.exceptions.ProcessException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessManager {
    private Process _process;
    private ProcessBuilder _processBuilder;

    public ProcessManager(File workingDirectory, String... processBuilderParams) {
        _processBuilder = new ProcessBuilder(processBuilderParams);
        _processBuilder.directory(workingDirectory);
    }

    public void runApplication() throws IOException, InterruptedException, ProcessException {
        this.startProcess();
    }

    public void terminateApplication(){
        this.stopProcess();
    }


    private void startProcess() throws IOException, InterruptedException, ProcessException {
        _process = _processBuilder.start();

        BufferedReader br=new BufferedReader(
                new InputStreamReader(
                        _process.getInputStream()));
        String line;
        while((line=br.readLine())!=null){
            System.err.println(line);
        }

        int exitResult = _process.waitFor();

        if(exitResult == 407){
            this.startProcess();
        }
        else{
            throw new ProcessException("Программа \"Скан-станция\" внезапно завершила свою работу.\nПожалуйста, обратитесь к Администратору.");
        }
    }

    private void stopProcess(){
        if(_process == null) return;

        _process.destroy();
    }
}
