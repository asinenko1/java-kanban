package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

    public void addTask(Task task) {
        task.setId(idCounter);
        tasks.put(idCounter, task);
        idCounter++;
    }

    public void addEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter, epic);
        idCounter++;
    }

    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Такого эпика не существует. Невзможно добавить подзадачу");
            return;
        }

        subtask.setId(idCounter);
        subtasks.put(idCounter, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());

        epicStatus(epic);
        idCounter++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic: epics.values()) {
            epic.getSubtasksId().clear();
            epicStatus(epic);
        }
    }

    public Task getTaskByID(int id) {
            return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else {
            System.out.println("Задача с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    public void removeEpicById(int id) {
        if (epics.containsKey(id)){
            Epic epic = epics.remove(id);
            for (int subtaskId: epic.getSubtasksId()){
                subtasks.remove(subtaskId);
            }
        } else {
            System.out.println("Эпик с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)){
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksId().remove(Integer.valueOf(id));
            epicStatus(epic);

        } else {
            System.out.println("Подзадача с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    public void updateTask (Task task) {

        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        } else if (epics.containsKey(task.getId())){
            Epic epic = epics.get(task.getId());
            if (epic == null) {
                return;
            }
            epic.setName(task.getName());
            epic.setDescription(task.getDescription());
        } else if (subtasks.containsKey(task.getId())){
            Subtask subtask = subtasks.get(task.getId());
            subtasks.put(task.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epicStatus(epic);

        } else {
            System.out.println("Задача " + task + " не найдена.");
        }
    }


    private void epicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId: epic.getSubtasksId()) {
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

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
            for (int subtaskId: epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                subtasksByEpic.add(subtask);
            }
            return subtasksByEpic;
        } else {
            System.out.println("Эпика " + epics.get(epicId) + " не найдено.");
            return new ArrayList<>();
        }
    }


}
