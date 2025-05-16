import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
//        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // TODO Написать тесты

        // 1.1 создание таски
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        Task task_2 = new Task("task_2", "task_2 desk", TaskStatus.NEW);
        taskManager.createTask(task_2);

        // 1.2 создание эпика
        Epic epic_with_subs = new Epic("epic_with_subs", "desc of epic with subs", TaskStatus.NEW);
        taskManager.createEpic(epic_with_subs);
        Epic epic_with_no_subs = new Epic("epic_with_no_subs", "desc of epic with no subs", TaskStatus.NEW);
        taskManager.createEpic(epic_with_no_subs);

        // 1.3 создание сабтаска
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1 desc", TaskStatus.NEW, epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_1);
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2 desc", TaskStatus.NEW, epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_2);

        // 2.1 обновление таски
        Task updatedTask = new Task(task_1.getTaskId(), "new name", task_1.getTaskDesc(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        // 2.2 обновление эпика
        Epic epicToUpdate = new Epic(epic_with_no_subs.getTaskId(), "new epic name", "new desc");
        taskManager.updateEpic(epicToUpdate);


        // 2.3 обновление сабтаски

        // 3.1 получение таски / всех тасков
        System.out.println("Tasks:");
        System.out.println(taskManager.getTaskById(task_1.getTaskId()));
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        // 3.2 получение эпика / всех эпиков
        System.out.println("Epics");
        System.out.println(taskManager.getEpicById(epic_with_no_subs.getTaskId()));
        System.out.println(taskManager.getEpicById(epic_with_subs.getTaskId()));
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        // 3.2.1 получение всех сабтасков эпика по id
        System.out.println("Epic subtasks");
        System.out.println(taskManager.getAllSubtasksOfEpic(epic_with_subs.getTaskId()));
        System.out.println();

        // 3.3 получение сабтаски / всех сабтасков
        System.out.println("Subtasks");
        System.out.println(taskManager.getSubtaskById(subtask_2.getTaskId()));
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        // 4.1 удаление таски
        taskManager.deleteTaskById(task_1.getTaskId());

        // 4.2 удаление эпика
        taskManager.deleteEpicById(epic_with_no_subs.getTaskId());

        // 4.3 удаление сабтаски
        taskManager.deleteSubtaskById(subtask_2.getTaskId());


    }

}
