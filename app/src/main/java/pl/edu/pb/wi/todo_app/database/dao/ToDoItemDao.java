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
    void update(ToDoItem todo_item);

    @Delete
    void delete(ToDoItem todo_item);

    @Query("DELETE FROM todo_item")
    void deleteAll();

    @Query("SELECT * FROM todo_item ORDER BY title")
    LiveData<List<ToDoItem>> findAll();

    @Query("SELECT * FROM todo_item WHERE title LIKE :title ORDER BY title")
    LiveData<List<ToDoItem>> findByTitle(String title);

    @Query("SELECT * FROM todo_item WHERE id LIKE :id")
    LiveData<ToDoItem> findById(Integer id);

    @Query("SELECT * FROM TODO_ITEM WHERE title LIKE '%' || :query || '%' ORDER BY title")
    LiveData<List<ToDoItem>> search(String query);

    @Query("SELECT * FROM TODO_ITEM WHERE done = :done ORDER BY title")
    LiveData<List<ToDoItem>> findByDone(boolean done);
}
