package ru.sigmait.timertasks;

import ru.sigmait.ftpmanagement.FtpConfig;
import ru.sigmait.ftpmanagement.FtpManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpMonitoringTimerTask extends TimerTask {
    private FtpConfig _config;

    public FtpMonitoringTimerTask(FtpConfig config){
        _config = config;
    }

    @Override
    public void run() {
        try {
            startTask();
        } catch (IOException e) {
            //todo вызов метода передачи информации об ошибке
            e.printStackTrace();
        }
    }

    private void startTask() throws IOException {
        FtpManager ftpManager = new FtpManager(_config);
        List<String> files = ftpManager.dir();
        //todo обработать SocketTimeoutExcepiton

//        List<String> fullInstallers = new ArrayList<>();
//        List<String> patches = new ArrayList<>();
        Pattern fullVersPattern = Pattern.compile( "\\d.+\\.\\d.+");
        TreeMap<Float, String> fullInstallers = new TreeMap<>();
        TreeMap<Float, String> patches = new TreeMap<>();

        String fullInstallerPattern = "(?)" + _config.get_fullInstallerMask();
        String patchInstallerPattern = "(?)" + _config.get_patchInstallerMask();

        for (String fileName:files) {

            if(Pattern.matches(fullInstallerPattern, fileName)){
                //todo получить список файлов инсталляторов, найти среди них максимальную версию, на основе текущей версии и максимальной версии сгенерировать имя патча с текущей на максимальную
                //todo найти в списке файлов files файл, соответствующий сгенерированному имени
                Matcher match = fullVersPattern.matcher(fileName);
                float version = 0;
                while(match.find()){
                    version = Float.parseFloat(fileName.substring(match.start(), match.end()));
                }

                fullInstallers.put(version, fileName);
            }

//            if (Pattern.matches(patchInstallerPattern, fileName)) {
//                patches.add(fileName);
//            }
        }
        System.out.println();
    }
}
