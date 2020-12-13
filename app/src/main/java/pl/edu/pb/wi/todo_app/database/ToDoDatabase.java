package pl.edu.pb.wi.todo_app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.edu.pb.wi.todo_app.database.conventer.PlaceTypeConverter;
import pl.edu.pb.wi.todo_app.database.dao.ToDoItemDao;
import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;

@Database(entities = {ToDoItem.class}, version = 1, exportSchema = false)
@TypeConverters(PlaceTypeConverter.class)
public abstract class ToDoDatabase extends RoomDatabase {

    public abstract ToDoItemDao toDoItemDao();

    private static volatile ToDoDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriterExecutor.execute(() -> {
                ToDoItemDao dao = INSTANCE.toDoItemDao();
                dao.deleteAll();

                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setTitle("Title");
                toDoItem.setDescription("Description");
                toDoItem.setDate(new Date().getTime());

                dao.insert(toDoItem);
            });
        }
    };

    public static ToDoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ToDoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ToDoDatabase.class, "todo_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
