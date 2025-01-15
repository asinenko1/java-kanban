package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic (String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }


}
