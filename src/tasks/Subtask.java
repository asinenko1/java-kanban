package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, TaskStatus status, Duration dur, LocalDateTime ldt) {
        super(name, description, status, dur, ldt);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            System.out.println("Подзадачу нельзя сделать своим же эпиком!");
        } else {
            this.epicId = epicId;
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}
