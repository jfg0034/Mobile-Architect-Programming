package com.example.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Custom Adapter class
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
    private List<Item> mItemList;
    private static Item currItem = new Item(); // Stores selected item
    private static List<String> tempItems = new ArrayList<String>(); // Stores list of temporary selected items
    Context context;
    private static char mode = 'C'; // C:cancel, A:add, E:edit
    private String currItemName = "";
    private int currItemQuantity;

    public ItemAdapter(List<Item> items, Context aContext) {
        mItemList = items;
        context = aContext;
    }

    // Validates number in case input is left blank
    private int validNum(String n) {
        if (n.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(n);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int pos) {

        // Sets values for each view
        holder.item_name.setText(mItemList.get(pos).getItemName());
        holder.item_quantity.setText(Integer.toString(mItemList.get(pos).getQuantity()));

        // Edit mode
        if (mode == 'E') {
            // views become clickable
            holder.item_name.setClickable(true);
            holder.item_quantity.setClickable(true);
            holder.editMark.setVisibility(View.VISIBLE);

            // Listeners are created to store current values
            // In this version the clicked items restart the activity, prompting the edit form to be displayed
            holder.item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String itemName = holder.item_name.getText().toString();
                    int quantity = validNum(holder.item_quantity.getText().toString());
                    setItemName(itemName);
                    setCurrItemQuantity(quantity);
                    currItem.setItemName(currItemName);
                    currItem.setQuantity(currItemQuantity);
                    notifyDataSetChanged();
                    Intent intent = new Intent(context, InventoryViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
            holder.item_quantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String itemName = holder.item_name.getText().toString();
                    int quantity = validNum(holder.item_quantity.getText().toString());
                    setItemName(itemName);
                    setCurrItemQuantity(quantity);
                    currItem.setItemName(currItemName);
                    currItem.setQuantity(currItemQuantity);
                    notifyDataSetChanged();
                    Intent intent = new Intent(context, InventoryViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }

        // Delete mode
        else if (mode == 'D') {
            // Checkboxes become visible
            holder.item_check.setVisibility(View.VISIBLE);
            holder.editMark.setVisibility(View.GONE);
            // Listener is created to update temporary list, which holds all clicked items to be deleted
            holder.item_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.item_check.isChecked()) {
                        tempItems.add(holder.item_name.getText().toString());
                    }
                    else {
                        // Every time a checkbox is unclicked, the item is removed from the delete list
                        tempItems.remove(holder.item_name.getText().toString());
                    }
                }
            });
            holder.item_name.setClickable(false);
            holder.item_quantity.setClickable(false);
        }

        else {
            // Clear tempItems list so no clicked checkboxes are stored
            tempItems = new ArrayList<String>();
            currItem = new Item();
            holder.item_name.setClickable(false);
            holder.item_quantity.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    // ItemHolder class contains views in the recycler view
    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView item_name;
        TextView item_quantity;
        CheckBox item_check;
        ImageView editMark;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.itemName);
            item_quantity = itemView.findViewById(R.id.itemQuantity);
            item_check = itemView.findViewById(R.id.check);
            editMark = itemView.findViewById(R.id.edit_mark);
        }
    }

    public void setItemList(List<Item> itemList) { // Sets list to be used by the adapter
        this.mItemList = itemList;
    }

    public void setMode(char m) {
        this.mode = m;
    }

    public char getMode() {
        return this.mode;
    }

    public Item getItem() {
        return this.currItem;
    }

    public void setItemName(String s){ // updates value of temporary item
        this.currItemName = s;
    }
    public void setCurrItemQuantity(int n){ // updates value of temporary item
        this.currItemQuantity = n;
    }

    public List<String> getItemList() {
        return this.tempItems; // returns temporary items to be deleted
    }

}
