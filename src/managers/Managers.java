package managers;

public class Managers {

     //Возвращает реализацию HistoryManager по умолчанию.
     //@return экземпляр InMemoryHistoryManager
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

     //Возвращает реализацию TaskManager по умолчанию (в памяти).
     //@return экземпляр InMemoryTaskManager
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

     //Возвращает реализацию TaskManager с сохранением в файл.
     //@param file файл, в который будут сохраняться данные
     //@return экземпляр FileBackedTasksManager
    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTasksManager(file);
    }

    //Возвращает реализацию TaskManager с сохранением в файл по умолчанию.
    //@return экземпляр FileBackedTasksManager с файлом по умолчанию
    public static TaskManager getDefaultFileBackedTaskManager() {
        return new FileBackedTasksManager();
    }
}
