package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void utilClassAlwaysReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);

        Task task = new Task("Test task", "Test task description");
        taskManager.addTask(task);

        System.out.println(taskManager.getAllTasks());


        assertNotNull(taskManager.getAllTasks(), "Список не отображается");
        assertEquals(1, taskManager.getAllTasks().size(), "Задача не добавлена");
        assertEquals(task, taskManager.getTaskByID(task.getId()), "Задачи не совпадают");
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
