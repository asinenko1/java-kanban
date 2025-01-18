package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskHeirTest {

    @Test
    void taskHeirsAreEqualIfIdsEreEqual() {
        Epic epic1 = new Epic("Test Epic1", "Test epic description1");
        Epic epic2 = new Epic("Test Epic2", "Test epic description2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Наследники должны быть равны, если равен их ID");
    }


}
