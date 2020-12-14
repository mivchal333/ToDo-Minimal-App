package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.settings.SettingsActivity;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    public static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TODO_ACTIVITY_REQUEST_CODE = 2;
    SearchView editsearch;
    ToDoItemAdapter adapter;
    private ToDoItemViewModel toDoItemViewModel;
    private LiveData<List<ToDoItem>> liveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new ToDoItemAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel.class);

        liveData = toDoItemViewModel.findAll();
        loadAllToDos();

        FloatingActionButton addToDoItemButton = findViewById(R.id.add_button);
        addToDoItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
            startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editsearch = findViewById(R.id.simpleSearchView);
        editsearch.setOnQueryTextListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_state_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void loadAllToDos() {
        liveData.removeObservers(this);
        liveData = toDoItemViewModel.findAll();
        liveData.observe(this, adapter::setToDoItems);
    }

    private void loadToDosByState(boolean done) {
        liveData.removeObservers(this);
        liveData = toDoItemViewModel.findByDone(done);
        liveData.observe(this, adapter::setToDoItems);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                loadAllToDos();
                return;
            case 1:
                loadToDosByState(false);
                return;
            case 2:
                loadToDosByState(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        loadAllToDos();
    }
}