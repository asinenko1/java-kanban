import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private static int idCounter = 1;

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
        epic.addSubtask(subtask);

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
            epic.getSubtasks().clear();
            epicStatus(epic);
        }
    }

    public Task getTaskByID(int id) {
        if  (tasks.containsKey(id)){
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            System.out.println("Задача " + id + " не найдена");
            return null;
        }
    }

    public void removeTaskById(int id) {

        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }

        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Subtask subtask: epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }

            epic.getSubtasks().clear();
            return;
        }

        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasks().remove(subtask);
            epicStatus(epic);
        }

    }

    public void updateTask (Task task) {

        if (tasks.containsKey(task.getId())){
            updateStatus(task);
            tasks.put(task.getId(), task);
        } else if (epics.containsKey(task.getId())){
            System.out.println("Невозможно изменить задачу в ручную. Она меняется автоматически с изменением подзадач.");
        } else if (subtasks.containsKey(task.getId())){
            Subtask subtask = subtasks.get(task.getId());
            updateStatus(subtask);
            subtasks.put(task.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epicStatus(epic);

        } else {
            System.out.println("Задача " + task + " не найдена.");
        }
    }

    private void updateStatus(Task task) {
        if (task.getStatus() == TaskStatus.NEW) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.setStatus(TaskStatus.DONE);
        } else {
            System.out.println("Задача с ID " + task.getId() + " уже завершена.");
        }
    }

    public void epicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask: epic.getSubtasks()) {
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
            return epic.getSubtasks();
        } else {
            System.out.println("Эпика " + epics.get(epicId) + " не найдено.");
            return null;
        }
    }


}
