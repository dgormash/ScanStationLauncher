import ru.sigmait.exceptions.ProcessException;
import ru.sigmait.processmanagement.ProcessManager;

import java.io.File;
import java.io.IOException;

public class Main {
    private static final String APPLICATION = "java-runtime/bin/java";
    private static final String JARFILENAME = "scanstation-1.0.jar";

    public static void main(String[] args) {
        try{
            File workingDirectory = new File(System.getProperty("user.dir"));
            File[] list = workingDirectory.listFiles();
            for (File file:list) {
                System.out.println(file);
            }
            System.out.println();
//            ProcessManager processManager = new ProcessManager(workingDirectory, "java-runtime/bin/java", "-jar", "scanstation-1.0.jar");
            ProcessManager processManager = new ProcessManager(workingDirectory, "java", "-jar", "sample407.jar", "407");

            processManager.runApplication();
        }catch(IOException e){
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } catch (ProcessException e) {
            System.err.println(e.getMessage());
        }
    }
}
