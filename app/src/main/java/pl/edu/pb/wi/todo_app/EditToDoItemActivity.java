package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import pl.edu.pb.wi.todo_app.database.entity.PlaceType;
import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;

import static pl.edu.pb.wi.todo_app.MainActivity.EDIT_TODO_ACTIVITY_REQUEST_CODE;
import static pl.edu.pb.wi.todo_app.MainActivity.NEW_TODO_ACTIVITY_REQUEST_CODE;
import static pl.edu.pb.wi.todo_app.SearchPlaceActivity.EXTRA_PLACE_ADDRESS;
import static pl.edu.pb.wi.todo_app.SearchPlaceActivity.EXTRA_PLACE_NAME;

public class EditToDoItemActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_TODO_ID = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_TODO_DESCRIPTION = "pb.edu.pl.EDIT_BOOK_AUThOR";
    public static final String EXTRA_SEARCH_PLACE_QUERY = "pb.edu.pl.EDIT_BOOK_AUThOR";
    public static final String EXTRA_EDIT_TODO_PLACE = "EXTRA_EDIT_TODO_PLACE";

    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private final int SEARCH_PLACE_ACTIVITY_REQUEST_CODE = 3;
    private EditText editTodoPlaceText;
    private String placeAddress;
    private String placeName;
    private PlaceType placeType = PlaceType.CUSTOM;
    private ToDoItemViewModel toDoItemViewModel;
    private ToDoItem currentTodo;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);
        requestCode = getIntent().getIntExtra("requestCode", NEW_TODO_ACTIVITY_REQUEST_CODE);


        editTitleEditText = findViewById(R.id.edit_todo_title);
        editDescriptionEditText = findViewById(R.id.edit_todo_description);
        editTodoPlaceText = findViewById(R.id.edit_todo_place);
        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel.class);
        if (requestCode == EDIT_TODO_ACTIVITY_REQUEST_CODE) {
            int selectedItemId = getIntent().getIntExtra(EXTRA_EDIT_TODO_ID, 0);
            toDoItemViewModel.findById(selectedItemId).observe(this, toDoItem -> {
                this.currentTodo = toDoItem;
                editTitleEditText.setText(toDoItem.getTitle());
                editDescriptionEditText.setText(toDoItem.getDescription());
                editTodoPlaceText.setText(getToDoPlaceLabel(toDoItem));
            });
        } else {
            currentTodo = new ToDoItem();
        }
        final Button button = findViewById(R.id.button_save);
        final Button searchButton = findViewById(R.id.button_search);
        button.setOnClickListener(e -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editTitleEditText.getText())
                    || TextUtils.isEmpty(editDescriptionEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String title = editTitleEditText.getText().toString();
                String description = editDescriptionEditText.getText().toString();
                currentTodo.setTitle(title);
                currentTodo.setDescription(description);
                currentTodo.setPlaceName(placeName);
                currentTodo.setPlaceAddress(placeAddress);
                currentTodo.setPlaceType(placeType);

                if (requestCode == EDIT_TODO_ACTIVITY_REQUEST_CODE) {
                    toDoItemViewModel.update(currentTodo);
                } else {
                    toDoItemViewModel.insert(currentTodo);
                }
                currentTodo = null;
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
        searchButton.setOnClickListener(e -> {
            Intent intent = new Intent(EditToDoItemActivity.this, SearchPlaceActivity.class);
            intent.putExtra(EXTRA_SEARCH_PLACE_QUERY, editTodoPlaceText.getText().toString());
            startActivityForResult(intent, SEARCH_PLACE_ACTIVITY_REQUEST_CODE);
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getToDoPlaceLabel(ToDoItem toDoItem) {
        return getToDoPlaceLabel(toDoItem.getPlaceName(), toDoItem.getPlaceAddress());
    }

    public String getToDoPlaceLabel(String name, String address) {
        if (name != null || address != null) {
            return name + ", " + address;

        } else return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_PLACE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            placeName = data.getStringExtra(EXTRA_PLACE_NAME);
            placeAddress = data.getStringExtra(EXTRA_PLACE_ADDRESS);
            placeType = PlaceType.DEFINED;
            editTodoPlaceText.setText(getToDoPlaceLabel(placeName, placeAddress));
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

}