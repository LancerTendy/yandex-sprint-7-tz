package tasks;

// tasks.Task.java
public class Task {
    private int id;
    private String name;
    private Integer epicId; // Для подзадач, если есть

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
        this.epicId = null; // По умолчанию подзадача не привязана к эпикам
    }

    public Task(int id, String name, int epicId) {
        this.id = id;
        this.name = name;
        this.epicId = epicId; // Подзадача привязана к эпикам
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getEpicId() {
        return epicId;
    }
}
