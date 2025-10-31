package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("test", "csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        Files.write(file.toPath(),  List.of("id,type,name,status,description,epic"));

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);

        assertTrue(load.getAllTasks().isEmpty(),"В менеджере не должно быть задач");
        assertTrue(load.getAllEpics().isEmpty(),"В менеджере не должно быть эпиков");
        assertTrue(load.getAllSubtasks().isEmpty(),"В менеджере не должно быть подзадач");
    }

    @Test
    void shouldSaveAndLoadTasks() {

        Task task = new Task("Task name", "Task description");
        manager.addTask(task);
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getId());
        manager.addSubtask(subtask);

        int subtaskIdBefore = subtask.getEpicId();

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getAllTasks().size(), manager2.getAllTasks().size(),"Количество задач должно совпадать" );
        assertEquals( manager.getAllSubtasks().size(), manager2.getAllSubtasks().size(),"В файле должна быть 1 подзадача" );
        assertEquals(manager.getAllEpics().size(), manager2.getAllEpics().size(),"В файле должна быть 1 эпик" );

        assertEquals(manager.getAllTasks(), manager2.getAllTasks(), "Данные должны совпадать");
        assertEquals(manager.getAllEpics(), manager2.getAllEpics(), "Данные должны совпадать");
        assertEquals(manager.getAllSubtasks(), manager2.getAllSubtasks(), "Данные должны совпадать");

        assertEquals(subtaskIdBefore,manager2.getAllSubtasks().get(0).getEpicId(), "Подзадача должна ссылаться на эпик");

    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("Task name", "Task description");
        manager.addTask(task);
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getId());
        manager.addSubtask(subtask);


        manager.removeTaskById(task.getId());

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, manager2.getAllTasks().size(), "Задачи быть не должно");
    }

    @Test
    void shouldUpdateTaskData() {
        Task task = new Task("Task name", "Task description");
        manager.addTask(task);

        Task updatedTask = new Task("Updated name", "Updated description");
        updatedTask.setId(task.getId());
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateTask(updatedTask);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        Task task2 = manager2.getTaskByID(task.getId());

        assertEquals(manager.getAllTasks().size(), manager2.getAllTasks().size(),"Количество задач должно совпадать" );
        assertEquals("Updated name", task2.getName(), "Name should be updated");
        assertEquals(updatedTask.getDescription(), task2.getDescription(),"Description should be updated");
        assertEquals(TaskStatus.IN_PROGRESS,updatedTask.getStatus(),"Status should be updated");

    }

}
