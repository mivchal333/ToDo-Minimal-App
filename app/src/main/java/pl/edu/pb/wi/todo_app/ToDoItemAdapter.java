package pl.edu.pb.wi.todo_app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;

class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemHolder> {
    private final MainActivity mainActivity;
    private List<ToDoItem> toDoItems;

    public ToDoItemAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ToDoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ToDoItemHolder(mainActivity, mainActivity.getLayoutInflater(), parent);
    }

    @Override
    public void onBindViewHolder(ToDoItemHolder holder, int position) {


        if (toDoItems != null) {
            ToDoItem toDoItem = toDoItems.get(position);
            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(toDoItem.getTitle().substring(0, 1), toDoItem.getColor());

            holder.toDoImageView.setImageDrawable(myDrawable);
            holder.bind(toDoItem);

        } else {
            Log.d("MainActivity", "No toDoItems");
        }
    }

    public int getItemCount() {
        if (toDoItems != null) {
            return toDoItems.size();
        } else {
            return 0;
        }
    }

    void setToDoItems(List<ToDoItem> toDoItems) {
        this.toDoItems = toDoItems;
        notifyDataSetChanged();
    }
}
