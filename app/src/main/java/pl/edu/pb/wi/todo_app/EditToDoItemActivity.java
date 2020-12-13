package pl.edu.pb.wi.todo_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.Locale;

import pl.edu.pb.wi.todo_app.database.entity.PlaceType;
import pl.edu.pb.wi.todo_app.database.entity.ToDoItem;
import pl.edu.pb.wi.todo_app.view.ToDoItemViewModel;

import static pl.edu.pb.wi.todo_app.MainActivity.EDIT_TODO_ACTIVITY_REQUEST_CODE;
import static pl.edu.pb.wi.todo_app.MainActivity.NEW_TODO_ACTIVITY_REQUEST_CODE;
import static pl.edu.pb.wi.todo_app.SearchPlaceActivity.EXTRA_PLACE_ADDRESS;
import static pl.edu.pb.wi.todo_app.SearchPlaceActivity.EXTRA_PLACE_NAME;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditToDoItemActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_TODO_ID = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_SEARCH_PLACE_QUERY = "pb.edu.pl.EDIT_BOOK_AUThOR";
    int MIN_SEARCH_INPUT_LENGTH = 3;
    public final String DATE_PATTERN = "dd-MM-yyyy";
    public final String EXTRA_REQUEST_CODE = "requestCode";


    final Calendar calendar = Calendar.getInstance();
    private EditText editTitleEditText;
    private EditText editDescriptionEditText;
    private EditText editTodoDateText;
    private final int SEARCH_PLACE_ACTIVITY_REQUEST_CODE = 3;
    private EditText editTodoPlaceText;
    private String placeAddress;
    private String placeName;
    private Date selectedDate;
    private PlaceType placeType = PlaceType.CUSTOM;
    private ToDoItemViewModel toDoItemViewModel;
    private ToDoItem currentTodo;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);
        this.requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, NEW_TODO_ACTIVITY_REQUEST_CODE);

        editTodoDateText = findViewById(R.id.edit_todo_date);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editTodoDateText.setOnClickListener(v -> {
            new DatePickerDialog(EditToDoItemActivity.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        editTitleEditText = findViewById(R.id.edit_todo_title);
        editDescriptionEditText = findViewById(R.id.edit_todo_description);
        editTodoPlaceText = findViewById(R.id.edit_todo_place);
        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel.class);
        if (this.requestCode == EDIT_TODO_ACTIVITY_REQUEST_CODE) {
            int selectedItemId = getIntent().getIntExtra(EXTRA_EDIT_TODO_ID, 0);
            toDoItemViewModel.findById(selectedItemId).observe(this, toDoItem -> {
                this.currentTodo = toDoItem;
                editTitleEditText.setText(toDoItem.getTitle());
                editDescriptionEditText.setText(toDoItem.getDescription());
                editTodoPlaceText.setText(getToDoPlaceLabel(toDoItem));
                Long toDoItemDate = toDoItem.getDate();
                if (toDoItemDate != null) {
                    calendar.setTimeInMillis(toDoItemDate);
                    selectedDate = new Date(toDoItemDate);
                    updateLabel(selectedDate);
                }
                if (PlaceType.DEFINED.equals(currentTodo.getPlaceType())) {
                    final Button navButton = findViewById(R.id.button_nav);
                    navButton.setVisibility(View.VISIBLE);

                    navButton.setOnClickListener(v -> {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + currentTodo.getPlaceAddress()));
                        startActivity(intent);

                    });
                }
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
                currentTodo.setPlaceType(placeType);
                if (selectedDate != null) {
                    currentTodo.setDate(selectedDate.getTime());
                }
                if (placeType.equals(PlaceType.DEFINED)) {
                    currentTodo.setPlaceAddress(placeAddress);
                    currentTodo.setPlaceName(placeName);
                } else {
                    currentTodo.setPlaceName(editTodoPlaceText.getText().toString());
                    currentTodo.setPlaceAddress(null);
                }

                if (this.requestCode == EDIT_TODO_ACTIVITY_REQUEST_CODE) {
                    toDoItemViewModel.update(currentTodo);
                } else {
                    currentTodo.setColor(ColorGenerator.MATERIAL.getRandomColor());
                    toDoItemViewModel.insert(currentTodo);
                }
                currentTodo = null;
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
        searchButton.setOnClickListener(e -> {
            String searchQueryInput = editTodoPlaceText.getText().toString();
            if (searchQueryInput.length() < MIN_SEARCH_INPUT_LENGTH) {
                Snackbar.make(findViewById(R.id.activityEditLayoutRl), getResources().getString(R.string.search_input_too_short),
                        Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            Intent intent = new Intent(EditToDoItemActivity.this, SearchPlaceActivity.class);
            intent.putExtra(EXTRA_SEARCH_PLACE_QUERY, searchQueryInput);
            startActivityForResult(intent, SEARCH_PLACE_ACTIVITY_REQUEST_CODE);
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_details);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getToDoPlaceLabel(ToDoItem toDoItem) {
        return getToDoPlaceLabel(toDoItem.getPlaceName(), toDoItem.getPlaceAddress());
    }

    public String getToDoPlaceLabel(String name, String address) {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name);
        }
        if (address != null) {
            sb.append(", ");
            sb.append(address);
        }
        return sb.toString();
    }

    private void updateLabel() {
        this.selectedDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        String formattedDate = simpleDateFormat.format(selectedDate);
        editTodoDateText.setText(formattedDate);
    }

    private void updateLabel(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);
        editTodoDateText.setText(formattedDate);
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