package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
            history.remove(task.getId());
        }

        Task copyOfTask = new Task(task.getName(), task.getDescription());
        copyOfTask.setId(task.getId());
        copyOfTask.setStatus(task.getStatus());

        Node<Task> node = new Node<>(copyOfTask);
        linkLast(node);

        history.put(copyOfTask.getId(), node);
        System.out.println("Task was added to history: " + copyOfTask.getName());
    }

    @Override
    public void remove(int id){
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
            System.out.println("Task with ID " + id + " was removed from  history");
        } else {
            System.out.println("Task with ID " + id + " wasn't found in history");
        }

    }


    public void linkLast(Node<Task> node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks= new ArrayList<>();

        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {

        if (head == node && tail == node) {
            head = null;
            tail = null;
            return;
        }
        if (node == head) {
            head = head.next;
            head.prev = null;
            return;
        }
        if (node == tail) {
            tail = tail.prev;
            tail.next = null;
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }




}
