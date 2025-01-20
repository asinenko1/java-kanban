package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void utilClassAlwaysReturnsInitializedTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void utilClassAlwaysReturnsInitializedHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }
}
