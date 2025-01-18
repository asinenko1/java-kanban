package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {

    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task("Test name", "Test description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void taskAddedToHistoryManagerSavesPreviousVersionOfTask() {

        Task task1 = new Task("First task", "First task description");
        task1.setId(13);
        historyManager.add(task1);

        task1.setName("New task");
        task1.setDescription("New Description");

        final List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(),  "В истории должна быть одна задача");

        final Task task = history.getFirst();

        assertEquals("First task", task.getName(), "Имя задачи в истории не должно меняться");
        assertEquals("First task description", task.getDescription(), "Описание задачи в истории не должно меняться");
        assertEquals(13, task.getId(), "Id задачи в истории не должно меняться");



    }
}
