package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String taskTitle, String taskDesc, Integer epicId) {
        super(taskTitle, taskDesc);
        super.setTaskStatus(TaskStatus.NEW);
        this.epicId = epicId;
    }

    public Subtask(String taskTitle, String taskDesc, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(taskTitle, taskDesc, TaskStatus.NEW, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus, Integer epicId,
                   LocalDateTime startTime, Duration duration) {
        super(taskId, taskTitle, taskDesc, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
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
                ", startTime=" + startTime.format(formatter) +
                ", duration=" + duration.toMinutes() +
                " min}";
    }

}
