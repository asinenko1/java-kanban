package manager;

import tasks.*;


public final class TaskTransformer {
    private TaskTransformer() {}

     static String toString(Task task) {
        String epicId = "";

        if (task.getType() == TaskType.SUBTASK) {
            Subtask sb = (Subtask) task;
            epicId = String.valueOf(sb.getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    static Task fromString(String value) {
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
}
