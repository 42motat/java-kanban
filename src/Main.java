import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
//        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // TODO Написать тесты

        // 1.1 создание Task
        Task task_1 = new Task("task_1", "task_1 desc", TaskStatus.NEW);
        taskManager.createTask(task_1);
        Task task_2 = new Task("task_2", "task_2 desk", TaskStatus.NEW);
        taskManager.createTask(task_2);

        // 1.2 создание Epic
        Epic epic_with_subs = new Epic("epic_with_subs", "desc of epic with subs");
        taskManager.createEpic(epic_with_subs);
        Epic epic_with_no_subs = new Epic("epic_with_no_subs", "desc of epic with no subs");
        taskManager.createEpic(epic_with_no_subs);

        // 1.3 создание Subtask
        Subtask subtask_1 = new Subtask("subtask_1", "subtask_1 desc", epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_1);
        Subtask subtask_2 = new Subtask("subtask_2", "subtask_2 desc", epic_with_subs.getEpicId());
        taskManager.createSubtask(epic_with_subs, subtask_2);

        // 2.1 обновление Task
        Task updatedTask = new Task(task_1.getTaskId(), "new task_1", task_1.getTaskDesc(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        // 2.2 обновление Epic
        Epic epicToUpdate = new Epic(epic_with_no_subs.getTaskId(), "new epic_with_no_subs", "new desc");
        taskManager.updateEpic(epicToUpdate);

        // 2.3 обновление Subtask
        Subtask newSubtask_1 = new Subtask(subtask_1.getTaskId(), "new subtask_1", "new subtask_1 desc", TaskStatus.DONE, epic_with_subs.getEpicId());
        taskManager.updateSubtask(newSubtask_1, subtask_1, epic_with_subs);
        /* Раскомментировать обновление newSubtask_2 для проверки смены статуса Epic на DONE;
        * на данный момент без обновления subtask_2 статус Epic'а - IN_PROGRESS */
//        Subtask newSubtask_2 = new Subtask(subtask_2.getTaskId(), "new subtask_2", "new subtask_2 desc", TaskStatus.DONE, epic_with_subs.getEpicId());
//        taskManager.updateSubtask(newSubtask_2, subtask_2, epic_with_subs);

        // 3.1 получение Task / всех Tasks
        System.out.println("**Tasks**");
        System.out.println(taskManager.getTaskById(task_1.getTaskId())); // после обновления
        System.out.println(taskManager.getTaskById(task_2.getTaskId()));
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        // 3.2 получение Epic / всех Epics
        System.out.println("**Epics**");
        System.out.println(taskManager.getEpicById(epic_with_no_subs.getTaskId()));
        System.out.println(taskManager.getEpicById(epic_with_subs.getTaskId()));
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        // 3.2.1 получение всех Subtasks по id Epic
        System.out.println("*Epic subtasks*");
        System.out.println(taskManager.getAllSubtasksOfEpic(epic_with_subs.getTaskId()));
        System.out.println();

        // 3.3 получение Subtask / всех Subtasks
        System.out.println("*Subtasks*");
        System.out.println(taskManager.getSubtaskById(subtask_1.getTaskId()));
        System.out.println(taskManager.getSubtaskById(subtask_2.getTaskId()));
        System.out.println(taskManager.getAllSubtasks());
        System.out.println();

        // 4.1 удаление Task
        taskManager.deleteTaskById(task_1.getTaskId());
        System.out.println(taskManager.getTaskById(task_1.getTaskId())); // проверка удаления task_1

        // 4.2 удаление Epic
        taskManager.deleteEpicById(epic_with_no_subs.getTaskId());
        System.out.println(taskManager.getEpicById(epic_with_no_subs.getTaskId())); // проверка удаления epic_with_no_subtasks

        // 4.3 удаление Subtask
        taskManager.deleteSubtaskById(subtask_2.getTaskId());
        System.out.println(taskManager.getSubtaskById(subtask_2.getTaskId())); // проверка удаления subtask_2

        /* Вывод пунктов 4.х  --  null, null, null */

    }

}
