package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private Task task;
    private Epic epic;
    private Subtask subtask;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void init() {
        task = new Task("Test task", "Test task description");
        epic = new Epic("Test epic", "Test epic description");
        subtask = new Subtask("Test subtask", "Test subtask description", epic.getId());
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddNewTask() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getTaskByID(task.getId()), "Задача не найдена.");
        assertEquals(task, taskManager.getTaskByID(task.getId()), "Задачи не совпадают.");
    }

    @Test
    void shouldFindTaskById() {
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getEpicByID(epic.getId()), "Эпик не найден.");
        assertEquals(epic, taskManager.getEpicByID(epic.getId()), "Эпики не совпадают.");

        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getSubtaskByID(subtask.getId()), "Подзадача не найдена.");
        assertEquals(subtask, taskManager.getSubtaskByID(subtask.getId()), "Подзадачи не совпадают.");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskByID(task.getId()), "Задача не удалена.");
        assertTrue(taskManager.getAllTasks().isEmpty(), "В списке не должно быть задач.");
    }

    @Test
    void shouldRemoveAllTasks() {
        taskManager.addTask(task);
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.addTask(task2);
        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "В списке не должно быть задач.");
    }

    @Test
    void shouldReturnAllTasks() {
        taskManager.addTask(task);
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getAllTasks().size(), "В списке должно быть 2 задачи");
        assertNotNull(taskManager.getAllTasks(), "Задачи не возвращаются.");
    }


    @Test
    void tasksWithPutIdAndGeneratedIdDoNotConflict() {
        Task generatedId = new Task("Task with generated id", "Task Description");
        taskManager.addTask(generatedId);

        Task putId = new Task("Task with putted Id", "Task Description");
        putId.setId(generatedId.getId());

        taskManager.addTask(putId);

        assertEquals(1, taskManager.getAllTasks().size(), "В менеджере должа быть только одна задача.");
        assertEquals(generatedId, taskManager.getTaskByID(generatedId.getId()), "Должна быть только сгенерированная задача");


    }

    @Test
    void correctFieldsOfTaskAfterAddToTaskManager() {
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskByID(task.getId());

        assertEquals(task.getName(), savedTask.getName(), "Изменилось имя у задачи");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Изменилось Описание у задачи");
        assertEquals(task.getId(), savedTask.getId(), "Изменилось Id у задачи");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Изменился статус у задачи");

    }

}
