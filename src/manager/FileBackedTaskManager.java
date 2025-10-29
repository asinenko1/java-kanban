package manager;

import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    void save() {
        try {
            List<String> lines = new ArrayList<>();

            lines.add("id,type,name,status,description,epic");

            for (Task task : getAllTasks()) {
                lines.add(toString(task));
            }

            for (Task epic : getAllEpics()) {
                lines.add(toString(epic));
            }

            for (Task subtask : getAllSubtasks()) {
                lines.add(toString(subtask));
            }
            Files.write(file.toPath(), lines);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String data = Files.readString(file.toPath());

            String[] lines = data.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = fromString(line);

                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    private String toString(Task task) {

        TaskType type;
        String epic = "";

        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
            epic = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = TaskType.TASK;
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type.name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epic
        );
    }

    private static Task fromString(String value) {
        String[] data = value.split(",");

        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        TaskStatus status = TaskStatus.valueOf(data[3]);
        String description = data[4];
        int epicId = 0;
        if (type == TaskType.SUBTASK) {
            epicId = Integer.parseInt(data[5]);
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                return null;
        }
    }



    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }
}
