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
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(this.tasks.values());
        allTasks.addAll(this.epics.values());
        allTasks.addAll(this.subTasks.values());
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
            throw new ManagerSaveException("Ошибка записи задач в файл");
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
        Task task = null;
        String[] values = value.split(",");
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
        //Убрала throws IOException
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String data = Files.readString(file.toPath());
            if (data == null) {
                throw new IOException("Данных  нет");
            }
            String[] lines = data.split("\\n");
            if (lines.length <= 1) {
                throw new IOException("Данных  нет");
            }
            for (int i = 1; i < lines.length; i++) {
                Task task = manager.fromString(lines[i]);
                if (task.getId() > manager.id) manager.id = task.getId();
                if (task.getType().equals(Type.TASK)) {
                    manager.tasks.put(task.getId(), task);
                } else if (task.getType().equals(Type.EPIC)) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType().equals(Type.SUBTASK)) {
                    manager.subTasks.put(task.getId(), (Subtask) task);
                    manager.epics.get(((Subtask) task).getEpicId()).addSubtaskId(task.getId());
                }

            }
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new ManagerSaveException("Ошибка чтения файла! Проверьте его наличие по указанному пути!");
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
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask t = super.getSubtask(id);
        save();
        return t;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic t = super.getEpic(id);
        save();
        return t;
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