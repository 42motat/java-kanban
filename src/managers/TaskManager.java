package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    /* МЕТОДЫ Task */
    void createTask(Task task);

    Task getTaskById(Integer taskId);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    void deleteAllTasks();

    ArrayList<Task> getAllTasks();

    /* МЕТОДЫ Epic */
    void createEpic(Epic epic);

    Epic getEpicById(Integer taskId);

    void updateEpic(Epic epic);

    void deleteEpicById(Integer epicId);

    void deleteAllEpics();

    ArrayList<Epic> getAllEpics();

    /* МЕТОДЫ Subtask */
    void createSubtask(Epic epic, Subtask subtask);

    void updateSubtask(Subtask subtask, Subtask subtaskToRemove, Epic epic);

    void deleteSubtaskById(Epic epic, Integer subtaskId);

    void deleteAllSubtasks();

    void deleteAllSubtasksOfEpic(Integer epicId);

    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtaskById(Integer taskId);

    ArrayList<Subtask> getSubtasksByEpicId(Integer epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    /* МЕГА ЧИСТКА */
    void clearAllInstances();
}
