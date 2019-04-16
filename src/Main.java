import ru.sigmait.managers.ApplicationManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Main {

    private static RandomAccessFile _randomFile = null;
    private static FileChannel _channel = null;
    private static FileLock _fileLock = null;

    public static void main(String[] args) {
        lock();
        ApplicationManager applicationManager = null;

        try {
            applicationManager =  new ApplicationManager();

        }catch(Exception e){
            System.err.println(e.getMessage());
        }finally {
            if(applicationManager != null) {
                applicationManager.stopBackgroundOperations();
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
            System.exit(0);
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
