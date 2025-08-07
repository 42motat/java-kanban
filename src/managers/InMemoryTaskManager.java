
package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskTypes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    // изменён порядок номеров задач, чтобы не было коллизий в новой реализации хранения истории
    protected static int taskGeneratorId = 100001;
    protected static int epicGeneratorId = 200001;
    protected static int subtaskGeneratorId = 900001;

    private int getNextTaskId() {
        return taskGeneratorId++;
    }

    private int getNextEpicId() {
        return epicGeneratorId++;
    }

    private int getNextSubtaskId() {
        return subtaskGeneratorId++;
    }

    // для работы с приоритетным списком задач
    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    public boolean isTimeConflicted(TreeSet<Task> tasksWithPriority, Task checkedTask) {
        if (checkedTask.getStartTime() == null) {
            return false;
        }

        LocalDateTime checkedTaskStart = checkedTask.getStartTime();
        LocalDateTime checkedTaskEnd = checkedTask.getEndTime();

        return tasksWithPriority.stream()
                .filter(existingTask -> existingTask.getStartTime() != null & !existingTask.equals(checkedTask))
                .anyMatch(existingTask -> checkedTaskStart.isBefore(existingTask.getEndTime()) &&
                                                existingTask.getStartTime().isBefore(checkedTaskEnd));

    }

    public void addToPrioritizedList(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            if (isTimeConflicted(prioritizedTasks, task)) {
                throw new ManagerSaveException("Задача не будет сохранена, поскольку пересекается с имеющейся в списке.");
            } else {
                prioritizedTasks.add(task);
            }
        }
    }

    public void deleteFromPrioritizedList(Task task) {
        prioritizedTasks.remove(task);
    }

    /* МЕТОДЫ Task */
    @Override
    public void createTask(Task task) {
        task.setTaskId(getNextTaskId());
        addToPrioritizedList(task);
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
        addToPrioritizedList(task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        deleteFromPrioritizedList(tasks.get(taskId));
        tasks.remove(taskId);
        removeHistory(taskId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            removeHistory(task.getTaskId());
        }
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
        epic.calculateEpicTime(epic);
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        final Epic epic = epics.remove(epicId);
        removeHistory(epicId);
        for (Subtask subtask : epic.getSubtasks(epic)) {
            subtasks.remove(subtask.getTaskId());
            deleteSubtaskById(epic, subtask.getTaskId());
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtaskToGet : subtasks.values()) {
                if (subtaskToGet.getEpicId().equals(epic.getEpicId())) {
                    removeHistory(subtaskToGet.getTaskId());
                }
            }
            removeHistory(epic.getEpicId());
        }
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
        addToPrioritizedList(subtask);
        epic.addSubtask(subtask);
        epic.calculateEpicTime(epic);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask, Subtask subtaskToRemove, Epic epic) {
        epic.replaceSubtask(subtask, subtaskToRemove);
        subtasks.remove(subtaskToRemove.getTaskId());
        addToPrioritizedList(subtask);
        subtasks.put(subtask.getTaskId(), subtask);
        epic.calculateEpicTime(epic);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void deleteSubtaskById(Epic epic, Integer subtaskId) {
        Subtask subtaskToDelete = subtasks.get(subtaskId);
        deleteFromPrioritizedList(subtaskToDelete);
        subtasks.remove(subtaskId);
        removeHistory(subtaskId);
        epic.deleteSubtask(subtaskToDelete);
        epic.calculateEpicTime(epic);
        epic.calculateEpicStatus(epic);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            epic.calculateEpicStatus(epic);
        }
        subtasks.clear();
        Set<Task> subtasksToDelete = prioritizedTasks.stream()
                .filter(task -> task.getType() == TaskTypes.SUBTASK)
                .collect(Collectors.toSet());
        prioritizedTasks.removeAll(subtasksToDelete);

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
            deleteFromPrioritizedList(subtasks.get(subtaskToDelete.getTaskId()));
            subtasks.remove(subtaskToDelete.getTaskId());
        }
        subtasksToDelete.clear();
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

        return (ArrayList<Subtask>) subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId().equals(epicId))
                .collect(Collectors.toList());

    }

    // МЕТОДЫ, СВЯЗАННЫЕ С HISTORY
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void addHistory(Task task) {
        // метод добавляет таск в историю
        historyManager.add(task);
    }

    public void removeHistory(Integer id) {
        historyManager.remove(id);
    }

    /* МЕГА ЧИСТКА */
    @Override
    public void clearAllInstances() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
}