package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;

class ToDoItemHolder extends RecyclerView.ViewHolder {
    private final MainActivity mainActivity;
    private final TextView toDoItemTitleTextView;
    private final TextView toDoItemDescriptionTextView;
    private final CheckBox todoItemDoneCheckbox;
    private final ToDoItemViewModel toDoItemViewModel;
    ImageView toDoImageView;
    private ToDoItem toDoItem;


    public ToDoItemHolder(MainActivity mainActivity, LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.todo_item_list, parent, false));
        this.mainActivity = mainActivity;

        toDoItemViewModel = ViewModelProviders.of(mainActivity).get(ToDoItemViewModel.class);
        toDoItemTitleTextView = itemView.findViewById(R.id.todo_title);
        toDoItemDescriptionTextView = itemView.findViewById(R.id.todo_description);
        toDoImageView = itemView.findViewById(R.id.todo_image);
        todoItemDoneCheckbox = itemView.findViewById(R.id.todo_done_checkbox);
        View toDoItemItem = itemView.findViewById(R.id.todo_item);
        toDoItemItem.setOnLongClickListener(v -> {
            toDoItemViewModel.delete(toDoItem);
            Snackbar.make(mainActivity.findViewById(R.id.coordinator_layout),
                    mainActivity.getString(R.string.todo_deleted),
                    Snackbar.LENGTH_LONG)
                    .show();
            return true;
        });
        toDoItemItem.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, EditToDoItemActivity.class);
            intent.putExtra(EditToDoItemActivity.EXTRA_EDIT_TODO_ID, toDoItem.getId());
            mainActivity.startActivityForResult(intent, MainActivity.EDIT_TODO_ACTIVITY_REQUEST_CODE);
        });
        todoItemDoneCheckbox.setOnClickListener(v -> {
            toDoItem.setDone(!toDoItem.isDone());
            toDoItemViewModel.update(toDoItem);
        });
    }

    public void bind(ToDoItem toDoItem) {
        toDoItemTitleTextView.setText(toDoItem.getTitle());
        toDoItemDescriptionTextView.setText(toDoItem.getDescription());
        todoItemDoneCheckbox.setChecked(toDoItem.isDone());
        this.toDoItem = toDoItem;
    }
}
