package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
//import java.util.Comparator;
//import java.util.Optional;

public class Epic extends Task {
    protected ArrayList<Subtask> epicSubtasks;
    protected LocalDateTime endTime;

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
    public Epic(Integer taskId, String taskTitle, String taskDesc, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        super(taskId, taskTitle, taskDesc, taskStatus, null, null);
        epicSubtasks = new ArrayList<>();
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
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


    // изменено имя метода на более подходящее
    public void calculateEpicTime(Epic epic) {
        epicSubtasks = getSubtasks(epic);

        if (epicSubtasks.isEmpty()) {
            return;
        }

        // применение стрима для получения даты начала эпика (с небольшой помощью от yandexGPT :))
        LocalDateTime earliestSubtaskStartTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime)
                .orElse(null);

        epic.setStartTime(earliestSubtaskStartTime);

        endTime = epicSubtasks.stream()
                .filter(subtask1 -> subtask1.getStartTime() != null)
                .max(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime)
                .orElse(null);

        // подсчёт длительности эпика на основе сабтасков, которые имеют стартовое время и продолжительность
        Duration epicDuration = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .reduce(Duration.ZERO, (duration, subtask) -> duration.plus(subtask.getDuration()), Duration::plus);

        epic.setDuration(epicDuration);

        // объединённый цикл для поиска времени начала и конца эпика
//        LocalDateTime earliestSubtaskStartTime = null;
//        LocalDateTime latestSubtaskEndTime = null;
//
//        for (Subtask subtask : epicSubtasks) {
//            if (subtask.getStartTime() != null) {
//                if (earliestSubtaskStartTime == null || subtask.getStartTime().isBefore(earliestSubtaskStartTime)) {
//                    earliestSubtaskStartTime = subtask.getStartTime();
//                }
//            }
//
//            if (subtask.getEndTime() != null) {
//                if (latestSubtaskEndTime == null || subtask.getEndTime().isAfter(latestSubtaskEndTime)) {
//                    latestSubtaskEndTime = subtask.getEndTime();
//                }
//            }
//        }
//
//        setStartTime(earliestSubtaskStartTime);
//        endTime = latestSubtaskEndTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + epicSubtasks +
                ", epicId=" + getTaskId() +
                ", epicTitle='" + getTaskTitle() + '\'' +
                ", epicDesc='" + getTaskDesc() + '\'' +
                ", epicStatus=" + getTaskStatus() +
//                ", startTime=" + startTime.format(formatter) +
//                ", duration=" + duration.toMinutes() +
//                " min}";
                "}";
    }

}
