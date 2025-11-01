package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldAddTask() {
        Task task = new Task("Test task", "Test task description");
        taskManager.addTask(task);

        assertTrue(taskManager.getAllTasks().contains(task));
    }

    @Test
    void shouldAddEpic() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        assertTrue(taskManager.getAllEpics().contains(epic));
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic("Test epic", "Test epic description");
        Subtask subtask = new Subtask("Test subtask", "Test epic subtask", epic.getId());
        taskManager.addSubtask(subtask);

        assertTrue(taskManager.getAllEpics().contains(subtask));
    }

    @Test
    void shouldFindTaskById() {
        Task task = new Task("Test task", "Test task description");
        taskManager.addTask(task);

        assertNotNull(taskManager.getTaskByID(task.getId()));
        assertEquals(task, taskManager.getTaskByID(task.getId()));

    }

    @Test
    void shouldFindEpicById() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getEpicByID(epic.getId()), "Эпик не найден.");
        assertEquals(epic, taskManager.getEpicByID(epic.getId()), "Эпики не совпадают.");

    }

    @Test
    void shouldFindSubtaskById() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test subtask", "Test epic subtask", epic.getId());
        taskManager.addSubtask(subtask);


        Subtask sb = taskManager.getSubtaskByID(subtask.getId());

        assertNotNull(sb);
        assertEquals(subtask, sb);
        assertEquals(epic.getId(), sb.getEpicId());

    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("Test task", "Test task description");
        taskManager.addTask(task);

        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTaskByID(task.getId()), "Задача не удалена.");
        assertTrue(taskManager.getAllTasks().isEmpty(), "В списке не должно быть задач.");
    }

    @Test
    void shouldRemoveAllTasks() {
        Task task = new Task("Test task", "Test task description");
        Task task2 = new Task("Task 2", "Description 2");

        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "В списке не должно быть задач.");
    }

    @Test
    void shouldRemoveAllEpics() {
        Epic epic = new Epic("Test epic", "Test epic description");
        Epic epic2 = new Epic("Test epic2", "Test epic description2");

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Test subtask", "Test epic subtask", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty(), "В списке не должно быть задач.");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "В списке не должно быть подзадач.");


    }

    @Test
    void shouldReturnAllTasks() {
        Task task = new Task("Test task", "Test task description");
        Task task2 = new Task("Task 2", "Description 2");

        taskManager.addTask(task);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getAllTasks().size(), "В списке должно быть 2 задачи");
        assertNotNull(taskManager.getAllTasks(), "Задачи не возвращаются.");
    }

    @Test
    void subtaskShouldHaveItsEpic() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Test epic subtask", epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    void epicStatusShouldBeNew() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldSaveTaskWithDurationAndStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2025, 11, 1, 15, 0);
        Duration duration = Duration.ofMinutes(90);

        Task task = new Task("Test task", "Test task description", duration, startTime);
        taskManager.addTask(task);

        assertEquals(duration,  taskManager.getTaskByID(task.getId()).getDuration());
    }

    @Test
    void shouldDetectIntersection() {
        LocalDateTime time = LocalDateTime.of(2025, 11, 1, 15, 0);
        Task task = new Task("Test task", "Test task description", Duration.ofMinutes(60), time);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofHours(2), time.minusMinutes(45));

        taskManager.addTask(task);

        assertThrows(IllegalStateException.class, () -> taskManager.addTask(task2),
                "Пересечение времени задач должно приводить к исключению");
    }


}
