package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();
    private static final int HISTORY_MAX = 10;

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {

        if (history.size() >= HISTORY_MAX) {
            history.removeFirst();
        }

        Task copyOfTask = new Task(task.getName(), task.getDescription());
        copyOfTask.setId(task.getId());
        copyOfTask.setStatus(task.getStatus());
        history.add(copyOfTask);
        System.out.println("Task was added to history: " + copyOfTask);
    }
}
