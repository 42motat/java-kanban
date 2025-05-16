import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    /* методы, которые должны быть в этом классе для каждого из типа задач (задача, подзадача, эпик):
     * 1. получение списка всех задач  --  +++
     * 2. удаление всех задач  --  +++
     * 3. получение задачи по id  --  +++
     * 4. создание; сам объект должен передаваться в качестве параметра  --  +++
     * 5. обновление; новая версия объекта с верным идентификатором передаётся в виде параметра
     * 6. удаление по идентификатору  --  +++
     *
     * доп. методы:
     * 1. получение списка всех подзадач определённого эпика  --  +++
    */

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int taskGeneratorId = 1;
    private int epicGeneratorId = 1;
    private int subtaskGeneratorId = 1;

    private int getNextTaskId() {
        return taskGeneratorId++;
    }
    private int getNextEpicId() {
        return epicGeneratorId++;
    }
    private int getNextSubtaskId() {
        return subtaskGeneratorId++;
    }

    /* МЕТОДЫ Task */
    public void createTask(Task task) {
        task.setTaskId(getNextTaskId());
        tasks.put(task.getTaskId(), task);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    /* МЕТОДЫ Epic */
    public void createEpic(Epic epic) {
        epic.setTaskId(getNextEpicId());
        epics.put(epic.getTaskId(), epic);
    }

    public Epic getEpicById(int taskId) {
        return epics.get(taskId);
    }

    public void updateEpic(Epic epic) {
        // здесь нужно проверять статус?
        epics.put(epic.getTaskId(), epic);
    }

    public void deleteEpicById(int taskId) {
        epics.remove(taskId);
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(int taskId) {
        return epics.get(taskId).getSubtasks();
    }

    /* МЕТОДЫ Subtask */
    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setTaskId(getNextSubtaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        epic.addSubtask(subtask);
    }

    public Subtask getSubtaskById(int taskId) {
        return subtasks.get(taskId);
    }

    public void deleteSubtaskById(int taskId) {
        subtasks.remove(taskId);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    /* МЕГА ЧИСТКА */
    public void clearAllInstances() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
}
