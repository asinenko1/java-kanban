package tasks;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;

class EpicTest {
    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("Epic", "Epic description");
        epic.setId(1);

        epic.addSubtaskId(epic.getId());

        assertFalse(epic.getSubtasksId().contains(epic.getId()));

    }
}