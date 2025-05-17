package tasks;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String taskTitle, String taskDesc, Integer epicId) {
        super(taskTitle, taskDesc);
        super.setTaskStatus(TaskStatus.NEW);
        this.epicId = epicId;
    }

    public Subtask(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus, Integer epicId) {
        super(taskId, taskTitle, taskDesc, taskStatus);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", subtaskId=" + getTaskId() +
                ", subtaskTitle='" + getTaskTitle() + '\'' +
                ", subtaskDesc='" + getTaskDesc() + '\'' +
                ", subtaskStatus=" + getTaskStatus() +
                '}';
    }

}
