/* Александру Ф.
 * Добрый день, Александр!
 * Заранее благодарю за код-ревью.
 */

package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            if (!file.exists()) {
                throw new ManagerSaveException("Ошибка чтения файла");
            }
            String oneBigString = Files.readString(file.toPath());

            String[] splitLines = oneBigString.split("\n");

            for (int i = 1; i < splitLines.length; i++) {
                Task task = CSVFormatter.fromString(splitLines[i]);
                if (task instanceof Epic) {
//                    taskManager.epics.put(task.getTaskId(), (Epic) task);
                    taskManager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    taskManager.createSubtask((taskManager.getEpicById(((Subtask) task).getEpicId())), (Subtask) task);
                } else if (task instanceof Task) {
                    taskManager.createTask(task);
                }
            }

        } catch (IOException | ManagerSaveException e) {
            // обработка исключения
            System.out.println(e.getMessage());
        }

        return taskManager;
    }

    /*  пример сохранения в файл
    id,type,name,status,description,epic
    1,TASK,Task1,NEW,Description task1,
    2,EPIC,Epic2,DONE,Description epic2,
    3,SUBTASK,Sub Task2,DONE,Description sub task3,2
    */

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (!file.exists()) {
                throw new ManagerSaveException("Ошибка чтения файла");
            }

            // 1. заголовок
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            // 2. таски
            for (Task task : getAllTasks()) {
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }
            // 3. эпики
            for (Epic epic : getAllEpics()) {
                writer.write(CSVFormatter.toString(epic));
                writer.newLine();
            }
            // 2. сабтаски
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }

        } catch (IOException | ManagerSaveException e) {
            // информация об исключении
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        super.createSubtask(epic, subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Subtask subtaskToRemove, Epic epic) {
        super.updateSubtask(subtask, subtaskToRemove, epic);
        save();
    }

    @Override
    public void deleteSubtaskById(Epic epic, Integer subtaskId) {
        super.deleteSubtaskById(epic, subtaskId);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksOfEpic(Integer epicId) {
        super.deleteAllSubtasksOfEpic(epicId);
        save();
    }
}
