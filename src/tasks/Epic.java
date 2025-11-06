package tasks;

import java.time.LocalDateTime;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
        this.setStatus(TaskStatus.NEW);
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId == this.getId()) {
            System.out.println("Епик нельзя добавить в себя же в виде подзадачи!");
        } else {
            subtasksId.add(subtaskId);
        }
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
