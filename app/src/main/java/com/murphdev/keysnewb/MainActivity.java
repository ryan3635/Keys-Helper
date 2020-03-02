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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Context context = this;
    MediaPlayer mp;
    Spinner scaleSpinner, chordSpinner;
    RadioGroup rootNotes;
    RadioButton root;
    TextView infoScale, infoChord, infoScaleDisplay, infoChordDisplay;
    ImageButton keyButton;
    String scale;
    boolean hideSel, hideNote;
    Key[] keys = new Key[24];
    Key key;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rootNotes = findViewById(R.id.rootNotes);
        infoScale = findViewById(R.id.infoSelectedScale);
        infoScaleDisplay = findViewById(R.id.infoScaleNotes);
        infoChord = findViewById(R.id.infoSelectedChord);
        infoChordDisplay = findViewById(R.id.infoChordNotes);
        hideSel = false;
        hideNote = false;

        //Scale type and chord selection spinners initialization
        scaleSpinner = findViewById(R.id.scaleType);
        ArrayAdapter<String> scaleAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.scaleTypes));
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scaleSpinner.setAdapter(scaleAdapter);
        chordSpinner = findViewById(R.id.chord);
        ArrayAdapter<String> chordAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.initialChords));
        chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chordSpinner.setAdapter(chordAdapter);

        chordSpinner.setOnItemSelectedListener(this);
        scaleSpinner.setOnItemSelectedListener(this);

        /*
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
        //Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
    }

    //playNote: Called when a key is clicked -> Plays sound and shows feedback on click
    public void playNote(View v) {
        Resources res = context.getResources();
        int id = v.getId();
        String noteId = getResources().getResourceEntryName(id);
        int note = res.getIdentifier(noteId, "raw", context.getPackageName());

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
        if (noteId.equals("cs1") || noteId.equals("ds1") || noteId.equals("fs1") || noteId.equals("gs1") || noteId.equals("as1") ||
                noteId.equals("cs2") || noteId.equals("ds2") || noteId.equals("fs2") || noteId.equals("gs2") || noteId.equals("as2"))
            keyButton.setImageResource(R.drawable.blackkeyfeedback);
        else keyButton.setImageResource(R.drawable.whitekeyfeedback);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                paintKey(hideSel, hideNote);
            }
        }, 50);
    }


    //rootUpdate: Called when the root note is changed -> highlights keys and updates chord selection
    public void rootUpdate(View v) {
        keysInitialization();
        setScale();
        paintKey(hideSel, hideNote);
        //Updating chord selection options (call separate function -> updateChords)
    }


    //onItemSelected: Called when either a scale type or chord is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        keysInitialization();
        switch (parent.getId()) {
            case R.id.scaleType:
                scale = parent.getItemAtPosition(position).toString();
                setScale();
                paintKey(hideSel, hideNote);
                //Updating chord selection options (call separate function -> updateChords)
                break;

            case R.id.chord:
                //Updating chord for information section (bottom right)
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
    public void checkBoxes(View v) {
        boolean isChecked = ((CheckBox) v).isChecked();
        int checkboxId = v.getId();
        switch (checkboxId) {
            case R.id.hideSelection:
                hideSel = isChecked;
                break;
            case R.id.hideNotes:
                hideNote = isChecked;
                break;
        }
        paintKey(hideSel, hideNote);
    }


    //setKeys: Called when root note or scale spinner is updated -> handles which keys will be selected for the corresponding scale
    public void setScale() {
        //Updating scale name for information section (bottom right)
        int selectedRoot = rootNotes.getCheckedRadioButtonId();
        root = findViewById(selectedRoot);
        String rootInfoNote = (String) root.getText();
        String scaleText;
        if (scale.equals("(select)")) scaleText = scale;
        else scaleText = rootInfoNote + " " + scale;
        infoScale.setText(scaleText);

        //Selecting root note and keys after it that are in scale
        String rootNote = getResources().getResourceEntryName(selectedRoot);
        rootNote = rootNote + "1"; //to match resource id of a key
        int[] steps = scaleSteps(scale);
        int step;
        int i = 0;
        int j = 0;
        while (!((keys[i].keyId).equals(rootNote))) i++;
        int rootPos = i; //saving root position
        String infoScaleNote = "";
        ArrayList<String> scaleNotes = new ArrayList<String>();

        while (i < keys.length) {
            //for default (select) case
            if (steps.length == 0) break;

            infoScaleNote = keys[i].keyId.toUpperCase();
            infoScaleNote = infoScaleNote.substring(0, infoScaleNote.length() - 1);
            switch (infoScaleNote) {
                case "CS":
                    infoScaleNote = "C#";
                    break;
                case "DS":
                    infoScaleNote = "D#";
                    break;
                case "FS":
                    infoScaleNote = "F#";
                    break;
                case "GS":
                    infoScaleNote = "G#";
                    break;
                case "AS":
                    infoScaleNote = "A#";
                    break;
            }
            scaleNotes.add(infoScaleNote);

            //selection
            keys[i].setScale();
            step = steps[j];
            i = i + step;
            if (i < keys.length) keys[i].setScale();
            j++;
            if (j >= steps.length) j = 0; //reset to start of steps array to continue selecting keys in correct order
        }
        //Updating scale notes for info section
        ArrayList<String> scaleNotesCondensed = new ArrayList<>();
        scaleNotesCondensed.add(rootInfoNote);
        for (int k = 1; k < scaleNotes.size(); k++){
            scaleNotesCondensed.add(scaleNotes.get(k));
            if (scaleNotesCondensed.get(k).equals(rootInfoNote)){
                scaleNotesCondensed.remove(k);
                break;
            }
        }
        String scaleNotesFinal = "";
        for (int k = 0; k < scaleNotesCondensed.size(); k++){
            scaleNotesFinal = scaleNotesFinal + scaleNotesCondensed.get(k) + ", ";
        }
        scaleNotesFinal = scaleNotesFinal.substring(0, scaleNotesFinal.length() - 2);
        if (scale.equals("(select)")) scaleNotesFinal = "";
        infoScaleDisplay.setText(scaleNotesFinal);

        //Selecting keys before root note
        i = rootPos;
        j = steps.length - 1;
        while (!(i < 0)) {
            //for (select) case
            if (steps.length == 0) break;
            step = steps[j];
            i = i - step;
            if (i >= 0) keys[i].setScale();
            j--;
            if (j < 0) j = steps.length - 1; //reset to end of steps array to continue selecting keys backwards from root in correct order
        }
    }


    //paintKey: Called when either spinner or checkbox is updated -> updates key image accordingly for each key
    public void paintKey(boolean hideSel, boolean hideNote) {
        //Default case - Both checkboxes are unchecked
        if (!hideSel && !hideNote) {
            String keyID;
            for (Key i : keys) {
                keyID = i.idCheck();
                int key = getResources().getIdentifier(keyID, "id", context.getPackageName());
                keyButton = findViewById(key);
                keyID = keyID.substring(0, keyID.length() - 1);
                if (i.chordCheck()) {
                    switch (keyID) {
                        case "c":
                            keyButton.setImageResource(R.drawable.cchord);
                            break;
                        case "cs":
                            keyButton.setImageResource(R.drawable.cschord);
                            break;
                        case "d":
                            keyButton.setImageResource(R.drawable.dchord);
                            break;
                        case "ds":
                            keyButton.setImageResource(R.drawable.dschord);
                            break;
                        case "e":
                            keyButton.setImageResource(R.drawable.echord);
                            break;
                        case "f":
                            keyButton.setImageResource(R.drawable.fchord);
                            break;
                        case "fs":
                            keyButton.setImageResource(R.drawable.fschord);
                            break;
                        case "g":
                            keyButton.setImageResource(R.drawable.gchord);
                            break;
                        case "gs":
                            keyButton.setImageResource(R.drawable.gschord);
                            break;
                        case "a":
                            keyButton.setImageResource(R.drawable.achord);
                            break;
                        case "as":
                            keyButton.setImageResource(R.drawable.aschord);
                            break;
                        case "b":
                            keyButton.setImageResource(R.drawable.bchord);
                            break;
                    }
                } else if (i.scaleCheck()) {
                    switch (keyID) {
                        case "c":
                            keyButton.setImageResource(R.drawable.cselected);
                            break;
                        case "cs":
                            keyButton.setImageResource(R.drawable.csselected);
                            break;
                        case "d":
                            keyButton.setImageResource(R.drawable.dselected);
                            break;
                        case "ds":
                            keyButton.setImageResource(R.drawable.dsselected);
                            break;
                        case "e":
                            keyButton.setImageResource(R.drawable.eselected);
                            break;
                        case "f":
                            keyButton.setImageResource(R.drawable.fselected);
                            break;
                        case "fs":
                            keyButton.setImageResource(R.drawable.fsselected);
                            break;
                        case "g":
                            keyButton.setImageResource(R.drawable.gselected);
                            break;
                        case "gs":
                            keyButton.setImageResource(R.drawable.gsselected);
                            break;
                        case "a":
                            keyButton.setImageResource(R.drawable.aselected);
                            break;
                        case "as":
                            keyButton.setImageResource(R.drawable.asselected);
                            break;
                        case "b":
                            keyButton.setImageResource(R.drawable.bselected);
                            break;
                    }
                } else {
                    switch (keyID) {
                        case "c":
                            keyButton.setImageResource(R.drawable.ckey);
                            break;
                        case "cs":
                            keyButton.setImageResource(R.drawable.cskey);
                            break;
                        case "d":
                            keyButton.setImageResource(R.drawable.dkey);
                            break;
                        case "ds":
                            keyButton.setImageResource(R.drawable.dskey);
                            break;
                        case "e":
                            keyButton.setImageResource(R.drawable.ekey);
                            break;
                        case "f":
                            keyButton.setImageResource(R.drawable.fkey);
                            break;
                        case "fs":
                            keyButton.setImageResource(R.drawable.fskey);
                            break;
                        case "g":
                            keyButton.setImageResource(R.drawable.gkey);
                            break;
                        case "gs":
                            keyButton.setImageResource(R.drawable.gskey);
                            break;
                        case "a":
                            keyButton.setImageResource(R.drawable.akey);
                            break;
                        case "as":
                            keyButton.setImageResource(R.drawable.askey);
                            break;
                        case "b":
                            keyButton.setImageResource(R.drawable.bkey);
                            break;
                    }
                }
            }
        }
        //Only Hide Selection checked
        if (hideSel && !hideNote) {
            String keyID;
            for (Key i : keys) {
                keyID = i.idCheck();
                int key = getResources().getIdentifier(keyID, "id", context.getPackageName());
                keyButton = findViewById(key);
                keyID = keyID.substring(0, keyID.length() - 1);
                switch (keyID) {
                    case "c":
                        keyButton.setImageResource(R.drawable.ckey);
                        break;
                    case "cs":
                        keyButton.setImageResource(R.drawable.cskey);
                        break;
                    case "d":
                        keyButton.setImageResource(R.drawable.dkey);
                        break;
                    case "ds":
                        keyButton.setImageResource(R.drawable.dskey);
                        break;
                    case "e":
                        keyButton.setImageResource(R.drawable.ekey);
                        break;
                    case "f":
                        keyButton.setImageResource(R.drawable.fkey);
                        break;
                    case "fs":
                        keyButton.setImageResource(R.drawable.fskey);
                        break;
                    case "g":
                        keyButton.setImageResource(R.drawable.gkey);
                        break;
                    case "gs":
                        keyButton.setImageResource(R.drawable.gskey);
                        break;
                    case "a":
                        keyButton.setImageResource(R.drawable.akey);
                        break;
                    case "as":
                        keyButton.setImageResource(R.drawable.askey);
                        break;
                    case "b":
                        keyButton.setImageResource(R.drawable.bkey);
                        break;
                }
            }
        }
        //Only Hide Notes checked
        if (!hideSel && hideNote) {
            boolean isBlackKey;
            String keyID;
            for (Key i : keys) {
                keyID = i.idCheck();
                int key = getResources().getIdentifier(keyID, "id", context.getPackageName());
                keyButton = findViewById(key);
                isBlackKey = keyID.equals("cs1") || keyID.equals("ds1") || keyID.equals("fs1") || keyID.equals("gs1") || keyID.equals("as1") ||
                        keyID.equals("cs2") || keyID.equals("ds2") || keyID.equals("fs2") || keyID.equals("gs2") || keyID.equals("as2");

                if (i.chordCheck()) {
                    if (isBlackKey) keyButton.setImageResource(R.drawable.blackchord);
                    else keyButton.setImageResource(R.drawable.whitechord);
                } else if (i.scaleCheck()) {
                    if (isBlackKey) keyButton.setImageResource(R.drawable.blackselected);
                    else keyButton.setImageResource(R.drawable.whiteselected);
                } else if (isBlackKey) keyButton.setImageResource(R.drawable.blackkey);
                else keyButton.setImageResource(R.drawable.whitekey);
            }
        }
        //Both checkboxes are checked
        if (hideSel && hideNote) {
            String keyID;
            for (Key i : keys) {
                keyID = i.idCheck();
                int key = getResources().getIdentifier(keyID, "id", context.getPackageName());
                keyButton = findViewById(key);
                if (keyID.equals("cs1") || keyID.equals("ds1") || keyID.equals("fs1") || keyID.equals("gs1") || keyID.equals("as1") ||
                        keyID.equals("cs2") || keyID.equals("ds2") || keyID.equals("fs2") || keyID.equals("gs2") || keyID.equals("as2"))
                    keyButton.setImageResource(R.drawable.blackkey);
                else keyButton.setImageResource(R.drawable.whitekey);
            }
        }
    }


    //scaleSteps: Called when scale type spinner or root note is updated -> Determines what order of keys must be selected to match the type of scale selected
    public int[] scaleSteps(String scaleType) {
        int[] steps = {};
        switch (scaleType) {
            case "(select)":
                steps = new int[] {};
                break;
            case "Major":
                steps = new int[] {2, 2, 1, 2, 2, 2, 1};
                break;
            case "Minor":
                steps = new int[] {2, 1, 2, 2, 1, 2, 2};
                break;
            case "Blues":
                steps = new int[] {3, 2, 1, 1, 3, 2};
                break;
            case "Major Pentonic":
                steps = new int[] {2, 2, 3, 2, 3};
                break;
            case "Minor Pentonic":
                steps = new int[] {3, 2, 2, 3, 2};
                break;
            case "Augmented":
                steps = new int[] {3, 1, 3, 1, 3, 1};
                break;
            case "Diminished":
                steps = new int[] {2, 1, 2, 1, 2, 1, 2, 1};
                break;
            case "Dorian":
                steps = new int[] {2, 1, 2, 2, 2, 1, 2};
                break;
            case "Locrian":
                steps = new int[] {1, 2, 2, 1, 2, 2, 2};
                break;
            case "Lydian":
                steps = new int[] {2, 2, 2, 1, 2, 1, 2};
                break;
            case "Mixolydian":
                steps = new int[] {2, 2, 1, 2, 2, 1, 2};
                break;
            case "Phrygian":
                steps = new int[] {1, 2, 2, 2, 1, 2, 2};
                break;
        }
        return steps;
    }


    //chordSteps: Called when chord spinner is updated -> Acts similar to scaleSteps function but for chord types instead
    public int[] chordSteps(String chordType) {
        int[] steps = {};
        switch (chordType) {
            case "maj":
                steps = new int[] {4, 3};
                break;
            case "m":
                steps = new int[] {3, 4};
                break;
            case "7":
                steps = new int[] {4, 3, 3};
                break;
            case "min7":
                steps = new int[] {3, 4, 3};
                break;
            case "maj7":
                steps = new int[] {4, 3, 4};
                break;
            case "6":
                steps = new int[] {4, 3, 2};
                break;
            case "min6":
                steps = new int[] {3, 4, 2};
                break;
            case "9":
                steps = new int[] {4, 3, 3, 4};
                break;
            case "sus4":
                steps = new int[] {5, 2};
                break;
            case "7sus4":
                steps = new int[] {5, 2, 3};
                break;
            case "sus2":
                steps = new int[] {2, 5};
                break;
            case "dim":
                steps = new int[] {3, 3};
                break;
            case "aug":
                steps = new int[] {4, 4};
                break;
        }
        return steps;
    }


    //keysInitialization: Called when app is first launched to set initial key properties
    public void keysInitialization() {
        boolean isRoot = false;
        boolean isScale = false;
        boolean isChord = false;
        String keyID;
        for (int i = 0; i < keys.length; i++) {
                if (i == 0) {
                    keyID = "c1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 1) {
                    keyID = "cs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 2) {
                    keyID = "d1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 3) {
                    keyID = "ds1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 4) {
                    keyID = "e1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 5) {
                    keyID = "f1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 6) {
                    keyID = "fs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 7) {
                    keyID = "g1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 8) {
                    keyID = "gs1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 9) {
                    keyID = "a1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 10) {
                    keyID = "as1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 11) {
                    keyID = "b1";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 12) {
                    keyID = "c2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 13) {
                    keyID = "cs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 14) {
                    keyID = "d2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 15) {
                    keyID = "ds2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 16) {
                    keyID = "e2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 17) {
                    keyID = "f2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 18) {
                    keyID = "fs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 19) {
                    keyID = "g2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 20) {
                    keyID = "gs2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 21) {
                    keyID = "a2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 22) {
                    keyID = "as2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 23) {
                    keyID = "b2";
                    key = new Key(keyID, isRoot, isScale, isChord);
                    keys[i] = key;
                }
        }
    }

}