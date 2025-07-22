package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,startTime,duration,epic";
    }

    /*  пример сохранения в файл
    id,type,name,status,description,startTime,duration,epic
    0 | 1  | 2  |  3   |     4     |    5    |   6    |  7
    1,TASK,Task1,NEW,LDT-value,Description task1,
    2,EPIC,Epic2,DONE,LDT-value,Description epic2,
    3,SUBTASK,Sub Task2,DONE,LDT-value,Description sub task3,2
    */

    public static String toString(Task task) {
        String taskToString = task.getTaskId() + "," + task.getType() + "," + task.getTaskTitle() + "," +
                              task.getTaskStatus() + "," + task.getTaskDesc() + ",";

        if (task.getStartTime() == null) {
            taskToString = taskToString + " " + ",";
        } else {
            taskToString += task.getStartTime().toString() + ",";
        }

        if (task.getDuration() == null) {
            taskToString = taskToString + " " + ",";
        } else {
            taskToString += task.getDuration().toString() + ",";
        }

        if (task.getType().equals(TaskTypes.SUBTASK)) {
            taskToString = taskToString + ((Subtask) task).getEpicId();
//        }
        } else {
            taskToString = taskToString + " ";
        }
        return taskToString;
    }

//    id,type,name,status,description,startTime,duration,epic
//    0 | 1  | 2  |  3   |     4     |    5    |   6    |  7

    public static Task fromString(String line) throws ManagerSaveException {
        LocalDateTime startTime;
        Duration duration;
        String[] taskParams = line.split(",");
        // айди
        Integer id = Integer.parseInt(taskParams[0]);
        // taskType
        TaskTypes taskType = TaskTypes.valueOf(taskParams[1]);
        // название
        String name = taskParams[2];
        // статус
        TaskStatus status = TaskStatus.valueOf(taskParams[3]);
        // описание
        String desc = taskParams[4];
        // старт тайм
        if (taskParams[5].equals(" ")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(taskParams[5]);
        }
        // продолжительность
        if (taskParams[6].equals(" ")) {
            duration = null;
        } else {
            duration = Duration.parse(taskParams[6]);
        }

        // для сабтасков меняется алгорит и последний элемент массива должен быть распарсен в число и указан как эпик,
        // к которому сабтаск относится
        if (taskType.equals(TaskTypes.EPIC)) {
            InMemoryTaskManager.epicGeneratorId = id;
            return new Epic(id, name, desc, status, startTime, duration);
        } else if (taskType.equals(TaskTypes.SUBTASK)) {
            InMemoryTaskManager.subtaskGeneratorId = id++;
            // убрать символ перевода каретки, чтобы парсилось только число
            taskParams[7] = taskParams[7].replace("\r", "");
            Integer epicId = Integer.parseInt(taskParams[7]);
            return new Subtask(id, name, desc, status, epicId, startTime, duration);
        } else if (taskType.equals(TaskTypes.TASK)) {
            InMemoryTaskManager.taskGeneratorId = id++;
            return new Task(id, name, desc, status, startTime, duration);
        } else  {
            throw new ManagerSaveException("не удалось загрузить задачу");
        }
    }
}
