/*This class uses the same activity to display and occult views as needed.
* future updates could refactor this functionality and add another activity
* to send and get the data*/

package com.example.inventoryapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InventoryViewActivity extends AppCompatActivity {

    private ItemsDB mItemsDB;
    private List<Item> itemList = new ArrayList<Item>();

    // Recycler view and private adapter
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;

    // Private view fields
    private TextView greeting;
    private String currentUsername = "";
    private Button add;
    private Button edit;
    private Button delete;
    private Button cancel;
    private Button submit;

    private FloatingActionButton removeOne;
    private FloatingActionButton addOne;

    private EditText itemName;
    private EditText itemQuantity;

    private TextView nameLabel;
    private TextView quantityLabel;
    private TextView nameLabelTable;
    private TextView quantityLabelTable;


    private static Item tempItem = new Item(); // Holds item values for visualization
    private static List<Item> tempItemList = new ArrayList<Item>(); // Holds temp values
    private static final int MAX_COUNT = tempItem.getMaxCount(); // Sets a max amount of items
    private static int pos = 0; // position

    @Override
    // When user presses back button the default view is set so no previous states are invoked
    public void onBackPressed() {
        super.onBackPressed();
        cancelAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_view);

        greeting = findViewById(R.id.greeting);
        mItemsDB = ItemsDB.getInstance(getApplicationContext());
        Intent intent = getIntent();
        // If an EXTRA is passed the greeting field is updated
        if (intent.hasExtra(MainActivity.EXTRA_USERNAME)) {
            currentUsername = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
            greeting.setText(currentUsername + "'s Inventory Table:");
            // Sets the database Username so data is only retrieved to the appropriate user
            mItemsDB.setUser(currentUsername);
        }
        // If no EXTRA exists, the default database will be accessed by "Guest"
        // This default user is only 5-character long, so no user can be created with this string
        else {
            mItemsDB.setUser("Guest"); // Only possible username for public access, other usernames are restricted to be longer
            greeting.setText("Guest: Data will be available to anyone.");
        }

        itemList = mItemsDB.getItems();
        this.pos = itemList.size() - 1; // Sets position to the end of the list

        recyclerView = findViewById(R.id.itemRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // renders the list from last to first item

        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(pos); // Sets view to last item in list in reverse, so is set at the top

        // Assign views to fields
        add = findViewById(R.id.add_items);
        edit = findViewById(R.id.edit_items);
        delete = findViewById(R.id.delete_items);
        cancel = findViewById(R.id.cancel_view);
        submit = findViewById(R.id.submit);

        removeOne = findViewById(R.id.remove_one);
        addOne = findViewById(R.id.add_one);

        itemName = findViewById(R.id.item_name);
        itemQuantity = findViewById(R.id.item_quantity);

        nameLabel = findViewById(R.id.name_label);
        quantityLabel = findViewById(R.id.quantity_label);

        nameLabelTable = findViewById(R.id.name_label_table);
        quantityLabelTable = findViewById(R.id.quantity_label_table);

        // If adapter is set to mode 'E' (Edit), the item details are displayed to be deleted
        if (mAdapter.getMode() == 'E') {
            Item item = new Item();
            displayItemForm();
            tempItem = mAdapter.getItem();
            displayOneItem(tempItem);
        }

        // If list is empty (i.e. after a brand new user is created) only "Add" button is needed
        if(mItemsDB.getItems().size() == 0) {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            nameLabelTable.setVisibility(View.GONE);
            quantityLabelTable.setVisibility(View.GONE);
        }

    }

    // Private helper function to display only the item form to add or delete
    private void displayItemForm() {
        recyclerView.setVisibility(View.GONE);
        nameLabelTable.setVisibility(View.GONE);
        quantityLabelTable.setVisibility(View.GONE);

        itemName.setVisibility(View.VISIBLE);
        itemQuantity.setVisibility(View.VISIBLE);

        removeOne.setVisibility(View.VISIBLE);
        addOne.setVisibility(View.VISIBLE);

        nameLabel.setVisibility(View.VISIBLE);
        quantityLabel.setVisibility(View.VISIBLE);

        add.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        edit.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.VISIBLE);
        delete.setBackgroundColor(Color.parseColor("#E11616"));
    }

    // Private helper function to display the item list
    private void displayItemList() {
        itemName.setVisibility(View.GONE);
        itemQuantity.setVisibility(View.GONE);

        nameLabel.setVisibility(View.GONE);
        quantityLabel.setVisibility(View.GONE);

        removeOne.setVisibility(View.GONE);
        addOne.setVisibility(View.GONE);

        add.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);

        recyclerView.setVisibility(View.VISIBLE);
        nameLabelTable.setVisibility(View.VISIBLE);
        quantityLabelTable.setVisibility(View.VISIBLE);

        // If list is empty only "Add" is displayed
        if(mItemsDB.getItems().size() == 0) {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            nameLabelTable.setVisibility(View.GONE);
            quantityLabelTable.setVisibility(View.GONE);
        }

        pos = mItemsDB.getItems().size() - 1;
        recyclerView.scrollToPosition(pos);
    }

    private void displayOneItem (Item item){ //Passes item info or provides a blank form
        itemName.setText(item.getItemName());
        itemQuantity.setText(Integer.toString(item.getQuantity()));
    }

    // Switches list view to the item form
    public void onClickAddItems(View v){
        Item item = new Item();
        tempItem = item;
        mAdapter.setMode('A'); // Mode 'A' sets a different logic within the adapter
        displayOneItem(item);
        displayItemForm();
        delete.setVisibility(View.GONE); // delete method when creating a new item is unneeded and incoherent
    }

    // Switches to edit mode, items become clickable so they can be edited
    public void onClickEditItems(View v){
        mAdapter.setMode('E');
        mAdapter.notifyDataSetChanged();

        add.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        edit.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.GONE);
        delete.setVisibility(View.VISIBLE);
    }

    // Switches to delete mode, checkboxes are enabled to select all items to be deleted
    public void onClickDeleteItems(View v){
        add.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        edit.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.GONE);
        mAdapter.setMode('D');
        mAdapter.notifyDataSetChanged();
        List<String> toDelete = mAdapter.getItemList();
        if (toDelete.size() > 0) {
            for (int i = 0; i < toDelete.size(); ++i) {
                if (!mItemsDB.deleteItem(toDelete.get(i))) {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
            cancelAll();
            Toast.makeText(this, "All selected items deleted.", Toast.LENGTH_SHORT).show();
        }
        else {  // toDelete.size() is zero, so delete button is selected from the item form view,
            if (tempItem.getItemName().length() != 0) {
                mItemsDB.deleteItem(tempItem.getItemName());
                Toast.makeText(this, "'" + tempItem.getItemName() + "' deleted.", Toast.LENGTH_SHORT).show();
                cancelAll();
            }
        }

        if (mAdapter.getMode() == 'D') {
            delete.setBackgroundColor(Color.parseColor("#E11616"));
            delete.setText("Delete checked items");
        }
    }

    // Helper function, prevents null entry
    private int validNum(String n) {
        if (n.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(n);
    }

    // Makes changes to the database: adds or edits items
    public void onSubmit(View v) {
        String name = itemName.getText().toString().trim();
        int quantity = validNum(itemQuantity.getText().toString());

        // Add
        if (tempItem.getItemName().length() == 0) { // First time form opens so no item currently exists
            if (name.length()>0 && isValidQuantity(quantity) && isUnique(name)) {
                long tempCode = mItemsDB.addItem(name, quantity);
                if (tempCode >= 0) {
                    cancelAll();
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

        // Edit
        else {
            Item item = new Item();
            if (name.length() > 0 && isValidQuantity(quantity)) {
                item = mItemsDB.getItem(tempItem.getItemName());
                item.setItemName(name);
                item.setQuantity(quantity);
                mItemsDB.updateItem(item);

                cancelAll();
                Toast.makeText(this, "Item updated.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Invalid name or quantity.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Reduces item quantity by 1
    public void onClickRemoveOne(View v) {
        int currNum = Integer.parseInt(itemQuantity.getText().toString().trim());
        if (currNum > 0) {
            itemQuantity.setText(Integer.toString(currNum - 1));
        }
        else {
            itemQuantity.setText(Integer.toString(0));
        }
    }

    // Increases item quantity by 1
    public void onClickAddOne(View v) {
        int currNum = Integer.parseInt(itemQuantity.getText().toString().trim());
        if (isValidQuantity(currNum)) {
            itemQuantity.setText(Integer.toString(currNum + 1));
        }
        else {
            itemQuantity.setText(Integer.toString(MAX_COUNT));
        }
    }

    // Sets functionality to Cancel button
    public void onClickCancelMode(View v) {
        cancelAll();
    }

    // Helper function, validates value of item quantity
    private boolean isValidQuantity(int num) {
        if (num >= MAX_COUNT || num < 0) {
            return false;
        }
        return true;
    }

    // Helper function to determine item is not present in the database already
    private boolean isUnique(String name) {
        if (mItemsDB.getItem(name) == null) {
            return true;
        }
        Toast.makeText(this, "Item already exists.", Toast.LENGTH_SHORT).show();
        return false;
    }

    // Helper function to return default values
    public void cancelAll(){
        mAdapter.setMode('C');
        delete.setBackgroundColor(Color.parseColor("#49A742"));
        delete.setText("Delete");
        displayItemList();
        tempItem = new Item(); // set back to an empty object

        // Get all the actual elements in the database
        mAdapter.setItemList(mItemsDB.getItems());
        recyclerView.setAdapter(mAdapter);
    }
}