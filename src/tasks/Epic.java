package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> epicSubtasks;

    // для создания epic
    public Epic(String taskTitle, String taskDesc) {
        super(taskTitle, taskDesc);
        this.epicSubtasks = new ArrayList<>();
    }

    // для обновления epic
    public Epic(Epic epic, Integer taskId, String taskTitle, String taskDesc) {
        super(taskId, taskTitle, taskDesc);
        epicSubtasks = epic.getSubtasks(epic);
    }

    // для использования в fromString
    public Epic(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus) {
        super(taskId, taskTitle, taskDesc, taskStatus);
        epicSubtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        epicSubtasks.add(subtask);
    }

    public void calculateEpicStatus(Epic epic) {
        epicSubtasks = getSubtasks(epic);
        if (epicSubtasks != null && !epicSubtasks.isEmpty()) {
            int doneSubtasks = 0;
            int newSubtasks = 0;
            for (Subtask sub : epicSubtasks)
                if (sub.getTaskStatus().equals(TaskStatus.DONE)) {
                    doneSubtasks++;
                } else if (sub.getTaskStatus().equals(TaskStatus.NEW)) {
                    newSubtasks++;
                }
            if (doneSubtasks < epicSubtasks.size() && newSubtasks < epicSubtasks.size()) {
                setTaskStatus(TaskStatus.IN_PROGRESS);
            } else if (doneSubtasks == epicSubtasks.size()) {
                setTaskStatus(TaskStatus.DONE);
            } else if (newSubtasks == epicSubtasks.size()) {
                setTaskStatus(TaskStatus.NEW);
            }
        } else {
            setTaskStatus(TaskStatus.NEW);
        }
    }

    public ArrayList<Subtask> getSubtasks(Epic epic) {
        return epicSubtasks;
    }

    public void replaceSubtask(Subtask subtask, Subtask subtaskToRemove) {
        epicSubtasks.remove(subtaskToRemove);
        epicSubtasks.add(subtask);
    }

    public void deleteSubtask(Subtask subtaskToDelete) {
        epicSubtasks.remove(subtaskToDelete);
    }

    public void deleteAllSubtasks() {
        if (epicSubtasks != null) {
            epicSubtasks.clear();
        }
    }

    public Integer getEpicId() {
        return getTaskId();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + epicSubtasks +
                ", epicId=" + getTaskId() +
                ", epicTitle='" + getTaskTitle() + '\'' +
                ", epicDesc='" + getTaskDesc() + '\'' +
                ", epicStatus=" + getTaskStatus() +
                '}';
    }

}
