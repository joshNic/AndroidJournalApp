package info.mysecretjournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.mysecretjournal.helpers.DatabaseHelper;
import info.mysecretjournal.helpers.EntriesAdapter;
import info.mysecretjournal.model.Entry;
import info.mysecretjournal.utils.MyDividerItemDecoration;
import info.mysecretjournal.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private EntriesAdapter mAdapter;
    private List<Entry> entriesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noEntriesView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noEntriesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);

        entriesList.addAll(db.getAllEntries());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new EntriesAdapter(this, entriesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyEntries();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createEntry(String entryTitle, String entryBody ) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertEntry(entryTitle, entryBody);

        // get the newly inserted note from db
        Entry e = db.getEntry(id);

        if (e != null) {
            // adding new note to array list at 0 position
            entriesList.add(0, e);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyEntries();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateEntry(String entryTitle, String entryBody, int position) {
        Entry e = entriesList.get(position);
        // updating note text
        e.setEntryTitle(entryTitle);
        e.setEntryBody(entryBody);

        // updating note in db
        db.updateEntry(e);

        // refreshing the list
        entriesList.set(position, e);
        mAdapter.notifyItemChanged(position);

        toggleEmptyEntries();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteEntry(int position) {
        // deleting the note from db
        db.deleteEntry(entriesList.get(position));

        // removing the note from the list
        entriesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyEntries();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, entriesList.get(position), position);
                } else {
                    deleteEntry(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Entry entry, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.entry_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputEntryTitle = view.findViewById(R.id.entryTitle);
        final EditText inputEntryBody = view.findViewById(R.id.entryBody);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_entry_title) : getString(R.string.lbl_edit_entry_title));

        if (shouldUpdate && entry != null) {
            inputEntryTitle.setText(entry.getEntryTitle());
            inputEntryBody.setText(entry.getEntryBody());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputEntryTitle.getText().toString()) || TextUtils.isEmpty(inputEntryBody.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && entry != null) {
                    // update note by it's id
                    updateEntry(inputEntryTitle.getText().toString(), inputEntryBody.getText().toString(), position);
                } else {
                    // create new note
                    createEntry(inputEntryTitle.getText().toString(), inputEntryBody.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyEntries() {
        // you can check notesList.size() > 0

        if (db.getEntriesCount() > 0) {
            noEntriesView.setVisibility(View.GONE);
        } else {
            noEntriesView.setVisibility(View.VISIBLE);
        }
    }
}