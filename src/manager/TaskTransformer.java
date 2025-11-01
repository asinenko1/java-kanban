package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;


public final class TaskTransformer {
    private TaskTransformer() {}

     static String toString(Task task) {
        String epicId = "";
        String duration = "";
        String startTime = "";

        if (task.getType() == TaskType.SUBTASK) {
            Subtask sb = (Subtask) task;
            epicId = String.valueOf(sb.getEpicId());
        }

        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

         if (task.getDuration() != null) {
             startTime = task.getStartTime().toString();
         }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                duration,
                startTime,
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

        Duration duration = null;
        if(!data[5].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(data[5]));
        }

        LocalDateTime startTime = null;
        if(!data[6].isEmpty()) {
            startTime = LocalDateTime.parse(data[6]);
        }

        int epicId = 0;
        if (type == TaskType.SUBTASK) {
            epicId = Integer.parseInt(data[7]);
        }

        Task task;

        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                task = new Subtask(name, description, epicId);
                break;
            default:
                return null;
        }

        task.setId(id);
        task.setStatus(status);

        if (type != TaskType.EPIC) {
            task.setDuration(duration);
            task.setStartTime(startTime);
        }

        return task;
    }
}
