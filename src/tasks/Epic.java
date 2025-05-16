package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String taskTitle, String taskDesc, TaskStatus taskStatus) {
        super(taskTitle, taskDesc, TaskStatus.DONE);
        this.subtasks = new ArrayList<>();
    }

    public Epic(Integer taskId, String taskTitle, String taskDesc) {
        super(taskId, taskTitle, taskDesc);
        this.subtasks = getSubtasks();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        if (subtasks.isEmpty()) {
            taskStatus = TaskStatus.NEW;
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
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
