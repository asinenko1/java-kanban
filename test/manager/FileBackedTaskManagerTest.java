package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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
    void shouldSaveSeveralTasks() throws IOException {
        Task task = new Task("Task name", "Task description");
        manager.addTask(task);

        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getId());
        manager.addTask(subtask);

        String data = Files.readString(file.toPath());
        String[] lines = data.trim().split("\\R");

        assertEquals("id,type,name,status,description,epic", lines[0], "Первая строка должна быть заголовком");
        assertEquals(4, lines.length, "Должно получится 4 строки");

        assertTrue(data.contains("Task name"), "В списке должно храниться название задачи");
        assertTrue(data.contains("Epic description"), "В списке должно храниться описание эпика");
        assertTrue(data.contains("SUBTASK"), "В списке должна храниться подзадача");

    }

    @Test
    void  shouldLoadSeveralTasks()  {
        Task task = new Task("Task name", "Task description");
        manager.addTask(task);

        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask name", "Subtask description", epic.getId());
        manager.addSubtask(subtask);

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, load.getAllTasks().size(),"В файле должна быть 1 задача" );
        assertEquals(1, load.getAllSubtasks().size(),"В файле должна быть 1 подзадача" );
        assertEquals(1, load.getAllEpics().size(),"В файле должна быть 1 эпик" );


        assertEquals(epic.getId(),load.getAllSubtasks().get(0).getEpicId(), "Подзадача должна ссылаться на эпик");
        assertEquals(epic.getStatus(),load.getAllEpics().get(0).getStatus(), "Статус эпика должно совпадать");
    }
}
