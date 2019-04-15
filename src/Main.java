import ru.sigmait.applicationmanagement.ApplicationManager;
import ru.sigmait.configmanagement.ConfigManager;
import ru.sigmait.environmentmanagement.EnvironmentManager;
import ru.sigmait.exceptions.ProcessException;
import ru.sigmait.scanstationmanagement.ScanStationManager;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static RandomAccessFile _randomFile = null;
    private static FileChannel _channel = null;
    private static FileLock _fileLock = null;

    public static void main(String[] args) {
        lock();


        ScanStationManager processManager = null;
        EnvironmentManager environmentManager = new EnvironmentManager();
        File workingDirectory = new File(System.getProperty("user.dir"));

        try {
            ApplicationManager applicationManager = new ApplicationManager();
            applicationManager.start();
//            ConfigManager configManager = new ConfigManager();
//
//            //todo Получать конфигурацию ftp-сервера
//            List<String> launchCommandData = new ArrayList();
//            launchCommandData.addAll(Arrays.asList(configManager.getLaunchCommand().split("\\s")));
//            String javaExecutable = launchCommandData.get(0);
//            String jarFile = launchCommandData.get(2);
//
//            if(!environmentManager.isPathExists(workingDirectory + "/" + jarFile)){
//                throw new FileNotFoundException("Не найден файл " + jarFile);
//            }
//
//            if(!javaExecutable.equalsIgnoreCase("java")){
//               if(!environmentManager.isPathExists(workingDirectory + "/" + javaExecutable + ".exe")){
//                   launchCommandData.set(0, "java");
//               }
//            }
//
//            //Добавляем classpath
//            String classpathValue = configManager.getClassPath();
//            launchCommandData.add(1, "-classpath");
//            launchCommandData.add(2, "\"" + classpathValue + "\"");
//
//            processManager = new ScanStationManager(workingDirectory, launchCommandData);
//            processManager.runApplication();

        }

        catch(IOException e){
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
        catch (IllegalAccessException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка чтения файла конфигурации",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InvocationTargetException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "Ошибка чтения файла конфигурации",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (NumberFormatException e) {
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
