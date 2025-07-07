package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    @Test
    void shouldWorkWithEmptyFile() throws IOException {
        File file = File.createTempFile("empty_file", ".csv");

        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач пуст");
    }

    @Test
    void shouldWorkWithTasksFromFile() throws IOException {
        File path = new File("E:\\Coding\\Codes\\Java\\Yandex_Practicum\\second_module\\7_seventh_sprint\\final_task\\java-kanban\\src\\recources");
        File file = File.createTempFile("some_tasks", ".csv", path);
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        Task task_2 = new Task("task_2", "task_2 desk", TaskStatus.NEW);
        taskManager.createTask(task_2);

        Epic epic_with_subs = new Epic("epic_with_subs", "desc of epic with subs");
        taskManager.createEpic(epic_with_subs);
        Epic epic_with_no_subs = new Epic("epic_with_no_subs", "desc of epic with no subs");
        taskManager.createEpic(epic_with_no_subs);
        Epic new_epic_with_no_subs = new Epic("new_epic_with_no_subs", "desc of REAL epic with no subs");
        taskManager.createEpic(new_epic_with_no_subs);

        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1 desc", epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_1);
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2 desc", epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_2);

        Task updatedTask = new Task(task_1.getTaskId(), "new_task_1", task_1.getTaskDesc(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        // загрузка из файла
        FileBackedTaskManager fromFile = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, fromFile.getAllTasks().size());
        assertEquals(3, fromFile.getAllEpics().size());
        assertEquals(2, fromFile.getAllSubtasks().size());

        // проверка корректной генерации айди после загрузки из файла
        Task task_3 = new Task("task_3", "task_3 desc", TaskStatus.NEW);
        taskManager.createTask(task_3);
        Task task_4 = new Task("task_4", "task_4 desc", TaskStatus.NEW);
        taskManager.createTask(task_4);

        assertEquals(100003, task_3.getTaskId());

    }
}
