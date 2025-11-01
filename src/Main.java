import manager.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import manager.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Epic epic1 = new Epic("Закрыть 4 спринт", "Закончить наконец-то все дела по 4 спринту");
        manager.addEpic(epic1);

        Task task1 = new Task("Купить продукты", "Надо сходить в магазин...");
        Task task2 = new Task("закрыть 5 спринт", "нужно сдать финальное задание");
        manager.addTask(task1);
        manager.addTask(task2);

        Subtask subtask1 = new Subtask("Составить план действий", "Постараться не сойти с ума", epic1.getId());
        Subtask subtask2 = new Subtask("Выполнить все уроки", "Отправить дз на проверку", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи эпика: " + manager.getSubtasksByEpic(epic1.getId()));

        manager.updateSubtask(subtask1);

        System.out.println(epic1.getStatus());

        manager.getTaskByID(2);
        manager.getTaskByID(3);
        manager.getEpicByID(1);
        manager.getSubtaskByID(4);

        printAllTasks(manager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
