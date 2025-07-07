package managers;

//import java.io.File;
//import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
//        return new FileBackedTaskManager(new File("resources/saved_tasks.csv"));
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
        }
}
