package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {

    private Task task;
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test task", "Test task description");
    }


    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void taskAddedToHistoryManagerSavesPreviousVersionOfTask() {


        task.setId(13);
        historyManager.add(task);

        task.setName("New task");
        task.setDescription("New Description");

        final List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "В истории должна быть одна задача");

        final Task task = history.getFirst();

        assertEquals("Test task", task.getName(), "Имя задачи в истории не должно меняться");
        assertEquals("Test task description", task.getDescription(), "Описание задачи в истории не должно меняться");
        assertEquals(13, task.getId(), "Id задачи в истории не должно меняться");

    }

    @Test
    void historyShouldContain10Elements() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        final List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Количество элементов в истории не должно превышать 10.");
        assertEquals("Task 2", history.getFirst().getName(), "Первая задача удаляется при превышении лимита");
    }
}
