package service;

import managers.FileBackedTasksManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

class FileBackedTasksManagerTest {

    @Test
    void verifySaveAndLoadFunctionsWithEmptyHistoryList() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        Epic epic1 = new Epic("Epic1", "Description of epic1");
        Epic epic2 = new Epic("Epic2", "Description of epic2");
        Task task1 = new Task("Task1", "Description of task1");
        Task task2 = new Task("Task2", "Description of task2");

        manager1.createEpic(epic1);
        manager1.createEpic(epic2);
        manager1.createTask(task1);
        manager1.createTask(task2);
        Subtask subTask1 = new Subtask("SubTask1", "Description of subTask1", epic1.getId());
        Subtask subTask2 = new Subtask("SubTask2", "Description of subTask2", epic2.getId());
        manager1.createSubtask(subTask1);
        manager1.createSubtask(subTask2);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertEquals(manager1.getAllTasks(), manager2.getAllTasks(), "Списки задач не совпадают!");
        Assertions.assertEquals(manager1.getAllEpics(), manager2.getAllEpics(), "Списки эпиков не совпадают!");
        Assertions.assertEquals(manager1.getAllSubTasks(), manager2.getAllSubTasks(), "Списки подзадач не совпадают!");

        Assertions.assertEquals(manager1.getTask(3).getName(), manager2.getTask(3).getName(), "Имена у тасок не совпадают");
        Assertions.assertEquals(manager1.getTask(3).getDescription(), manager2.getTask(3).getDescription(), "Описание у тасок не совпадают");
        Assertions.assertEquals(manager1.getTask(3).getTaskStatus(), manager2.getTask(3).getTaskStatus(), "Статусы у тасок не совпадают");

        Assertions.assertEquals(manager1.getSubtask(5).getEpicId(), manager2.getSubtask(5).getEpicId(), "Эпики у сабтасок не совпадают");
        Assertions.assertEquals(manager1.getEpic(1).getSubtasksId(), manager2.getEpic(1).getSubtasksId(), "Сабтастки у эпиков не совпадают");
        Assertions.assertEquals(manager1.getEpic(2).toString(), manager2.getEpic(2).toString(), "Информация у эпиков не совпадают");
    }

    @Test
    void verifySaveAndLoadWithEmptyTaskList() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        // Сохраняем пустой менеджер
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertTrue(manager2.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        Assertions.assertTrue(manager2.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        Assertions.assertTrue(manager2.getAllSubTasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void verifySaveAndLoadWithEpicWithoutSubtasks() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        Epic epic1 = new Epic("Epic1", "Description of epic1");
        manager1.createEpic(epic1);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertEquals(1, manager2.getAllEpics().size(), "Должен быть один эпик");
        Assertions.assertTrue(manager2.getAllSubTasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void verifySaveAndLoadWithHistory() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        Task task1 = new Task("Task1", "Description of task1");
        Task task2 = new Task("Task2", "Description of task2");
        manager1.createTask(task1);
        manager1.createTask(task2);

        // Добавляем задачи в историю
        manager1.getTask(task1.getId());
        manager1.getTask(task2.getId());

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        List<Task> history1 = manager1.getHistory();
        List<Task> history2 = manager2.getHistory();

        Assertions.assertEquals(history1.size(), history2.size(), "Истории должны совпадать по размеру");
        for (int i = 0; i < history1.size(); i++) {
            Assertions.assertEquals(history1.get(i).getId(), history2.get(i).getId(), "Истории должны совпадать по задачам");
        }
    }

    @Test
    void verifySaveAndLoadWithDeletedTask() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        Task task1 = new Task("Task1", "Description of task1");
        manager1.createTask(task1);
        manager1.deleteTask(task1.getId()); // Удаляем задачу

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertTrue(manager2.getAllTasks().isEmpty(), "Список задач должен быть пустым после удаления");
    }

    @Test
    void verifySaveAndLoadWithNonExistentTask() throws IOException {
        File file = File.createTempFile("file", ".csv");
        FileBackedTasksManager manager1 = new FileBackedTasksManager(file);

        Task task1 = new Task("Task1", "Description of task1");
        manager1.createTask(task1);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertNull(manager2.getTask(999), "Задача с несуществующим ID должна возвращать null");
    }
}
