package pl.edu.pb.wi.todo_app.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.ToDoDatabase;
import pl.edu.pb.wi.todo_app.database.dao.ToDoItemDao;
import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;

public class ToDoRepository {
    private final ToDoItemDao toDoItemDao;
    private final LiveData<List<ToDoItem>> toDoItems;

    public ToDoRepository(Application application) {
        ToDoDatabase database = ToDoDatabase.getDatabase(application);
        toDoItemDao = database.toDoItemDao();
        toDoItems = toDoItemDao.findAll();
    }

    public LiveData<List<ToDoItem>> findAllToDoItems() {
        return toDoItems;
    }

    public void insert(ToDoItem toDoItem) {
        ToDoDatabase.databaseWriterExecutor.execute(() -> {
            toDoItemDao.insert(toDoItem);
        });
    }

    public void update(ToDoItem toDoItem) {
        ToDoDatabase.databaseWriterExecutor.execute(() -> {
            toDoItemDao.update(toDoItem);
        });
    }

    public void delete(ToDoItem toDoItem) {
        ToDoDatabase.databaseWriterExecutor.execute(() -> {
            toDoItemDao.delete(toDoItem);
        });
    }

    public LiveData<ToDoItem> findById(Integer id) {
        return toDoItemDao.findById(id);
    }
}
