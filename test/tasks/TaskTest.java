package tasks;


import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("Test task1", "Test Description2");
        Task task2 = new Task("Test task1", "Test Description2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи не равны, хотя их ID совпадает");
    }

    @Test
    void  shouldReturnEndTime() {
        Task task1 = new Task("Test task1", "Test Description2");
        task1.setStartTime((LocalDateTime.of(2023, 2, 12,10, 30)));
        task1.setDuration(Duration.ofMinutes(70));

        LocalDateTime expectedTime = LocalDateTime.of(2023, 2,  12, 11, 40);
        assertEquals(expectedTime, task1.getEndTime(), "Время окончания должно совпадать");
    }
}
