package Main;

import manager.InMemoryHistoryManager;
import tasks.Epic;
import tasks.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        // Создание задач
        Task task1 = new Task(1, "Задача 1");
        Task task2 = new Task(2, "Задача 2");

        // Создание эпиков
        Epic epic1 = new Epic(3, "Эпик 1");
        Task subtask1 = new Task(4, "Подзадача 1", epic1.getId());
        Task subtask2 = new Task(5, "Подзадача 2", epic1.getId());
        Task subtask3 = new Task(6, "Подзадача 3", epic1.getId());
        Epic epic2 = new Epic(7, "Эпик 2"); // Эпик без подзадач

        // Добавление задач в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);
        historyManager.add(epic2);

        // Запрос задач в разном порядке и вывод истории
        printHistory(historyManager); // Все задачи

        // Запрос задачи 1
        historyManager.add(task1);
        printHistory(historyManager); // Проверка на отсутствие повторов

        // Запрос подзадачи 1
        historyManager.add(subtask1);
        printHistory(historyManager); // Проверка на отсутствие повторов

        // Удаление задачи 1 и проверка истории
        historyManager.remove(task1.getId());
        printHistory(historyManager); // Задача 1 не должна отображаться

        // Удаление эпика с подзадачами
        historyManager.remove(epic1.getId());
        printHistory(historyManager); // Эпик и его подзадачи не должны отображаться
    }

    private static void printHistory(InMemoryHistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        System.out.println("История просмотров:");
        for (Task task : history) {
            System.out.println(task.getId() + ": " + task.getName());
        }
        System.out.println();
    }
}
