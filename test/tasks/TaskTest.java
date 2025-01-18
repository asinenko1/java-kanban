package tasks;


import org.junit.jupiter.api.Test;

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
}
