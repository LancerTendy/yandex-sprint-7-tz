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
        if (tail != null) {
            tail.setNext(node);
            node.setPrev(tail);
        } else {
            head = node;
        }
        tail = node;
        linkedTasks.put(task.getId(), node);
    }

    private void removeNode(int id) {
        Node node = linkedTasks.get(id);
        if (node == null) return;

        if (node == tail && node == head) {
            tail = null;
            head = null;
        } else if (node == head) {
            head = head.getNext();
            if (head != null) {
                head.setPrev(null);
            }
        } else if (node == tail) {
            tail = tail.getPrev();
            if (tail != null) {
                tail.setNext(null);
            }
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }

        linkedTasks.remove(id);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            allTasks.add(node.getTask());
            node = node.getNext();
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
                removeNode(task.getId());
            }
            linkLast(task);
        }
    }
}
