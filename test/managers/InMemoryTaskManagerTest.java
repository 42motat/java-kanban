package managers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    public void generalTestsForCreatingAndDeletingTasks() {
        taskManager.createTask(new Task("name", "desc", TaskStatus.NEW));
        // проверка наличия в общем списке тасков созданного таска
        Assertions.assertThat(taskManager.getAllTasks()).hasSize(1);
        taskManager.deleteAllTasks();
        Assertions.assertThat(taskManager.getAllTasks()).hasSize(0);
    }

    @Test
    public void generalTestsForCreatingAndDeletingEpics() {
        taskManager.createEpic(new Epic("epic_name", "epic_desc"));
        // проверка наличия в общем списке эпиков созданного эпика
        Assertions.assertThat(taskManager.getAllEpics()).hasSize(1);
        // проверка отсутствия в общем списке эпиков ранее созданного эпика
        taskManager.deleteAllEpics();
        Assertions.assertThat(taskManager.getAllEpics()).hasSize(0);
    }

    @Test
    public void generalTestsForCreatingAndDeletingSubtasksFromEpic() {
        Epic epic_with_subs = new Epic("epic_name", "epic_desc");
        taskManager.createEpic(epic_with_subs);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_with_subs.getEpicId());
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2_desc", epic_with_subs.getEpicId());
        Subtask subtask_3 = new Subtask("subtask_3", "subtask_3_desc", epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_1);
        taskManager.createSubtask(epic_with_subs, subtask_2);
        taskManager.createSubtask(epic_with_subs, subtask_3);
        // проверка наличия сабтасок в общем списке TaskManager'а
        Assertions.assertThat(taskManager.getAllSubtasks()).hasSize(3);
        // проверка удаления сабтасок в общем списке TaskManager'а
        taskManager.deleteAllSubtasksOfEpic(epic_with_subs.getTaskId());
        Assertions.assertThat(taskManager.getAllSubtasks()).hasSize(0);
        // проверка удаления сабтасок в списке эпика
        Assertions.assertThat(epic_with_subs.getSubtasks(epic_with_subs)).isEmpty();
    }

    @Test
    public void shouldReturnTrueIfTaskEqualsUpdatedTask() {
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        Task task_1_updated = new Task(task_1.getTaskId(), "task_1 updated", "desc updated", task_1.getTaskStatus());
        taskManager.updateTask(task_1_updated);
        assertEquals(task_1, task_1_updated);
    }

    @Test
    public void shouldReturnTrueIfEpicEqualsUpdatedEpic() {
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createTask(epic_1);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_1.getEpicId());
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2_desc", epic_1.getEpicId());
        taskManager.createSubtask(epic_1, subtask_1);
        taskManager.createSubtask(epic_1, subtask_2);
        Epic epic_1_updated = new Epic(epic_1, epic_1.getEpicId(), "task_1 updated", "desc updated");
        taskManager.updateTask(epic_1_updated);
        assertEquals(epic_1, epic_1_updated);
    }

    @Test
    public void shouldnotAddEpicAsASubtaskToItself() {
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createTask(epic_1);
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1_desc", epic_1.getEpicId());
        taskManager.createSubtask(epic_1, subtask_1);
        Assertions.assertThat(epic_1.getSubtasks(epic_1).size()).isEqualTo(1);
        /* невозможно добавить эпик как сабтаск в самого себя, потому что:
         * 1. при создании объекта класса сабтаск явно указывается эпик, в который сабтаск попадает;
         * 2. при создании объекта класса эпик не существует конструктора,
         * который позволял бы добавлять эпик в другой эпик;
         */
    }

    @Test
    public void GeneralHistoryTests() {
        Assertions.assertThat(taskManager.getHistory()).isEmpty();
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        taskManager.getTaskById(task_1.getTaskId()); // #1
        Assertions.assertThat(taskManager.getHistory()).isNotEmpty();
        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(1);
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId()); // #4
        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(1);
        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createEpic(epic_1);
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getEpicById(epic_1.getEpicId());
        taskManager.getTaskById(task_1.getTaskId());
        taskManager.getTaskById(task_1.getTaskId()); // #10
        // после нескольких вызовов таск и эпик в списке истории остаются только две записи
        Assertions.assertThat(taskManager.getHistory().size()).isEqualTo(2);
        taskManager.getTaskById(task_1.getTaskId()); // #10
        // последним был добавлен task_1
        Assertions.assertThat(taskManager.getHistory().getLast()).isEqualTo(task_1);
        taskManager.getTaskById(task_1.getTaskId()); // #1
        taskManager.getEpicById(epic_1.getEpicId());
        Assertions.assertThat(taskManager.getHistory().getFirst()).isEqualTo(task_1);
        Assertions.assertThat(taskManager.getHistory().getLast()).isEqualTo(epic_1);
    }

    @Test
    public void ShouldNotAddTaskInHistoryIfTaskIsNull() {
        Assertions.assertThat(taskManager.getHistory()).isEmpty();
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);

        taskManager.getTaskById(task_1.getTaskId());
        Assertions.assertThat(taskManager.getHistory()).isNotEmpty();

        // обращение к несуществующей Task не вызывает NPE
        taskManager.getTaskById(42);
        // размер списка истории остаётся равен 1, значение null не добавляется в список с историей
        Assertions.assertThat(taskManager.getHistory()).hasSize(1);

        Epic epic_1 = new Epic("epic_1", "epic_1 desc");
        taskManager.createEpic(epic_1);

        taskManager.getEpicById(epic_1.getTaskId());
        Assertions.assertThat(taskManager.getHistory()).hasSize(2);

        taskManager.getEpicById(42);
        Assertions.assertThat(taskManager.getHistory()).hasSize(2);
    }
}
