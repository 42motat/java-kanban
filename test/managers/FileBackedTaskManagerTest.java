package managers;

//import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest {
    @Test
    void shouldWorkWithEmptyFile() throws IOException {
        File file = File.createTempFile("empty_file", ".csv");

        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач пуст");
    }

    @Test
    void shouldWorkWithTasksFromFile() throws IOException {
//        File path = new File("E:\\Coding\\Codes\\Java\\Yandex_Practicum\\second_module\\8_eighth_sprint\\final_proj\\java-kanban\\src\\resources");
//        File file = File.createTempFile("some_tasks", ".csv", path);
        File file = File.createTempFile("some_tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);

        LocalDateTime startTask2 = LocalDateTime.of(2025, 7, 22, 12, 52);
        Task task_2 = new Task("task_2", "task_2 desk", TaskStatus.NEW, startTask2, Duration.of(20, ChronoUnit.MINUTES));
        taskManager.createTask(task_2);

        Epic epic_with_subs = new Epic("epic_with_subs", "desc of epic with subs");
        taskManager.createEpic(epic_with_subs);
        Epic epic_with_no_subs = new Epic("epic_with_no_subs", "desc of epic with no subs");
        taskManager.createEpic(epic_with_no_subs);

        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1 desc", epic_with_subs.getEpicId(), LocalDateTime.now().plusMinutes(180), Duration.of(60, ChronoUnit.MINUTES));
        taskManager.createSubtask(epic_with_subs, subtask_1);
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2 desc", epic_with_subs.getEpicId(), LocalDateTime.now().plusMinutes(300), Duration.of(60, ChronoUnit.MINUTES));
        taskManager.createSubtask(epic_with_subs, subtask_2);

        // загрузка из файла
        FileBackedTaskManager fromFile = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, fromFile.getAllTasks().size());
        assertEquals(2, fromFile.getAllEpics().size());
        assertEquals(2, fromFile.getAllSubtasks().size());
    }


}
