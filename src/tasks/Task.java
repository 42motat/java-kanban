package tasks;

import java.util.Objects;

public class Task {
    private Integer taskId;
    private String taskTitle;
    private String taskDesc;
    private TaskStatus taskStatus;

    // для создания task и subtask
    public Task(String taskTitle, String taskDesc, TaskStatus taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
    }

    // для создания epic
    public Task(String taskTitle, String taskDesc) {
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
    }

    // для обновления task
    public Task(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
        this.taskStatus = taskStatus;
    }

    // для обновления epic
    public Task(Integer taskId, String taskTitle, String taskDesc) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
        /* Также советуем применить знания о методах equals() и hashCode(), чтобы реализовать идентификацию задачи по её id.
         * !! При этом две задачи с одинаковым id должны выглядеть для менеджера как одна и та же. !!
         * выше указана причина изменения в переопределении метода equals; взято из ТЗ к четвёртому спринту. */
//              && Objects.equals(taskTitle, task.taskTitle) &&
//               Objects.equals(taskDesc, task.taskDesc) &&
//               taskStatus == task.taskStatus;
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
        return "Task{" +
                "taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
