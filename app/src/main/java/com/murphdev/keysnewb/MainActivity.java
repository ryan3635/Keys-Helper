package com.murphdev.keysnewb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Context context = this;
    MediaPlayer mp;
    Spinner scaleSpinner, chordSpinner;
    RadioGroup rootNotes;
    RadioButton root;
    TextView infoScale, infoChord;
    ImageButton keyButton;

    //24 keys each with these 4 properties
    Key [] keys = new Key[24];
    Key key;
    String keyID;
    boolean isRoot = false;
    boolean isScale = false;
    boolean isChord = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rootNotes = findViewById(R.id.rootNotes);
        infoScale = findViewById(R.id.infoSelectedScale);
        infoChord = findViewById(R.id.infoSelectedChord);

        //Scale type and chord selection spinners initialization
        scaleSpinner = findViewById(R.id.scaleType);
        ArrayAdapter<String> scaleAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.scaleTypes));
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scaleSpinner.setAdapter(scaleAdapter);
        chordSpinner = findViewById(R.id.chord);
        ArrayAdapter<String> chordAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.initialChords));
        chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chordSpinner.setAdapter(chordAdapter);

        //Initialize keys array
        keysInitialization();
        keys[1].setRoot();
        keys[1].setChord();
        keys[1].setScale();
        keys[23].setRoot();
        keys[23].setChord();
        keys[23].setScale();

        chordSpinner.setOnItemSelectedListener(this);
        scaleSpinner.setOnItemSelectedListener(this);
    }

    //playNote: Called when a key is clicked
    public void playNote(View v) {
        Resources res = context.getResources();
        int id = v.getId();
        String noteId = getResources().getResourceEntryName(id);
        int note = res.getIdentifier(noteId, "raw", context.getPackageName());

        //For testing~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //testing key class - remove when done
        /*if (noteId.equals("c1")) {
            isRoot = true;
            isScale = true;
            isChord = true;
            c1.setRoot();
            c1.setScale();
            c1.setChord();
            Toast.makeText(this, "root? " + isRoot + " scale? " + isScale + " chord? " + isChord, Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, noteId, Toast.LENGTH_SHORT).show();
        int i;
        int j = 0;
        String tempId, realId = "";
        loop:
        for (i = 0; i < keys.length; i++) {
            tempId = keys[i].idCheck();
            if (tempId.equals(noteId)) {
                j = i;
                realId = tempId;
                break loop;
                //testId = "hi";
            }
        }

        boolean testRoot = keys[j].rootCheck();
        boolean testScale = keys[j].scaleCheck();
        boolean testChord = keys[j].scaleCheck();

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Test");
        alert.setCancelable(false);
        alert.setMessage("ID: " + realId + " root: " + testRoot + " scale: " + testScale + " chord: " + testChord);
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();}
        });
        alert.show();

         */

        //Plays sound
        mp = MediaPlayer.create(context, note);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
        mp.start();

        //Displays feedback on key click
        keyButton = findViewById(id);
        final int imageId = res.getIdentifier(noteId, "drawable", context.getPackageName());
        if (noteId.equals("cs1") || noteId.equals("ds1") || noteId.equals("fs1") || noteId.equals("gs1") || noteId.equals("as1") ||
                noteId.equals("cs2") || noteId.equals("ds2") || noteId.equals("fs2") || noteId.equals("gs2") || noteId.equals("as2"))
            keyButton.setImageResource(R.drawable.blackkeyfeedback);
        else keyButton.setImageResource(R.drawable.whitekeyfeedback);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                keyButton.setImageResource(imageId);
            }
        }, 15);
    }


    //rootUpdate: Called when the root note is changed - highlights keys and updates chord selection
    public void rootUpdate(View v) {
        //Updating scale for information section
        int selectedRoot = rootNotes.getCheckedRadioButtonId();
        root = findViewById(selectedRoot);
        String rootNote = (String) root.getText();
        String scale = scaleSpinner.getSelectedItem().toString();
        String scaleText = rootNote + " " + scale;
        infoScale.setText(scaleText);

        //Updating chord selection options
        //...
    }


    //onItemSelected: Called when either a scale type or chord is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.scaleType:
                //Updating scale for information section
                String scale = parent.getItemAtPosition(position).toString();
                int selectedRoot = rootNotes.getCheckedRadioButtonId();
                root = findViewById(selectedRoot);
                String rootNote = (String) root.getText();
                String scaleText = rootNote + " " + scale;
                infoScale.setText(scaleText);

                //Highlighting keys for selected scale
                //...call a function that goes through each scale to find step formula of selected scale (matches string)
                //..use step formula along with rootNote to build array and then highlight keys corresponding to that array

                //Updating chord selection options
                //...
                break;

            case R.id.chord:
                //Updating chord for information section
                String chord = parent.getItemAtPosition(position).toString();
                infoChord.setText(chord);

                //Highlighting keys for selected chord
                int selectedRoot2 = rootNotes.getCheckedRadioButtonId();
                root = findViewById(selectedRoot2);
                String rootNote2 = (String) root.getText();
                //...call a function that goes through each chord to find step formula of selected chord (matches string)
                //..use step formula along with rootNote2 to build array and then highlight keys corresponding to that array
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Not used -> forced to include with AdapterView class
    }

    //checkBoxes: Called when either checkbox is clicked
    public void checkBoxes (View v) {
        boolean isChecked = ((CheckBox)v).isChecked();
        int checkboxId = v.getId();
        switch (checkboxId) {
            case R.id.hideSelection:
                if (isChecked)
                    Toast.makeText(this, "scale selection hidden", Toast.LENGTH_SHORT).show();
                    //go through selected notes array and update image from yellow (scale) and green (chord) key image to white/black key image
                else
                    Toast.makeText(this, "scale selection shown", Toast.LENGTH_SHORT).show();
                    //go through selected notes array and update image from white/black key image to yellow key image
                break;
            case R.id.hideNotes:
                if (isChecked)
                    Toast.makeText(this, "notes hidden", Toast.LENGTH_SHORT).show();
                    //if (isSelected)
                        //replace key image with regular yellow/green key image
                    //else
                        //replace key image with regular white/black key image
                else
                    Toast.makeText(this, "notes shown", Toast.LENGTH_SHORT).show();
                    //if (isSelected)
                        //replace key image with yellow key image with note
                    //else
                        //replace key image with white/black key image with note
                break;
        }
    }

    //keysInitialization: Called when app is first launched to set initial key properties
    public void keysInitialization() {
        for (int i = 0; i < keys.length; i++) {
            switch (i) {
                case 0:
                    keyID = "c1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 1:
                    keyID = "cs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 2:
                    keyID = "d1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 3:
                    keyID = "ds1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 4:
                    keyID = "e1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 5:
                    keyID = "f1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 6:
                    keyID = "fs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 7:
                    keyID = "g1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 8:
                    keyID = "gs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 9:
                    keyID = "a1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 10:
                    keyID = "as1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 11:
                    keyID = "b1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 12:
                    keyID = "c2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 13:
                    keyID = "cs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 14:
                    keyID = "d2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 15:
                    keyID = "ds2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 16:
                    keyID = "e2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 17:
                    keyID = "f2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 18:
                    keyID = "fs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 19:
                    keyID = "g2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 20:
                    keyID = "gs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 21:
                    keyID = "a2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 22:
                    keyID = "as2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                case 23:
                    keyID = "b2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
            }
        }
    }
}