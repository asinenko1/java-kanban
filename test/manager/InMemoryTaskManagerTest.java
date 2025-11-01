package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private Task task;
    private Task task2;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void init() {
        task = new Task("Test task", "Test task description");
        task2 = new Task("Task 2", "Description 2");
        epic = new Epic("Test epic", "Test epic description");
        subtask = new Subtask("Test subtask", "Test subtask description", epic.getId());
        subtask2 = new Subtask("Test subtask2", "Test subtask2 description", epic.getId());
        taskManager = new InMemoryTaskManager();
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromTaskManager() {
        taskManager.addTask(task);
        taskManager.getTaskByID(task.getId());

        assertNotNull(taskManager.getHistory(), "История не может быть пустой");

        taskManager.removeTaskById(task.getId());
        assertEquals(0, taskManager.getHistory().size(), "В истории не должно быть задач");
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicWhenSubtaskRemoved() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(1, epic.getSubtasksId().size(), "В эпике должна быть одна подзадача!");

        taskManager.removeSubtaskById(subtask.getId());

        assertEquals(0, epic.getSubtasksId().size(), "В эпике не должно остаться ID");
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

    @Test
    void shouldReturnCorrectHistoryList() {
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task2.getId());
        taskManager.getEpicByID(epic.getId());

        assertNotNull(taskManager.getHistory(), "История не может быть пустой");
        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");
        assertEquals(task, taskManager.getHistory().getFirst(), "Первая задача не совпадает с исходной");
    }

    @Test
    void shouldReturnSubtasksByEpicId() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        final List<Subtask> subtasks = taskManager.getSubtasksByEpic(epic.getId());

        assertEquals(2, subtasks.size(), "В эпике должно быть 2 задачи");
        assertEquals(subtask2, subtasks.get(1), "Задачи должны совпадать");

    }

    @Test
    void shouldCollectEpicTimeFromSubtasks() {
        taskManager.addEpic(epic);

        subtask.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());

        subtask.setStartTime((LocalDateTime.of(2023, 2, 12, 10, 30)));
        subtask.setDuration(Duration.ofMinutes(90));
        taskManager.addSubtask(subtask);

        subtask2.setStartTime((LocalDateTime.of(2023, 2, 11, 10, 30)));
        subtask2.setDuration(Duration.ofMinutes(60));
        taskManager.addSubtask(subtask2);

        Epic epicNew = taskManager.getEpicByID(epic.getId());

        assertEquals(Duration.ofMinutes(150), epicNew.getDuration(), "Эпик должен длиться 150 минут");
        assertEquals(LocalDateTime.of(2023, 2, 11, 10, 30), epicNew.getStartTime(), "Start time");
        assertEquals(LocalDateTime.of(2023, 2, 12, 12, 0), epicNew.getEndTime(), "EndTime");
    }


}
