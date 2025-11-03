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

            lines.add("id,type,name,status,description,duration, startTime, epic");

            for (Task task : getAllTasks()) {
                lines.add(TaskTransformer.toString(task));
            }

            for (Task epic : getAllEpics()) {
                lines.add(TaskTransformer.toString(epic));
            }

            for (Task subtask : getAllSubtasks()) {
                lines.add(TaskTransformer.toString(subtask));
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

            int maxId = 0;

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = TaskTransformer.fromString(line);
                if (task == null) continue;
                TaskType type = task.getType();

                int id = task.getId();
                if (id > maxId) maxId = id;

                switch (type) {
                    case TASK:
                        manager.tasks.put(id, task);
                        break;
                    case EPIC:
                        manager.epics.put(id, (Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(id, subtask);
                        break;
                }

            }

            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }

            for (Epic epic: manager.epics.values()) {
                manager.epicStatus(epic);
            }

           manager.idCounter = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
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
