package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer taskId;
    private String taskTitle;
    private String taskDesc;
    private TaskStatus taskStatus;
    // поля для времени
    protected LocalDateTime startTime;
    protected Duration duration;

//    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");


    // для создания task без времени и продолжительности
    public Task(String taskTitle, String taskDesc, TaskStatus taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
    }

    // для обновления task без времени и продолжительности
    public Task(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
    }

    // для создания task и subtask
    public Task(String taskTitle, String taskDesc, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    // для обновления task
    public Task(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    // для создания epic
    public Task(String taskTitle, String taskDesc) {
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
    }

    // для обновления epic
    public Task(Integer taskId, String taskTitle, String taskDesc) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDateTime getEndTime() {
        if (duration != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(taskId);
        result = 31 * result + Objects.hashCode(taskTitle);
        result = 31 * result + Objects.hashCode(taskDesc);
        result = 31 * result + Objects.hashCode(taskStatus);
        return result;
    }

    @Override
    public String toString() {
        if (startTime != null) {
            return "Task{" +
                    "taskId=" + taskId +
                    ", taskTitle='" + taskTitle + '\'' +
                    ", taskDesc='" + taskDesc + '\'' +
                    ", taskStatus=" + taskStatus +
                    ", startTime=" + startTime /*.format(formatter)*/ +
                    ", duration=" + duration.toMinutes() +
                    "}";
        } else {
            return "Task{" +
                    "taskId=" + taskId +
                    ", taskTitle='" + taskTitle + '\'' +
                    ", taskDesc='" + taskDesc + '\'' +
                    ", taskStatus=" + taskStatus +
//                    ", startTime=" + startTime.format(formatter) +
//                    ", duration=" + duration.toMinutes() +
//                    " min}";
                    "}";
        }
    }
}
