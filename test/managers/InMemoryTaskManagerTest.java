package managers;

// все тесты с assertj заменены на junit, но оставлены в комментариях

//import org.assertj.core.api.Assertions;
import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    public void generalTestsForCreatingAndDeletingTasks() {
        taskManager.createTask(new Task("name", "desc", TaskStatus.NEW));
        // проверка наличия в общем списке тасков созданного таска
//        Assertions.assertThat(taskManager.getAllTasks()).hasSize(1);
        Assertions.assertEquals(1, taskManager.getAllTasks().size());
        taskManager.deleteAllTasks();
//        Assertions.assertThat(taskManager.getAllTasks()).hasSize(0);
        Assertions.assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void generalTestsForCreatingAndDeletingEpics() {
        taskManager.createEpic(new Epic("epic_name", "epic_desc"));
        // проверка наличия в общем списке эпиков созданного эпика
//        Assertions.assertThat(taskManager.getAllEpics()).hasSize(1);
        Assertions.assertEquals(1, taskManager.getAllEpics().size());
        // проверка отсутствия в общем списке эпиков ранее созданного эпика
        taskManager.deleteAllEpics();
//        Assertions.assertThat(taskManager.getAllEpics()).hasSize(0);
        Assertions.assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void generalTestsForCreatingAndDeletingSubtasksFromEpic() {
        Epic epic_with_subs = new Epic("epic_name", "epic_desc");
        taskManager.createEpic(epic_with_subs);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_with_subs.getEpicId(), LocalDateTime.of(2025, 7, 21, 13, 24), Duration.of(30, ChronoUnit.MINUTES));
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2_desc", epic_with_subs.getEpicId(), LocalDateTime.of(2025, 7, 22, 13, 24), Duration.of(30, ChronoUnit.MINUTES));
        Subtask subtask_3 = new Subtask("subtask_3", "subtask_3_desc", epic_with_subs.getEpicId(), LocalDateTime.of(2025, 7, 23, 13, 24), Duration.of(30, ChronoUnit.MINUTES));
        taskManager.createSubtask(epic_with_subs, subtask_1);
        taskManager.createSubtask(epic_with_subs, subtask_2);
        taskManager.createSubtask(epic_with_subs, subtask_3);
        // проверка наличия сабтасок в общем списке TaskManager'а
//        Assertions.assertThat(taskManager.getAllSubtasks()).hasSize(3);
        Assertions.assertEquals(3, taskManager.getAllSubtasks().size());
        // проверка удаления сабтасок в общем списке TaskManager'а
        taskManager.deleteAllSubtasksOfEpic(epic_with_subs.getTaskId());
//        Assertions.assertThat(taskManager.getAllSubtasks()).hasSize(0);
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size());
        // проверка удаления сабтасок в списке эпика
        Assertions.assertTrue(taskManager.getSubtasksByEpicId(epic_with_subs.getEpicId()).isEmpty());
    }

    @Test
    public void shouldReturnTrueIfTaskEqualsUpdatedTask() {
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        Task task_1_updated = new Task(task_1.getTaskId(), "task_1 updated", "desc updated", task_1.getTaskStatus());
        taskManager.updateTask(task_1_updated);
        Assertions.assertEquals(task_1, task_1_updated);
    }

    @Test
    public void shouldReturnTrueIfEpicEqualsUpdatedEpic() {
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createTask(epic_1);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_1.getEpicId(), LocalDateTime.of(2025, 7, 22, 13, 24), Duration.of(30, ChronoUnit.MINUTES));
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2_desc", epic_1.getEpicId(), LocalDateTime.of(2025, 7, 25, 13, 24), Duration.of(30, ChronoUnit.MINUTES));
        taskManager.createSubtask(epic_1, subtask_1);
        taskManager.createSubtask(epic_1, subtask_2);
        Epic epic_1_updated = new Epic(epic_1, epic_1.getEpicId(), "task_1 updated", "desc updated");
        taskManager.updateTask(epic_1_updated);
        Assertions.assertEquals(epic_1, epic_1_updated);
    }

    @Test
    public void shouldNotAddEpicAsASubtaskToItself() {
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createTask(epic_1);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_1.getEpicId());
        taskManager.createSubtask(epic_1, subtask_1);
//        Assertions.assertThat(epic_1.getSubtasks(epic_1).size()).isEqualTo(1);
        Assertions.assertEquals(1, epic_1.getSubtasks(epic_1).size());
    }

    @Test
    public void GeneralHistoryTests() {
//        Assertions.assertThat(taskManager.getHistory()).isEmpty();
        Assertions.assertTrue(taskManager.getHistory().isEmpty());
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        taskManager.getTaskById(task_1.getTaskId()); // #1
//        Assertions.assertThat(taskManager.getHistory()).isNotEmpty();
        Assertions.assertFalse(taskManager.getHistory().isEmpty());
//        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(1);
        Assertions.assertEquals(1, taskManager.getHistory().size());
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId()); // #4
//        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(1);
        Assertions.assertEquals(1, taskManager.getHistory().size());
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createEpic(epic_1);
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId()); // #10
        // после нескольких вызовов таск и эпик в списке истории остаются только две записи
//        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(2);
        Assertions.assertEquals(2, taskManager.getHistory().size());
        taskManager.getTaskById(task_1.getTaskId()); // #10
        // последним был добавлен task_1
//        Assertions.assertThat(taskManager.getHistory().getLast()).isEqualTo(task_1);
        Assertions.assertEquals(taskManager.getHistory().getLast(), task_1);
        taskManager.getTaskById(task_1.getTaskId()); // first
        taskManager.getEpicById(epic_1.getEpicId()); // last
//        Assertions.assertThat(taskManager.getHistory().getFirst()).isEqualTo(task_1);
        Assertions.assertEquals(taskManager.getHistory().getFirst(), task_1);
//        Assertions.assertThat(taskManager.getHistory().getLast()).isEqualTo(epic_1);
        Assertions.assertEquals(taskManager.getHistory().getLast(), epic_1);
    }

    @Test
    public void ShouldNotAddTaskInHistoryIfTaskIsNull() {
//        Assertions.assertThat(taskManager.getHistory()).isEmpty();
        Assertions.assertTrue(taskManager.getHistory().isEmpty());
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);

        taskManager.getTaskById(task_1.getTaskId());
//        Assertions.assertThat(taskManager.getHistory()).isNotEmpty();
        Assertions.assertFalse(taskManager.getHistory().isEmpty());

        // обращение к несуществующей Task не вызывает NPE
        taskManager.getTaskById(42);
        // размер списка истории остаётся равен 1, значение null не добавляется в список с историей
//        Assertions.assertThat(taskManager.getHistory()).hasSize(1);
        Assertions.assertEquals(1, taskManager.getHistory().size());

        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createEpic(epic_1);

        taskManager.getEpicById(epic_1.getTaskId());
//        Assertions.assertThat(taskManager.getHistory()).hasSize(2);
        Assertions.assertEquals(2, taskManager.getHistory().size());

        taskManager.getEpicById(42);
//        Assertions.assertThat(taskManager.getHistory()).hasSize(2);
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void shouldThrowExceptionWhileCreatingATask() throws IOException {
//        File path = new File("E:\\Coding\\Codes\\Java\\Yandex_Practicum\\second_module\\8_eighth_sprint\\final_proj\\java-kanban\\src\\resources");
//        File file = File.createTempFile("some_tasks", ".csv", path);
                File file = File.createTempFile("some_tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        LocalDateTime startTask2 = LocalDateTime.of(2025, 7, 22, 12, 52);
        Task task_2 = new Task("task_2", "task_2 desk", TaskStatus.NEW, startTask2, Duration.of(20, ChronoUnit.MINUTES));
        taskManager.createTask(task_2);

        LocalDateTime startTask3 = LocalDateTime.of(2025, 7, 22, 12, 52);
        Task task_3 = new Task("task_3", "task_3 desc", TaskStatus.NEW, startTask3, Duration.of(20, ChronoUnit.MINUTES));
        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task_3));
    }
}
