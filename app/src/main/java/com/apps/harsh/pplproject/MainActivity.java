package com.apps.harsh.pplproject;
// TODO Export and Share feature, Image from file feature, Load from folder
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private EditText scanResults;
    private Uri imageUri;
    private EditText input;
    private String filename = "";
    private TextRecognizer detector;
    private CoordinatorLayout coordinator;
    private ImageView blank;
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    private static final String TAG = MainActivity.class.getSimpleName();

    public static void writeStringAsFile(final String fileContents, String fileName) {
        try {
            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory() + "/OCR to Doc", fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinator = findViewById(R.id.coordinator);
        scanResults = findViewById(R.id.results);
        scanResults.setImeOptions(EditorInfo.IME_ACTION_DONE);
        scanResults.setRawInputType(InputType.TYPE_CLASS_TEXT);

        blank = findViewById(R.id.blank);
        scanResults.setEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Exo2BoldItalicTextAppearance);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.app_name);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBoldItalic.ttf");
        mTitle.setText(toolbar.getTitle());
        mTitle.setTypeface(myCustomFont);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_save );
        final FloatingActionButton fab = new FloatingActionButton(this);
        fab.setType(FloatingActionButton.TYPE_NORMAL);
        fab.setImageDrawable(myIcon);
        fab.setColorPressedResId(R.color.colorPrimary);
        fab.setColorNormalResId(R.color.colorAccent);
        fab.setColorRippleResId(R.color.colorPrimaryDark);
        fab.setShadow(true);

        new SpringFloatingActionMenu.Builder(this)
                .fab(fab)
                //add menu item via addMenuItem(bgColor,icon,label,label color,onClickListener)
                .addMenuItem(R.color.tumblr_orange, R.mipmap.c, "C", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".c");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.tumblr_red, R.mipmap.cpp, "C++", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".cpp");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.tumblr_green, R.mipmap.txt, "Text", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".txt");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.tumblr_white, R.mipmap.java, "Java", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".java");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.tumblr_grey, R.mipmap.word, "Word", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // TODO Remove DOC Format
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".word");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.tumblr_blue, R.mipmap.pdf, "PDF", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // TODO
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".pdf");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                .addMenuItem(R.color.path_purple, R.mipmap.python, "Python", R.color.text_color, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scanResults.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "You need to scan first!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                            builderInner.setTitle("Enter the File Name: ");
                            input = new EditText(MainActivity.this);
                            input.setHeight(150);
                            input.setWidth(340);
                            input.setGravity(Gravity.START);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            builderInner.setView(input);
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = input.getText().toString();

                                    if (filename.equals("")) {
                                        Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        writeStringAsFile(scanResults.getText().toString(), filename + ".py");
                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                        scanResults.setEnabled(false);
                                        scanResults.setText("");
                                        blank.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builderInner.show();
                        }
                    }
                })
                //you can choose menu layout animation
                .animationType(SpringFloatingActionMenu.ANIMATION_TYPE_TUMBLR)
                //setup reveal color while the menu opening

                .revealColor(R.color.colorPrimary)
                //set FAB location, only support bottom center and bottom right
                .gravity(Gravity.CENTER | Gravity.BOTTOM)
                .onMenuActionListner(new OnMenuActionListener() {
                    @Override
                    public void onMenuOpen() {
                        //set FAB icon when the menu opened
                        Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_close );
                        fab.setImageDrawable(myIcon);
                    }

                    @Override
                    public void onMenuClose() {
                        //set back FAB icon when the menu closed
                        Drawable myIcon = getResources().getDrawable( R.drawable.ic_action_save );
                        fab.setImageDrawable(myIcon);
                    }
                })
                .build();

        File folder = new File(Environment.getExternalStorageDirectory() + "/OCR to Doc");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (!success) {
            Toast.makeText(this, "Failed to make folder", Toast.LENGTH_SHORT).show();
        }

        FabSpeedDial fabSpeedDial = findViewById(R.id.fab_speed_dial);


        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_save:
                        /*if (scanResults.getText().toString().equals("")) {
                            Snackbar snackbar = Snackbar
                                    .make(coordinator, "You need to Scan First!", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            break;
                        }
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                        builderSingle.setIcon(R.mipmap.ic_launcher);
                        builderSingle.setTitle("Select Type of File: ");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.add("Text");
                        arrayAdapter.add("Word");
                        arrayAdapter.add("PDF");
                        arrayAdapter.add("C Source");
                        arrayAdapter.add("Java");
                        arrayAdapter.add("Python");

                        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                                //builderInner.setMessage(strName);
                                builderInner.setTitle("Enter the File Name: ");
                                input = new EditText(MainActivity.this);
                                input.setHeight(150);
                                input.setWidth(340);
                                input.setGravity(Gravity.START);
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                builderInner.setView(input);
                                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        filename = input.getText().toString();

                                        if (filename.equals("")) {
                                            Toast.makeText(MainActivity.this, "File can not be Empty!", Toast.LENGTH_SHORT).show();
                                        } else {

                                            if (strName != null) {
                                                switch (strName) {
                                                    case "Text":
                                                        writeStringAsFile(scanResults.getText().toString(), filename + ".txt");
                                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case "C Source":
                                                        writeStringAsFile(scanResults.getText().toString(), filename + ".c");
                                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case "Python":
                                                        writeStringAsFile(scanResults.getText().toString(), filename + ".py");
                                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case "Java":
                                                        writeStringAsFile(scanResults.getText().toString(), filename + ".java");
                                                        Toast.makeText(MainActivity.this, "Saved Successfully on" + Environment.getExternalStorageDirectory() + "/OCR to Doc", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case "PDF":
                                                        //writeStringAsFile(scanResults.getText().toString(), filename + ".java");
                                                        break;
                                                }
                                            }
                                            scanResults.setEnabled(false);
                                            scanResults.setText("");
                                            blank.setVisibility(View.VISIBLE);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                builderInner.show();


                            }
                        });
                        builderSingle.show();*/
                        startGalleryChooser();
                        break;

                    case R.id.action_scan:
                        scanResults.setText("");
                        blank.setVisibility(View.INVISIBLE);
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                        break;
                }
                //TODO: Start some activity
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        /*Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);*/

        if (isTaskRoot()) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    }
                    scanResults.setEnabled(true);
                    if (textBlocks.size() == 0) {
                        Toast.makeText(this, "Scan Failed: Found nothing to scan!", Toast.LENGTH_SHORT).show();
                    } else {
                        scanResults.setText(scanResults.getText() + blocks + "\n");
                    }
                } else {
                    Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    String blocks = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    }
                    scanResults.setEnabled(true);
                    if (textBlocks.size() == 0) {
                        Toast.makeText(this, "Scan Failed: Found nothing to scan!", Toast.LENGTH_SHORT).show();
                    } else {
                        scanResults.setText(scanResults.getText() + blocks + "\n");
                    }
                } else {
                    Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(MainActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                //callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}
