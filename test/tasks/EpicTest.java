package tasks;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EpicTest {
    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Epic", "Epic description");
        epic.setId(1);

        epic.addSubtaskId(epic.getId());

        assertFalse(epic.getSubtasksId().contains(epic.getId()));

    }

    @Test
    void taskHeirsAreEqualIfIdsEreEqual() {
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        Epic epic2 = new Epic("Test Epic2", "Test epic description2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Наследники должны быть равны, если равен их ID");
    }

    @Test
    void shouldStoreEndTime() {
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        LocalDateTime endTime = LocalDateTime.of(2025, 11,1, 12, 30);
        epic1.setEndTime(endTime);

        assertEquals(endTime, epic1.getEndTime());
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksNew() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        manager.addEpic(epic1);

        Subtask st1 = new Subtask("s1", "d1", epic1.getId());
        st1.setStatus(TaskStatus.NEW);
        manager.addSubtask(st1);

        Subtask st2 = new Subtask("s2", "d2", epic1.getId());
        st2.setStatus(TaskStatus.NEW);
        manager.addSubtask(st2);

        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Cтатус эпика должен быть NEW");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksDone() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        manager.addEpic(epic1);

        Subtask st1 = new Subtask("s1", "d1", epic1.getId());
        st1.setStatus(TaskStatus.DONE);
        manager.addSubtask(st1);

        Subtask st2 = new Subtask("s2", "d2", epic1.getId());
        st2.setStatus(TaskStatus.DONE);
        manager.addSubtask(st2);

        assertEquals(TaskStatus.DONE, epic1.getStatus(), "Cтатус эпика должен быть DONE");
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksNewDone() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        manager.addEpic(epic1);

        Subtask st1 = new Subtask("s1", "d1", epic1.getId());
        st1.setStatus(TaskStatus.NEW);
        manager.addSubtask(st1);

        Subtask st2 = new Subtask("s2", "d2", epic1.getId());
        st2.setStatus(TaskStatus.DONE);
        manager.addSubtask(st2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksInProgress() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        manager.addEpic(epic1);

        Subtask st1 = new Subtask("s1", "d1", epic1.getId());
        st1.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubtask(st1);

        Subtask st2 = new Subtask("s2", "d2", epic1.getId());
        st2.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubtask(st2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
    }


}