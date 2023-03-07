package com.example.inventoryapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class InventoryService extends IntentService {

    public InventoryService() {
        super("InventoryService");
    }

    // Control, defines whether the notification should show up or not
    private static String permission = "N"; // N:No Y:Yes

    @Override
    protected void onHandleIntent(Intent intent) {

        ItemsDB mItemsDB;
        mItemsDB = ItemsDB.getInstance(getApplicationContext());

        String low = "";
        createInventoryNotificationChannel();

        // If an EXTRA is found, the permission is set to the sent value
        if (intent.hasExtra(SettingsActivity.EXTRA_PERMISSION)) {
            permission = intent.getStringExtra(SettingsActivity.EXTRA_PERMISSION);
        }

        // Only if the permission is positive, the notifications will be displayed
        if (permission.compareTo("Y") == 0) {
            // Produces a String containing all items with ZERO stock
            for (Item item: mItemsDB.getItems()) {
                if (item.getQuantity() == 0) {
                    low = low + item.getItemName() + ", ";
                }
            }
            if (low.length() > 0) { // Only if there is a string to display a notification is created
                createInventoryNotification(mItemsDB.getUsername() +"'s DB: Low inventory (ZERO): " + low);
            }
        }
    }


    private final String CHANNEL_ID_INVENTORY = "channel_inventory";

    // Channel Notification creation method
    private void createInventoryNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) return;

        CharSequence name = "Inventory Channel";
        String description = "Notifies inventory set to zero";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_INVENTORY, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private final int NOTIFICATION_ID = 0;

    // Notification creation method
    private void createInventoryNotification(String text) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_INVENTORY)
                .setSmallIcon(R.drawable.ic_inventory50)
                .setContentTitle("InventoryApp")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}