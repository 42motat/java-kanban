package managers;

import exceptions.ManagerSaveException;
import tasks.*;

public class CSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    /*  пример сохранения в файл
    id,type,name,status,description,epic
    1,TASK,Task1,NEW,Description task1,
    2,EPIC,Epic2,DONE,Description epic2,
    3,SUBTASK,Sub Task2,DONE,Description sub task3,2
    */

    public static String toString(Task task) {
        String taskToString = task.getTaskId() + "," + task.getType() + "," + task.getTaskTitle() + "," +
                              task.getTaskStatus() + "," + task.getTaskDesc() + ",";
        if (task.getType().equals(TaskTypes.SUBTASK)) {
            taskToString = taskToString + ((Subtask) task).getEpicId();
//        }
        } else {
            taskToString = taskToString + "";
        }
        return taskToString;
    }

    // при определении в Task метода getType() метод checkTaskType() оказался не нужен,
    // поскольку проще сразу вызывать getType()

    public static Task fromString(String line) throws ManagerSaveException {
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

        // для сабтасков меняется алгорит и последний элемент массива должен быть распарсен в число и указан как эпик,
        // к которому сабтаск относится
        if (taskType.equals(TaskTypes.EPIC)) {
            InMemoryTaskManager.epicGeneratorId = id;
            return new Epic(id, name, desc, status);
        } else if (taskType.equals(TaskTypes.SUBTASK)) {
            InMemoryTaskManager.subtaskGeneratorId = id++;
            // убрать символ перевода каретки, чтобы парсилось только число
            taskParams[5] = taskParams[5].replace("\r", "");
            Integer epicId = Integer.parseInt(taskParams[5]);
            return new Subtask(id, name, desc, status, epicId);
        } else if (taskType.equals(TaskTypes.TASK)) {
            InMemoryTaskManager.taskGeneratorId = id++;
            return new Task(id, name, desc, status);
        } else  {
            throw new ManagerSaveException("не удалось загрузить задачу");
        }
    }
}
