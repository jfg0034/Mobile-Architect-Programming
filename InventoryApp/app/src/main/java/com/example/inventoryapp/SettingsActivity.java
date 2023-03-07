package com.example.inventoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

// Settings Activity: Current version only supports permissions requests
// The request is manifested if user clicks the switch
// If permissions are granted, the user will not be prompted for permission request again

public class SettingsActivity extends AppCompatActivity {

    private int SMS_PERMISSION_CODE = 1;
    private static Switch switchPermission;
    private static String permission = "N";
    private static boolean switchOff = true;

    // Extra key
    public static final String EXTRA_PERMISSION = "com.example.inventoryapp.permission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Passes the view value to local field
        switchPermission = findViewById(R.id.permissionSwitch);
        // If permission was already granted the user will not be able to turn it off, unless they go on the phone's settings' permissions
        if (!switchOff) {
            switchPermission.setChecked(true);
            //switchPermission.setClickable(false);
        }
        else {
            switchPermission.setChecked(false);
        }

        // Defines listener to prompt the permission when turned on
        switchPermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                Intent intent = new Intent(SettingsActivity.this, InventoryService.class);
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                        switchOff = false;
                        permission = "Y";
                        intent.putExtra(EXTRA_PERMISSION, permission);
                        startService(intent);
                        Toast.makeText(SettingsActivity.this, "you have already granted this", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requestSMSPermission(); // If no permission is granted then a request is made
                    }


                }
                else {
                    switchOff = true;
                    permission = "N";
                    intent.putExtra(EXTRA_PERMISSION, permission);
                    startService(intent);
                    Toast.makeText(SettingsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Permission message
    private void requestSMSPermission(){
        Intent intent = new Intent(SettingsActivity.this, InventoryService.class);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
            new AlertDialog.Builder(this)
                    .setTitle("SMS permission request")
                    .setMessage("Allow InventoryApp to access SMS to receive notifications?")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            ActivityCompat.requestPermissions(SettingsActivity.this, new String[] {Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
                            switchPermission.setChecked(true);
                            switchOff = false;
                            permission = "Y";
                        }
                    })
                    .setNegativeButton("Deny",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            switchPermission.setChecked(false);
                            switchOff = true;
                            permission = "N";
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            intent.putExtra(EXTRA_PERMISSION, permission);
            startService(intent);

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
        }
    }

    // Verifies User permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent(SettingsActivity.this, InventoryService.class);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                switchPermission.setChecked(true);
                permission = "Y";
                intent.putExtra(EXTRA_PERMISSION, permission);
                startService(intent);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                switchPermission.setChecked(false);
            }
        }
    }

}