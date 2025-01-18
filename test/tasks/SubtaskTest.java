package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    @Test
    void subtaskCannotBeItEpic() {
        Subtask subtask = new Subtask("Subtask name", "Subtask description", 1);
        subtask.setId(2);

        subtask.setEpicId(subtask.getId());

        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Subtask cannot be its own epic");
    }
}
