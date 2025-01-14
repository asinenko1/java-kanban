public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Epic epic1 = new Epic("Закрыть 4 спринт", "Закончить наконец-то все дела по 4 спринту");
        manager.addEpic(epic1);

        Task task1 = new Task("Купить продукты", "Надо сходить в магазин...");
        manager.addTask(task1);

        Subtask subtask1 = new Subtask("Составить план действий", "Постараться не сойти с ума", epic1.getId());
        manager.addSubtask(subtask1);

        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи эпика: " + manager.getSubtasksByEpic(epic1.getId()));

        manager.updateTask(subtask1);

        System.out.println(epic1.getStatus());


    }
}
