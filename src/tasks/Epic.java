package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
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

}
