package manager;

import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import java.util.ArrayList;
import java.util.List;


public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void updateTask(Task task);

    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();
}
