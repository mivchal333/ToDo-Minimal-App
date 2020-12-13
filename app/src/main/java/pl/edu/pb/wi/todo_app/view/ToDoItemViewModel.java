package pl.edu.pb.wi.todo_app.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.database.repository.ToDoRepository;


public class ToDoItemViewModel extends AndroidViewModel {
    private final ToDoRepository toDoRepository;
    private final LiveData<List<ToDoItem>> books;

    public ToDoItemViewModel(@NonNull Application application) {
        super(application);
        toDoRepository = new ToDoRepository(application);
        books = toDoRepository.findAllToDoItems();
    }

    public LiveData<List<ToDoItem>> findAll() {
        return books;
    }

    public LiveData<ToDoItem> findById(Integer id) {
        return toDoRepository.findById(id);
    }

    public void insert(ToDoItem toDoItem) {
        toDoRepository.insert(toDoItem);
    }

    public void update(ToDoItem toDoItem) {
        toDoRepository.update(toDoItem);
    }

    public void delete(ToDoItem toDoItem) {
        toDoRepository.delete(toDoItem);
    }
}

