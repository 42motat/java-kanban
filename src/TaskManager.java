import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
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
        epic.calculateEpicStatus(epic);
    }

    public Epic getEpicById(Integer taskId) {
        return epics.get(taskId);
    }

    public void updateEpic(Epic epic) {
        epic.calculateEpicStatus(epic);
        epics.put(epic.getTaskId(), epic);
    }

    public void deleteEpicById(Integer taskId) {
        epics.remove(taskId);
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(int taskId) {
        return epics.get(taskId).getSubtasks(getEpicById(taskId));
    }

    /* МЕТОДЫ Subtask */
    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setTaskId(getNextSubtaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        epic.addSubtask(subtask);
        epic.calculateEpicStatus(epic);
    }

    public void updateSubtask(Subtask subtask, Subtask subtaskToRemove, Epic epic) {
        epic.replaceSubtask(subtask, subtaskToRemove);
        subtasks.remove(subtaskToRemove.getTaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        epic.calculateEpicStatus(epic);
    }

    public void deleteSubtaskById(Integer taskId) {
        subtasks.remove(taskId);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public Subtask getSubtaskById(Integer taskId) {
        return subtasks.get(taskId);
    }

    /* МЕГА ЧИСТКА */
    public void clearAllInstances() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
}
