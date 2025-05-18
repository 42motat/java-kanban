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

    public Task getTaskById(Integer taskId) {
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

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
        epics.put(epic.getTaskId(), epic);
        epic.calculateEpicStatus(epic);
    }

    public void deleteEpicById(Integer epicId) {
        final Epic epic = epics.remove(epicId);
        for (Subtask subtask : epic.getSubtasks(epic)) {
            subtasks.remove(subtask.getTaskId());
            deleteSubtaskById(epic, subtask.getTaskId());
        }
        /* попытался сделать метод более простым и компактным, но без второго for
        * выходит ошибка ConcurrentModificationException */
//        ArrayList<Subtask> subsToDelete = new ArrayList<>();
//        for (Subtask subtask : subtasks.values()) {
//            int epicIdToCheck = subtask.getEpicId();
//            if (epicIdToCheck == (epicId)) {
//                subsToDelete.add(subtask);
//            }
//        }
//        for (Subtask subtask : subsToDelete) {
//            subtasks.remove(subtask.getTaskId());
//        }
//        epics.remove(epicId);
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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

    public void deleteSubtaskById(Epic epic, Integer subtaskId) {
        subtasks.remove(subtaskId);
        epic.deleteSubtask(subtaskId);
        epic.calculateEpicStatus(epic);
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            epic.calculateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(Integer taskId) {
        return subtasks.get(taskId);
    }

    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()){
            if (subtask.getEpicId().equals(epicId)){
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    /* МЕГА ЧИСТКА */
    public void clearAllInstances() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
}

/* Александру Ф.
 * Добрый день, Александр!
 * Спасибо за оперативную и конструктивную обратную связь!
 * Постарался исправить все замечания, которые возникли.
 *
 * */
