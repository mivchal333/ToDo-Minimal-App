package pl.edu.pb.wi.todo_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditToDoItemActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_TODO_TITLE = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_TODO_DESCRIPTION = "pb.edu.pl.EDIT_BOOK_AUThOR";

    private EditText editTitleEditText;
    private EditText editAuthorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);


        editTitleEditText = findViewById(R.id.edit_todo_title);
        editAuthorEditText = findViewById(R.id.edit_todo_description);
        if (getIntent().hasExtra(EXTRA_EDIT_TODO_TITLE)) {
            editTitleEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_TODO_TITLE));
            editAuthorEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_TODO_DESCRIPTION));
        }
        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(e -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editTitleEditText.getText())
                    || TextUtils.isEmpty(editAuthorEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String title = editTitleEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_TODO_TITLE, title);
                String author = editAuthorEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_TODO_DESCRIPTION, author);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}