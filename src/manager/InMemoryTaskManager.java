package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected int idCounter = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private boolean isIdUnique(int id) {
        return !tasks.containsKey(id) && !epics.containsKey(id) && !subtasks.containsKey(id);
    }

    @Override
    public void addTask(Task task) {

        if (hasIntersection(task)) {
            throw new IllegalStateException("Нельзя добавить задание. Задача пересекается по врмеени");
        }

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
        addToPrioritized(task);
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
        addToPrioritized(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());

        epicStatus(epic);
        epicTime(epic);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
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
        epics.values().forEach(epic -> {
            epic.getSubtasksId().clear();
            epicStatus(epic);
            epicTime(epic);
        });
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
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
            epicTime(epic);
        } else {
            System.out.println("Подзадача с ID " + id + " не найдена. Невозможно удалить задачу. ");
        }

    }

    @Override
    public void updateTask(Task task) {
        Task previous = tasks.get(task.getId());
        if (previous == null) {
            System.out.println("Задача " + task + " не найдена.");
            return;
        }
        removeFromPrioritized(previous);

        if (hasIntersection(task)) {
            addToPrioritized(previous);
            throw new IllegalStateException("Нельзя обновить задание. Задача пересекается по времени");
        }
        tasks.put(task.getId(), task);
        addToPrioritized(task);

    }

    @Override
    public void updateEpic(Epic epic) {
        Epic previous = epics.get(epic.getId());
        if (previous == null) {
            System.out.println("Эпик " + epic + " не найден.");
            return;
        }

        previous.setName(epic.getName());
        previous.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask previous = subtasks.get(subtask.getId());
        if (previous == null) {
            System.out.println("Подзадача " + subtask + " не найдена.");
            return;
        }

        subtask.setEpicId(previous.getEpicId());
        removeFromPrioritized(previous);

        if (hasIntersection(subtask)) {
            addToPrioritized(previous);
            throw new IllegalStateException("Нельзя обновить задание. Задача пересекается по времени");
        }


        subtasks.put(subtask.getId(), subtask);
        addToPrioritized(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epicStatus(epic);
        epicTime(epic);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    void epicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        List<TaskStatus> statuses = epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .toList();

        boolean allDone = statuses.stream().allMatch(status -> status == TaskStatus.DONE);
        boolean allNew = statuses.stream().allMatch(status -> status == TaskStatus.NEW);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

    private void epicTime(Epic epic) {
        Duration total = Duration.ZERO;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Integer sId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(sId);

            if (subtask == null) continue;

            Duration d = subtask.getDuration();
            if (d != null) total = total.plus(d);

            LocalDateTime startTimeSubtask = subtask.getStartTime();
            LocalDateTime endTimeSubtask = subtask.getEndTime();

            if (startTimeSubtask != null) {
                if (earliest == null || startTimeSubtask.isBefore(earliest)) {
                    earliest = startTimeSubtask;
                }
            }

            if (endTimeSubtask != null) {
                if (latest == null || endTimeSubtask.isAfter(latest)) {
                    latest = endTimeSubtask;
                }
            }
        }

        epic.setDuration(total.isZero() ? null : total);
        epic.setStartTime(earliest);
        epic.setEndTime(latest);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime).thenComparing(Task::getId)
    );

    private void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    private boolean isIntersects(Task t1, Task t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getStartTime() == null || t2.getStartTime() == null) return false;

        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime() != null ? t1.getEndTime() : start1;
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime() != null ? t2.getEndTime() : start2;

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasIntersection(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) return false;

        return getPrioritizedTasks().stream()
                .filter(Objects::nonNull)
                .filter(task -> task.getStartTime() != null)
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> isIntersects(task, newTask));
    }


    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

}
