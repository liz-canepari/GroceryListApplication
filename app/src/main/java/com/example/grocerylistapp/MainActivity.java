package com.example.grocerylistapp;

import static com.example.grocerylistapp.R.id.*;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.example.grocerylistapp.R.id;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> groceryList;  // Stores items in the current grocery list
    private GroceryAdapter adapter;         // Adapter for displaying the grocery list
    private SharedPreferences sharedPreferences; // SharedPreferences for saving and loading lists
    private EditText itemInput;             // Input field for adding new items
    private TextView selectedListName;      // Displays the name of the currently selected list

    private static final String SHARED_PREFS_KEY = "grocery_list_prefs";
    private static final String GROCERY_LIST_NAMES_KEY = "grocery_list_names";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        // Get references to UI elements
        selectedListName = findViewById(R.id.selected_list_name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        itemInput = findViewById(item_input);
        Button addButton = findViewById(add_button);
        RecyclerView recyclerView = findViewById(id.recyclerView);

        // Set up the grocery list and adapter for RecyclerView
        groceryList = new ArrayList<>();
        adapter = new GroceryAdapter(groceryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load the last used list if available and display its name
        String lastUsedListName = getLastUsedListName();
        if (lastUsedListName != null) {
            selectedListName.setText("Selected List: " + lastUsedListName);
        }
        groceryList.addAll(loadGroceryList(getLastUsedListName()));
        adapter.notifyDataSetChanged();

        // Set up the add button to add items to the list
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = itemInput.getText().toString().trim();
                if (!item.isEmpty()) {
                    groceryList.add(item);  // Add item to the grocery list
                    adapter.notifyDataSetChanged();
                    saveGroceryList(getLastUsedListName(), groceryList); // Save updated list
                    itemInput.setText("");  // Clear input field
                }
            }
        });

        // Load previously saved grocery list items
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
        groceryList.addAll(loadGroceryList(getLastUsedListName()));
        adapter.notifyDataSetChanged();
    }

    /**
     * Saves the last used list name to SharedPreferences
     */
    private void saveLastUsedListName(String listName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LAST_USED_LIST", listName);
        editor.apply();
    }

    /**
     * Retrieves the last used list name from SharedPreferences
     */
    private String getLastUsedListName() {
        return sharedPreferences.getString("LAST_USED_LIST", null);
    }

    /**
     * Saves a grocery list to SharedPreferences in JSON format
     */
    private void saveGroceryList(String listName, ArrayList<String> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list); // Convert list to JSON
        editor.putString("LIST_" + listName, json); // Save JSON string
        editor.apply();
    }

    /**
     * Loads a grocery list from SharedPreferences or returns an empty list if none is saved
     */
    private ArrayList<String> loadGroceryList(String listName) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("LIST_" + listName, null); // Get JSON string
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type); // Convert JSON to list
    }

    /**
     * Saves the list of grocery list names to SharedPreferences
     */
    private void saveListNames(ArrayList<String> listNames) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listNames);
        editor.putString(GROCERY_LIST_NAMES_KEY, json);
        editor.apply();
    }

    /**
     * Loads the list of grocery list names from SharedPreferences
     */
    private ArrayList<String> loadListNames() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(GROCERY_LIST_NAMES_KEY, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_new_list) {
            createNewList(); // Create a new list when selected
            return true;
        } else if (id == R.id.action_select_list) {
            selectDifferentList(); // Select a different list
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Prompts the user to create a new grocery list by entering a name
     */
    private void createNewList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New List Name");

        final EditText input = new EditText(this);  // Input for list name
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listName = input.getText().toString().trim();
                if (!listName.isEmpty()) {
                    groceryList = new ArrayList<>();  // Create a new empty list
                    adapter.updateList(groceryList);
                    saveGroceryList(listName, groceryList);  // Save new list to SharedPreferences
                    saveLastUsedListName(listName);  // Set as last used list

                    ArrayList<String> listNames = loadListNames(); // Load all list names
                    listNames.add(listName);  // Add new list name
                    saveListNames(listNames); // Save updated list names
                }
            }
        });

        // Display message indicating no selected list
        selectedListName.setText("Selected List: None");

        builder.setNegativeButton("Cancel", null); // Cancel option
        builder.show();
    }

    /**
     * Prompts the user to select a different grocery list from saved lists
     */
    private void selectDifferentList() {
        ArrayList<String> listNames = loadListNames(); // Load all list names

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a List");

        String[] NamesArray = listNames.toArray(new String[0]);
        builder.setItems(NamesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedList = NamesArray[which];  // Get selected list
                groceryList = loadGroceryList(selectedList); // Load list items
                adapter.updateList(groceryList);
                adapter.notifyDataSetChanged();
                saveLastUsedListName(selectedList);  // Set as last used list
                selectedListName.setText("Selected List: " + selectedList);  // Display selected list name
            }
        });
        builder.setNegativeButton("Cancel", null); // Cancel option
        builder.show();
    }
}
