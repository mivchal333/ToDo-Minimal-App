package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.settings.SettingsActivity;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;


public class MainActivity extends AppCompatActivity {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TODO_ACTIVITY_REQUEST_CODE = 2;

    private ToDoItemViewModel toDoItemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ToDoItemAdapter adapter = new ToDoItemAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel.class);
        toDoItemViewModel.findAll().observe(this, adapter::setToDoItems);

        FloatingActionButton addToDoItemButton = findViewById(R.id.add_button);
        addToDoItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
            startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.toDoItem_added),
                    Snackbar.LENGTH_LONG).show();
        } else if (requestCode == EDIT_TODO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.toDoItem_edited),
                    Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class ToDoItemHolder extends RecyclerView.ViewHolder {
        private final TextView toDoItemTitleTextView;
        private final TextView toDoItemDescriptionTextView;
        private ToDoItem toDoItem;

        public ToDoItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.todo_item_list, parent, false));

            toDoItemTitleTextView = itemView.findViewById(R.id.todo_title);
            toDoItemDescriptionTextView = itemView.findViewById(R.id.todo_description);
            View toDoItemItem = itemView.findViewById(R.id.todo_item);
            toDoItemItem.setOnLongClickListener(v -> {
                toDoItemViewModel.delete(toDoItem);
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.todo_deleted),
                        Snackbar.LENGTH_LONG)
                        .show();
                return true;
            });
            toDoItemItem.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
                intent.putExtra(EditToDoItemActivity.EXTRA_EDIT_TODO_ID, toDoItem.getId());
                startActivityForResult(intent, EDIT_TODO_ACTIVITY_REQUEST_CODE);
            });
        }

        public void bind(ToDoItem toDoItem) {
            toDoItemTitleTextView.setText(toDoItem.getTitle());
            toDoItemDescriptionTextView.setText(toDoItem.getDescription());
            this.toDoItem = toDoItem;
        }
    }

    private class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemHolder> {
        private List<ToDoItem> toDoItems;

        @NonNull
        @Override
        public ToDoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ToDoItemHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(ToDoItemHolder holder, int position) {
            if (toDoItems != null) {
                ToDoItem toDoItem = toDoItems.get(position);
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
}