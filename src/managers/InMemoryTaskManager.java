package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

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
    @Override
    public void createTask(Task task) {
        task.setTaskId(getNextTaskId());
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        addHistory(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /* МЕТОДЫ Epic */
    @Override
    public void createEpic(Epic epic) {
        epic.setTaskId(getNextEpicId());
        epics.put(epic.getTaskId(), epic);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        addHistory(epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
        epic.calculateEpicStatus(epic);
    }

    @Override
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

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /* МЕТОДЫ Subtask */
    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setTaskId(getNextSubtaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        epic.addSubtask(subtask);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask, Subtask subtaskToRemove, Epic epic) {
        epic.replaceSubtask(subtask, subtaskToRemove);
        subtasks.remove(subtaskToRemove.getTaskId());
        subtasks.put(subtask.getTaskId(), subtask);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void deleteSubtaskById(Epic epic, Integer subtaskId) {
        Subtask subtaskToDelete = subtasks.get(subtaskId);
        subtasks.remove(subtaskId);
        epic.deleteSubtask(subtaskToDelete);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            epic.calculateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksOfEpic(Integer epicId) {
        Epic epicToClean = epics.get(epicId);
        epicToClean.deleteAllSubtasks();
        epicToClean.calculateEpicStatus(epicToClean);

        // удалить из subtasks только сабы, которые имеют эпикАйди нужного эпика
        HashMap<Integer, Subtask> subtasksToDelete = new HashMap<>();
        for (Subtask subtaskToGet : subtasks.values()) {
            if (subtaskToGet.getEpicId().equals(epicToClean.getEpicId())) {
                subtasksToDelete.put(subtaskToGet.getTaskId(), subtaskToGet);
            }
        }
        for (Subtask subtaskToDelete : subtasksToDelete.values()) {
            subtasks.remove(subtaskToDelete.getTaskId());
        }
        subtasksToDelete.clear();
//        subtasks.clear();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        addHistory(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()){
            if (subtask.getEpicId().equals(epicId)){
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    // МЕТОДЫ, СВЯЗАННЫЕ С HISTORY
    @Override
    public List<Task> getHistory() {
        // получить список истории
        return historyManager.getHistory();
    }

    public void addHistory(Task task) {
        // метод добавляет таск в историю
        historyManager.add(task);
    }

    /* МЕГА ЧИСТКА */
    @Override
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
