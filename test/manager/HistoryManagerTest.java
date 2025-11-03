package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        Task task2 = new Task("name2", "description 2");
        task2.setId(2);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size(), "Должно быть две задачи");
    }

    @Test
    void shouldReturnEmptyHistory() {
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History  should  be empty");
    }

    @Test
    void shouldRemoveTask() {
        historyManager.add(task);

        historyManager.remove(task.getId());

        final List<Task> history = historyManager.getHistory();

        assertEquals(0, history.size());

    }

    @Test
    void shouldDeleteSameTaskFromHistory() {
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должна быть только одна задача");
    }

    @Test
    void keepLastViewInHistory() {
        task.setId(1);

        Task task2 = new Task("name 2", "description 2");
        task2.setId(1);

        historyManager.add(task);
        historyManager.add(task2);

        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должна быть она задача");
        assertEquals("name 2", history.getFirst().getName(), "Должна отобразиться вторая задача");

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
    void shouldChangeOrderAfterView() {
        Task task2 = new Task("name 2", "description 2");
        Task task3 = new Task("name 3", "description 3");

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task2, task3, task), history);
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        task.setId(1);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size(), "В истории не должно остаться задач.");
    }

    @Test
    void shouldRemoveFirstNode() {
        Task task2 = new Task("name 2", "description 2");
        Task task3 = new Task("name 3", "description 3");

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);

        final List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task2, task3), history);
    }

    @Test
    void shouldRemoveLastNode() {
        Task task2 = new Task("name 2", "description 2");
        Task task3 = new Task("name 3", "description 3");

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);

        final List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task, task2), history);
    }

    @Test
    void shouldRemoveMiddleNode() {
        Task task2 = new Task("name 2", "description 2");
        Task task3 = new Task("name 3", "description 3");

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        final List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task, task3), history);
    }
}
