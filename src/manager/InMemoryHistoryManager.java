package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        public Task task;
        public Node next;
        public Node prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }

    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        removeNode(history.get(task.getId()));
        history.remove(task.getId());

        Task copyOfTask = new Task(task.getName(), task.getDescription());
        copyOfTask.setId(task.getId());
        copyOfTask.setStatus(task.getStatus());

        Node node = new Node(copyOfTask);
        linkLast(node);

        history.put(copyOfTask.getId(), node);
        System.out.println("Task was added to history: " + copyOfTask.getName());
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
        System.out.println("Task with ID " + id + " was removed from  history");
    }


    private void linkLast(Node node) {
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

    }


}
