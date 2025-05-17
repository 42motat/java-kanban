package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    // для создания epic
    public Epic(String taskTitle, String taskDesc) {
        super(taskTitle, taskDesc);
        this.subtasks = new ArrayList<>();
    }

    // для обновления epic
    public Epic(Integer taskId, String taskTitle, String taskDesc) {
        super(taskId, taskTitle, taskDesc);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void calculateEpicStatus(Epic epic) {
        subtasks = getSubtasks(epic);
        if (subtasks != null) {
            int doneSubtasks = 0;
            int newSubtasks = 0;
            for (Subtask sub : subtasks)
                if (sub.getTaskStatus().equals(TaskStatus.DONE)) {
                    doneSubtasks++;
                } else if (sub.getTaskStatus().equals(TaskStatus.NEW)) {
                    newSubtasks++;
                }
            if (doneSubtasks < subtasks.size() && newSubtasks < subtasks.size()) {
                setTaskStatus(TaskStatus.IN_PROGRESS);
            } else if (doneSubtasks == subtasks.size()) {
                setTaskStatus(TaskStatus.DONE);
            } else if (newSubtasks == subtasks.size()) {
                setTaskStatus(TaskStatus.NEW);
            }
        } else {
            setTaskStatus(TaskStatus.NEW);
        }
    }

    public ArrayList<Subtask> getSubtasks(Epic epic) {
        return subtasks;

    }

    public void replaceSubtask(Subtask subtask, Subtask subtaskToRemove) {
        subtasks.remove(subtaskToRemove);
        subtasks.add(subtask);
    }

    public void deleteSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void deleteAllSubtasks() {
        if (subtasks != null) {
            subtasks.clear();
        }
    }

    public Integer getEpicId() {
        return getTaskId();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", epicId=" + getTaskId() +
                ", epicTitle='" + getTaskTitle() + '\'' +
                ", epicDesc='" + getTaskDesc() + '\'' +
                ", epicStatus=" + getTaskStatus() +
                '}';
    }

}
