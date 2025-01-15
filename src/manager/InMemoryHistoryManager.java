package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final Set<Integer> taskIds = new HashSet<>();

    @Override
    public void add(Task task) {
        if (taskIds.add(task.getId())) {
            history.add(task);
        }
    }

    @Override
    public void remove(int id) {
        taskIds.remove(id);
        history.removeIf(task -> task.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
