package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskManagerTest {
    @Test
    void addNewTaskAndFindTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", epic.getId());

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getTaskByID(task.getId()), "Задача не найдена.");
        assertEquals(task, taskManager.getTaskByID(task.getId()), "Задачи не совпадают.");

        assertNotNull(taskManager.getEpicByID(epic.getId()), "Эпик не найден.");
        assertEquals(epic, taskManager.getEpicByID(epic.getId()), "Эпики не совпадают.");

        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getSubtaskByID(subtask.getId()), "Подзадача не найдена.");
        assertEquals(subtask, taskManager.getSubtaskByID(subtask.getId()), "Подзадачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        final List<Task> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");

        final List<Task> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void tasksWithPutIdAndGeneratedIdDoNotConflict() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

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
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Test task name", "Test task description");
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);

        manager.addTask(task);
        final Task savedTask = manager.getTaskByID(task.getId());

        assertEquals(task.getName(), savedTask.getName(), "Изменилось имя у задачи");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Изменилось Описание у задачи");
        assertEquals(task.getId(), savedTask.getId(), "Изменилось Id у задачи");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Изменился статус у задачи");

    }

}
