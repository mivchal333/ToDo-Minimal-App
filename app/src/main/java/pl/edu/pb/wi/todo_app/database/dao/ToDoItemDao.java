package pl.edu.pb.wi.todo_app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;

@Dao
public interface ToDoItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ToDoItem todo_item);

    @Update
    public void update(ToDoItem todo_item);

    @Delete
    public void delete(ToDoItem todo_item);

    @Query("DELETE FROM todo_item")
    public void deleteAll();

    @Query("SELECT * FROM todo_item ORDER BY title")
    public LiveData<List<ToDoItem>> findAll();

    @Query("SELECT * FROM todo_item WHERE title LIKE :title")
    public LiveData<List<ToDoItem>> findByTitle(String title);

}
