package managers;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> linkedTasks = new HashMap<>();
    private Node tail = null;
    private Node head = null;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (tail != null && head != null) {
            tail.next = node;
            node.prev = tail;
        } else {
            head = node;
        }
        tail = node;
        linkedTasks.put(node.task.getId(), node);
    }

    private void removeNode(int id) {
        Node node = linkedTasks.get(id);
        if (node == null) return;
        if (node == tail && node == head) {
            tail = null;
            head = null;
        } else if (node == head) {
            head = head.next;
        } else if (node == tail) {
            tail = tail.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        linkedTasks.remove(id);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            allTasks.add(node.task);
            node = node.next;
        }
        return allTasks;
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (linkedTasks.containsKey(task.getId())) {
                linkedTasks.remove(task.getId());
                removeNode(task.getId());
            }
            linkLast(task);
        }
    }


    public static class Node {
        Node next;
        Node prev;
        Task task;

        public Node(Task task) {
            this.next = null;
            this.prev = null;
            this.task = task;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    '}';
        }
    }
}

