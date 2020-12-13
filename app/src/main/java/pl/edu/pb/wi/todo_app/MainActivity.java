package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import pl.edu.pb.wi.todo_app.settings.SettingsActivity;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TODO_ACTIVITY_REQUEST_CODE = 2;
    SearchView editsearch;
    ToDoItemAdapter adapter;
    private ToDoItemViewModel toDoItemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new ToDoItemAdapter(this);
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

        editsearch = findViewById(R.id.simpleSearchView);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        int id = item.getItemId();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText.trim();
        if (text.length() > 1) {
            toDoItemViewModel.search(text).observe(this, adapter::setToDoItems);
        } else {
            toDoItemViewModel.findAll().observe(this, adapter::setToDoItems);
        }
        return false;
    }

}