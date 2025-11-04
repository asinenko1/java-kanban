package manager;

import manager.exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    private File file;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("test", "csv");
        taskManager = createTaskManager();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        Files.write(file.toPath(),  List.of("id,type,name,status,description, duration, startTime, epic"));

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);

        assertTrue(load.getAllTasks().isEmpty(),"В менеджере не должно быть задач");
        assertTrue(load.getAllEpics().isEmpty(),"В менеджере не должно быть эпиков");
        assertTrue(load.getAllSubtasks().isEmpty(),"В менеджере не должно быть подзадач");
    }

    @Test
    public void shouldThrowExceptionWhenNoFile() {
        File noFile = new File("there_is_no_file.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(noFile),
                "Отсутствие файла должно приводить к исключению");
    }


}
