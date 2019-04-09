import ru.sigmait.configmanagement.LaunchConfigManager;
import ru.sigmait.environmentmanagement.EnvironmentManager;
import ru.sigmait.exceptions.ProcessException;
import ru.sigmait.processmanagement.ProcessManager;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static RandomAccessFile _randomFile = null;
    private static FileChannel _channel = null;
    private static FileLock _fileLock = null;

    public static void main(String[] args) {
        lock();
        LaunchConfigManager configManager = new LaunchConfigManager();
        ProcessManager processManager = null;
        EnvironmentManager environmentManager = new EnvironmentManager();
        File workingDirectory = new File(System.getProperty("user.dir"));

        try {
            List<String> processBuilderParams = Arrays.asList(configManager.getLaunchCommand().split("\\s"));
            String javaExecutable = processBuilderParams.get(0);
            String jarFile = processBuilderParams.get(2);

            if(!environmentManager.isPathExists(workingDirectory + "/" + jarFile)){
                throw new FileNotFoundException("Не найден файл " + jarFile);
            }

            if(!javaExecutable.equalsIgnoreCase("java")){
               if(!environmentManager.isPathExists(workingDirectory + "/" + javaExecutable + ".exe")){
                   processBuilderParams.set(0, "java");
               }
            }

            processManager = new ProcessManager(workingDirectory, processBuilderParams);
            processManager.runApplication();

        }catch(IOException e){
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка запуска скан-станции",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка запуска скан-станции",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ProcessException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка в процессе работы скан-станции",
                    JOptionPane.ERROR_MESSAGE);
        }catch (NullPointerException e){
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка чтения файла конфигурации",
                    JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if(processManager != null) {
                processManager.terminateApplication();
            }
            releaseLock();
            System.exit(0);
        }
    }

    private static void releaseLock(){
        try{
            _randomFile.close();
            _channel.close();

            File lockFile = new File("lock");
            lockFile.delete();
        }catch (IOException e)
        {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка запуска скан-станции",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void lock(){

        try{
            _randomFile = new RandomAccessFile("lock","rw");
            _channel = _randomFile.getChannel();
            _fileLock = _channel.tryLock();
            if(_fileLock == null)
                System.exit(0);
        }catch( Exception e ) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка запуска скан-станции",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}
