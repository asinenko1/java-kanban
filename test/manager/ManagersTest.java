package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void utilClassAlwaysReturnsInitializedTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void utilClassAlwaysReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);

        Task task = new Task("Test task", "Test tassk description");
        historyManager.add(task);

        assertNotNull(historyManager.getHistory(), "История не отображается");
        assertEquals(1, historyManager.getHistory().size(), "Задача не добавлена в историю");
        assertEquals(task, historyManager.getHistory().get(task.getId()), "Задачи не совпадают");

    }
}
