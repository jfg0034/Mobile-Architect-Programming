package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    // Private view fields
    private Button add;
    private Button search;
    private Button edit;
    private Button delete;
    private Button submit;
    private Button cancel;
    private FloatingActionButton removeOne;
    private FloatingActionButton addOne;
    private EditText searchBar;
    private EditText itemName;
    private EditText itemQuantity;
    private TextView nameLabel;
    private TextView quantityLabel;

    private ItemsDB mItemsDB;

    private static Item tempItem = new Item(); // Holds item values for visualization

    // Retrieves max number allowed for an item quantity
    private static final int MAX_COUNT = tempItem.getMaxCount();


    private String currentItem = "";
    public static final String EXTRA_USERNAME = "com.example.inventoryapp.username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrieve views by ID
        add = findViewById(R.id.add);
        search = findViewById(R.id.search);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        removeOne = findViewById(R.id.remove_one);
        addOne = findViewById(R.id.add_one);
        searchBar = findViewById(R.id.search_bar);
        itemName = findViewById(R.id.item_name);
        itemQuantity = findViewById(R.id.item_quantity);
        nameLabel = findViewById(R.id.name_label);
        quantityLabel = findViewById(R.id.quantity_label);

        mItemsDB = ItemsDB.getInstance(getApplicationContext());
    }

    // Local helper function
    private void displayItem() {
        itemName.setInputType(InputType.TYPE_NULL);
        itemName.setFocusableInTouchMode(false);
        itemName.setVisibility(View.VISIBLE);

        itemQuantity.setInputType(InputType.TYPE_NULL);
        itemQuantity.setFocusableInTouchMode(false);
        itemQuantity.setVisibility(View.VISIBLE);

        add.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        removeOne.setVisibility(View.GONE);
        addOne.setVisibility(View.GONE);

        searchBar.setVisibility(View.VISIBLE);

        nameLabel.setVisibility(View.VISIBLE);
        quantityLabel.setVisibility(View.VISIBLE);
    }

    // Local helper function
    private void displayEditButtons() {
        add.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        search.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);

        itemName.setInputType(InputType.TYPE_CLASS_TEXT);
        itemName.setFocusableInTouchMode(true);
        itemName.setVisibility(View.VISIBLE);

        itemQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        itemQuantity.setFocusableInTouchMode(true);
        itemQuantity.setVisibility(View.VISIBLE);

        searchBar.setVisibility(View.GONE);

        nameLabel.setVisibility(View.VISIBLE);
        quantityLabel.setVisibility(View.VISIBLE);
    }

    // Helper function
    private void clearDisplay() {
        delete.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        itemName.setVisibility(View.GONE);
        itemQuantity.setVisibility(View.GONE);

        nameLabel.setVisibility(View.GONE);
        quantityLabel.setVisibility(View.GONE);
    }

    // Validates user input is not left blank
    private boolean isValidQuantity(int num) {
        if (num >= MAX_COUNT || num < 0) {
            return false;
        }
        return true;
    }

    // Helper function, prevents null entry
    private int validNum(String n) {
        if (n.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(n);
    }


    //-----onClick FUNCTIONS------

    public void onClickAdd(View v) {
        // Only displays appropriate components
        displayEditButtons();
        submit.setText("Add item");
        itemName.setText("");
        itemQuantity.setText("0");
    }

    public void onClickSearch(View v) {

        // If found:
        displayItem();
        String name = searchBar.getText().toString().trim();
        tempItem = mItemsDB.getItem(name);
        if (tempItem != null) {
            itemName.setText(tempItem.getItemName());
            itemQuantity.setText(Integer.toString(tempItem.getQuantity()));
        }
        else {
            tempItem = new Item();
            clearDisplay();
            Toast.makeText(this, "Item not found.", Toast.LENGTH_SHORT).show();
        }
        searchBar.setText(""); // clears input data
        int num = mItemsDB.getItems().size();
        }

    public void onClickEdit(View v) {
        displayEditButtons();
        submit.setText("Submit changes"); // sets type of function from submit button

        removeOne.setVisibility(View.VISIBLE);
        addOne.setVisibility(View.VISIBLE);
    }

    public void onClickDelete(View v) {
        // If deleted
        if (mItemsDB.deleteItem(tempItem.getItemName())) {
            clearDisplay();
            Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickSubmit(View v) {
        String iName = itemName.getText().toString().trim();
        int iQuantity = validNum(itemQuantity.getText().toString().trim());
        String control = submit.getText().toString().trim(); // determines type of behavior

        if (control.equals("Submit changes")) { //Item exists, so a valid ID exists
            // edit
            Item item = new Item();
            if (iName.length()>0 && isValidQuantity(iQuantity)) {
                item.setID(tempItem.getID());
                item.setItemName(iName);
                item.setQuantity(iQuantity);
                mItemsDB.updateItem(item);

                // Update values of temporary item
                tempItem.setItemName(iName);
                tempItem.setQuantity(iQuantity);
                displayItem();
                Toast.makeText(this, "Item updated.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Invalid name or quantity.", Toast.LENGTH_SHORT).show();
            }
        }
        // add
        else {
            if (iName.length()>0 && isValidQuantity(iQuantity)) {
                long tempCode = mItemsDB.addItem(iName, iQuantity);
                if (tempCode >= 0) {
                    tempItem.setID(tempCode);
                    tempItem.setItemName(iName);
                    tempItem.setQuantity(iQuantity);
                    displayItem();
                    Toast.makeText(this, "Item created.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Invalid name or quantity.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void onClickCancel(View v) {
        // Do not update fields and display previous data
        if (submit.getText().equals("Add item")) {
            displayItem(); // displays all missing fields
            clearDisplay(); // occults uneeded fields
        }
        else {
            itemName.setText(tempItem.getItemName());
            itemQuantity.setText(Integer.toString(tempItem.getQuantity()));
            displayItem();
        }

    }

    public void onClickRemoveOne(View v) {
        // Reduces quantity by 1
        int currNum = Integer.parseInt(itemQuantity.getText().toString().trim());
        if (currNum > 0) {
            itemQuantity.setText(Integer.toString(currNum - 1));
        }
        else {
            itemQuantity.setText(Integer.toString(0));
        }
    }

    public void onClickAddOne(View v) {
        // Increases quantity by 1
        int currNum = Integer.parseInt(itemQuantity.getText().toString().trim());
        if (isValidQuantity(currNum)) {
            itemQuantity.setText(Integer.toString(currNum + 1));
        }
        else {
            itemQuantity.setText(Integer.toString(MAX_COUNT));
        }
    }
}