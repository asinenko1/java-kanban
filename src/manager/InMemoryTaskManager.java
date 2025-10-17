package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private boolean isIdUnique(int id) {
        return !tasks.containsKey(id) || !epics.containsKey(id) || !subtasks.containsKey(id);
    }

    @Override
    public void addTask(Task task) {
        if (!isIdUnique(task.getId())) {
            System.out.println("Задание с таким Id уже существует. Нельзя добавить задание.");
            return;
        }
        if (task.getId() == 0) {
            while (!isIdUnique(idCounter)) {
                idCounter++;
            }
            task.setId(idCounter);
            idCounter++;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (!isIdUnique(epic.getId())) {
            System.out.println("Эпик с таким Id уже существует. Нельзя добавить эпик.");
            return;
        }
        if (epic.getId() == 0) {
            while (!isIdUnique(idCounter)) {
                idCounter++;
            }
            epic.setId(idCounter);
            idCounter++;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Такого эпика не существует. Невозможно добавить подзадачу");
            return;
        }
        if (subtask.getEpicId() == subtask.getId()) {
            System.out.println("Подзадачу нельзя сделать своим же эпиком!");
            return;
        }

        if (!isIdUnique(subtask.getId())) {
            System.out.println("Подзадача с таким Id уже существует. Нельзя добавить подзадачу.");
            return;
        }
        if (subtask.getId() == 0) {
            while (!isIdUnique(idCounter)) {
                idCounter++;
            }
            subtask.setId(idCounter);
            idCounter++;
        }

        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        epicStatus(epic);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            epicStatus(epic);
        }
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        } else {
            System.out.println("Эпик с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().remove(Integer.valueOf(id));
            epicStatus(epic);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    @Override
    public void updateTask(Task task) {

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else if (epics.containsKey(task.getId())) {
            Epic epic = epics.get(task.getId());
            if (epic == null) {
                return;
            }
            epic.setName(task.getName());
            epic.setDescription(task.getDescription());
        } else if (subtasks.containsKey(task.getId())) {
            Subtask subtask = subtasks.get(task.getId());
            subtasks.put(task.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epicStatus(epic);

        } else {
            System.out.println("Задача " + task + " не найдена.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                subtasksByEpic.add(subtask);
            }
            return subtasksByEpic;
        } else {
            System.out.println("Эпика " + epics.get(epicId) + " не найдено.");
            return new ArrayList<>();
        }
    }

    private void epicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}
