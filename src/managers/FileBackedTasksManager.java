package managers;

import exceptions.ManagerSaveException;
import models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File filename;

    public FileBackedTasksManager() {
        this.filename = new File("file.csv");
    }

    public FileBackedTasksManager(File src) {
        this.filename = src;
    }

    protected void save() {
        // Создаем файл, если он не существует
        if (!filename.exists()) {
            try {
                filename.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла: " + filename.getPath(), e);
            }
        }

        // Собираем все задачи в один список
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(this.tasks.values());
        allTasks.addAll(this.epics.values());
        allTasks.addAll(this.subTasks.values());

        // Записываем задачи в файл
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task entry : allTasks) {
                if (entry.getType().equals(Type.SUBTASK)) {
                    writer.write(toStringSub((Subtask) entry));
                } else {
                    writer.write(toString(entry));
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи задач в файл: " + filename.getPath(), exception);
        }
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ", " + "\n";
    }

    private String toStringSub(Subtask task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + "\n";
    }

    private Task fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] values = value.split(",");
        Task task = null;

        if (values[1].equals(Type.TASK.toString())) {
            task = new Task(values[2], values[4]);
            task.setTaskStatus(Status.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        } else if (values[1].equals(Type.SUBTASK.toString())) {
            task = new Subtask(values[2], values[4], Integer.parseInt(values[5]));
            task.setTaskStatus(Status.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        } else if (values[1].equals(Type.EPIC.toString())) {
            task = new Epic(values[2], values[4]);
            task.setTaskStatus(Status.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        }

        return task;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try {
            String data = Files.readString(file.toPath());
            if (data == null || data.isEmpty()) {
                throw new IOException("Файл пуст или отсутствует");
            }

            String[] lines = data.split("\\n");
            if (lines.length <= 1) {
                throw new IOException("Файл не содержит данных");
            }

            for (int i = 1; i < lines.length; i++) {
                Task task = manager.fromString(lines[i]);
                if (task == null) {
                    continue;
                }

                if (task.getId() > manager.id) {
                    manager.id = task.getId();
                }

                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subTasks.put(task.getId(), (Subtask) task);
                        manager.epics.get(((Subtask) task).getEpicId()).addSubtaskId(task.getId());
                        break;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new ManagerSaveException("Ошибка чтения файла: " + file.getPath(), exception);
        }

        return manager;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        return super.getTask(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        return super.getSubtask(id);
    }

    @Override
    public Epic getEpic(Integer id) {
        return super.getEpic(id);
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(Integer id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public int createTask(Task t) {
        int id = super.createTask(t);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask s) {
        int id = super.createSubtask(s);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic e) {
        int id = super.createEpic(e);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
