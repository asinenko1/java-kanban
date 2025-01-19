package tasks;

import org.junit.jupiter.api.Test;


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
}