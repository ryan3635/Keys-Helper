package com.murphdev.keysnewb;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Context context = this;
    MediaPlayer mp;
    ImageButton keyButton;
    TextView infoScale, infoChord, infoScaleDisplay, infoChordDisplay, enterText, quizRequest, userInputArea, inputText, quizRules, timeText, timeLeft, scoreText, currentScore;
    Button quizButton, recordsButton;
    RadioGroup rootNotes;
    RadioButton root;
    Spinner scaleSpinner, chordSpinner;
    CheckBox hideSelection;
    Key[] keys = new Key[24];
    Key key;
    boolean hideSel, hideNote, quizRunning;
    int score = 0, userInputPos = 0, maxInput = 5, quizType = 3, quizDifficulty = 3;
    String scale, chord;
    String [] rootNotesQuiz = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    ArrayList<String> userInput = new ArrayList<>();
    ArrayList<String> easyScales = new ArrayList<>();
    ArrayList<String> medScales = new ArrayList<>();
    ArrayList<String> hardScales = new ArrayList<>();
    ArrayList<String> easyChords = new ArrayList<>();
    ArrayList<String> medChords = new ArrayList<>();
    ArrayList<String> hardChords = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Initializing UI elements
        rootNotes = findViewById(R.id.rootNotes);
        infoScale = findViewById(R.id.infoSelectedScale);
        infoScaleDisplay = findViewById(R.id.infoScaleNotes);
        infoChord = findViewById(R.id.infoSelectedChord);
        infoChordDisplay = findViewById(R.id.infoChordNotes);
        hideSelection = findViewById(R.id.hideSelection);
        quizButton = findViewById(R.id.quizButton);
        recordsButton = findViewById(R.id.recordsButton);
        //These become visible once quiz is running - become invisible when quiz is ended
        enterText = findViewById(R.id.enterText);
        quizRequest = findViewById(R.id.quizRequest);
        userInputArea = findViewById(R.id.userInput);
        inputText = findViewById(R.id.inputText);
        quizRules = findViewById(R.id.quizRules);
        timeText = findViewById(R.id.timeText);
        timeLeft = findViewById(R.id.timeLeft);
        scoreText = findViewById(R.id.scoreText);
        currentScore = findViewById(R.id.currentScore);

        hideSel = false;
        hideNote = false;
        chord = "(select)";
        quizRunning = false;
        difficultyInitialization();

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

        quizButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (quizRunning) {
                    quizRunning = false;
                    score = 0;
                    quizButton.setBackgroundColor(0xFF345126);
                    quizButton.setText(R.string.quizButton);
                    userInputPos = 0;
                    userInput.clear();
                    userInputArea.setText(R.string.noSelection);
                    hideSelection.setChecked(false);
                    hideSel = false;
                    paintKey(hideSel, hideNote);
                    //hiding text on screen
                    enterText.setVisibility(View.INVISIBLE);
                    quizRequest.setVisibility(View.INVISIBLE);
                    inputText.setVisibility(View.INVISIBLE);
                    quizRules.setVisibility(View.INVISIBLE);
                    timeText.setVisibility(View.INVISIBLE);
                    timeLeft.setVisibility(View.INVISIBLE);
                    scoreText.setVisibility(View.INVISIBLE);
                    currentScore.setVisibility(View.INVISIBLE);
                }
                else {
                    quizRunning = true;
                    score = 0;
                    quizButton.setBackgroundColor(0xFFEA0E0E);
                    quizButton.setText(R.string.endQuizButton);
                    hideSelection.setChecked(true);
                    hideSel = true;
                    paintKey(hideSel, hideNote);
                    //showing text on screen
                    enterText.setVisibility(View.VISIBLE);
                    quizRequest.setVisibility(View.VISIBLE);
                    inputText.setVisibility(View.VISIBLE);
                    quizRules.setVisibility(View.VISIBLE);
                    timeText.setVisibility(View.VISIBLE);
                    timeLeft.setVisibility(View.VISIBLE);
                    scoreText.setVisibility(View.VISIBLE);
                    currentScore.setVisibility(View.VISIBLE);
                    //quizGame(); -> runs randomizer, maxInput = size of randomized string -> userInput = new String [maxInput] -> ends after 2 mins
                    //quizRunning = false;
                    //hide elements again, as quiz is now stopped
                    //hideSelection.setChecked(false);
                    //hideSel = false;
                    //paintKey(hideSel, hideNote);
                    //show score as alert?
                    //save score onto phone if it's score is greater than top 10 previous records (separate records for each quiz type?)
                    //add score to records page (when implemented)
                }
            }
        });


        recordsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //temporarily using this button to test randomizer function
                String request = randomizer(quizType, quizDifficulty);
                quizRequest.setText(request);
            }
        });


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
        if ((noteId.charAt(1)) == 's') keyButton.setImageResource(R.drawable.blackkeyfeedback);
        else keyButton.setImageResource(R.drawable.whitekeyfeedback);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                paintKey(hideSel, hideNote);
            }
        }, 50);

        //For quiz - if quiz running then add note to user input area
        if (quizRunning) {
            if (userInputPos < maxInput) {
                userInput.add(noteId);
                userInputPos++;
            }
            String userInputDisplay = "";
            String temp;
            for (int i = 0; i < userInput.size(); i++) {
                temp = userInput.get(i);
                if (temp.charAt(1) == 's') temp = temp.substring(0, 1) + "#";
                else temp = temp.substring(0, 1);
                temp = temp.toUpperCase();
                userInputDisplay = userInputDisplay + temp + ", ";
            }
            userInputArea.setText(userInputDisplay);
            if (userInputPos == maxInput) {
                userInputDisplay = userInputDisplay.substring(0, userInputDisplay.length() - 2);
                userInputArea.setText(userInputDisplay);
                userInputPos = 0;
                userInput.clear();
                Timer t2 = new Timer();
                t2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userInputArea.setText(R.string.noSelection);
                                //show 'wrong' X mark
                            }
                        });
                    }
                }, 75);
            }
        }
    }


    //rootUpdate: Called when the root note is changed -> Highlights keys and updates chord selection
    public void rootUpdate(View v) {
        keysInitialization();
        setScale();
        updateChords();
        setChord();
        paintKey(hideSel, hideNote);
    }


    //onItemSelected: Called when either a scale type or chord is selected -> Handles spinners functionality
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        keysInitialization();
        switch (parent.getId()) {
            case R.id.scaleType:
                scale = parent.getItemAtPosition(position).toString();
                if (scale.equals("(select)")) {
                    String [] defaultChord = {"(select)"};
                    ArrayAdapter<String> chordAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, defaultChord);
                    chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chordSpinner.setAdapter(chordAdapter);
                }
                setScale();
                updateChords();
                setChord();
                paintKey(hideSel, hideNote);
                break;

            case R.id.chord:
                //Updating chord for info section (bottom right)
                chord = parent.getItemAtPosition(position).toString();
                infoChord.setText(chord);
                setScale();
                setChord();
                paintKey(hideSel, hideNote);
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


    //setScale: Called when root note or scale spinner is updated -> handles which keys will be selected for the corresponding scale
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
        int i = 0, j = 0;
        while (!((keys[i].keyId).equals(rootNote))) i++;
        int rootPos = i; //saving root position
        String infoScaleNote;
        ArrayList<String> scaleNotes = new ArrayList<>();

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


    //setChord: Called when a chord is selected from chord spinner -> handles which keys will be selected for the corresponding chord
    public void setChord() {
        String chordStart, chordType;
        int i = 0;
        int [] steps;
        ArrayList<String> chordNotes = new ArrayList<>();
        if (chord.equals("(select)")) {
            while (i < keys.length) {
                keys[i].clearChord();
              i++;
            }
            infoChordDisplay.setText(R.string.noChordNotes);
        }
        //selection
        else {
            chordStart = chord.substring(0, 1);
            if ((chord.charAt(1)) == '#') {
                chordStart = chordStart + "s";
                chordType = chord.substring(2);
            }
            else chordType = chord.substring(1);
            steps = chordSteps(chordType);
            chordStart = chordStart.toLowerCase();
            chordStart = chordStart + "1"; //to match resource id of key
            i = 0;
            while (!((keys[i].keyId).equals(chordStart))) i++;
            for (int step : steps) {
                keys[i].setChord();
                chordNotes.add(keys[i].keyId);
                i = i + step;
            }
            keys[i].setChord();
            chordNotes.add(keys[i].keyId);

            //updating chord notes for info section
            String note;
            String chordNotesFinal = "";
            for (int j = 0; j < chordNotes.size(); j++) {
                note = chordNotes.get(j);
                if (note.charAt(1) == 's') note = note.substring(0, 1) + "#";
                else note = note.substring(0, 1);
                note = note.toUpperCase();
                if (j == 0) chordNotesFinal = note;
                else chordNotesFinal = chordNotesFinal + ", " + note;
            }
            infoChordDisplay.setText(chordNotesFinal);
        }
    }


    //updateChords: Called when scale spinner or root note is updated -> determines what chords are loaded into chord spinner
    public void updateChords() {
        ArrayList<String> chords = new ArrayList<>();
        String chordStart;
        String [] chordTypes = {"maj", "m", "7", "min7", "maj7", "6", "min6", "sus4", "7sus4", "sus2", "dim", "aug"};
        int [] chordSteps;
        chords.add("(select)");
        //Checking all chord types for each of the 12 notes, starting from C and ending on B
        for (int i = 0; i < 12; i++) {
            chordStart = keys[i].keyId;
            if ((chordStart.charAt(1)) == 's') {
                chordStart = chordStart.substring(0, 1);
                chordStart = chordStart + "#";
            }
            else chordStart = chordStart.substring(0, 1);
            chordStart = chordStart.toUpperCase();
            int j = 0;
            while (j < chordTypes.length) {
                chordSteps = chordSteps(chordTypes[j]);
                boolean isChord = true;
                int step = i;
                //If all notes within chord are in scale, the chord is added to chord spinner
                for (int chordStep : chordSteps) {
                    if (!keys[step].scaleCheck()) {
                        isChord = false;
                        break;
                    }
                    step = step + chordStep;
                }
                    if (!keys[step].scaleCheck()) isChord = false;
                if (isChord) chords.add(chordStart + chordTypes[j]);
                j++;
            }
        }
        //sorting chords by chord type
        ArrayList<String> sortedChords = new ArrayList<>();
        ArrayList<String> majChords = new ArrayList<>();
        ArrayList<String> mChords = new ArrayList<>();
        ArrayList<String> sevenChords = new ArrayList<>();
        ArrayList<String> min7Chords = new ArrayList<>();
        ArrayList<String> maj7Chords = new ArrayList<>();
        ArrayList<String> sixChords = new ArrayList<>();
        ArrayList<String> min6Chords = new ArrayList<>();
        ArrayList<String> sus4Chords = new ArrayList<>();
        ArrayList<String> sevenSus4Chords = new ArrayList<>();
        ArrayList<String> sus2Chords = new ArrayList<>();
        ArrayList<String> dimChords = new ArrayList<>();
        ArrayList<String> augChords = new ArrayList<>();
        String chordType;
        for (int i = 0; i < chords.size(); i++) {
            chordType = chords.get(i);
            if (chordType.charAt(1) == '#') chordType = chordType.substring(2);
            else chordType = chordType.substring(1);
            switch (chordType) {
                case "maj": {
                   majChords.add(chords.get(i));
                   break;
                }
                case "m": {
                    mChords.add(chords.get(i));
                    break;
                }
                case "7": {
                    sevenChords.add(chords.get(i));
                    break;
                }
                case "min7": {
                    min7Chords.add(chords.get(i));
                    break;
                }
                case "maj7": {
                    maj7Chords.add(chords.get(i));
                    break;
                }
                case "6": {
                    sixChords.add(chords.get(i));
                    break;
                }
                case "min6": {
                    min6Chords.add(chords.get(i));
                    break;
                }
                case "sus4": {
                    sus4Chords.add(chords.get(i));
                    break;
                }
                case "7sus4": {
                    sevenSus4Chords.add(chords.get(i));
                    break;
                }
                case "sus2": {
                    sus2Chords.add(chords.get(i));
                    break;
                }
                case "dim": {
                    dimChords.add(chords.get(i));
                    break;
                }
                case "aug": {
                    augChords.add(chords.get(i));
                    break;
                }

            }
        }
        sortedChords.add("(select)");
        sortedChords.addAll(majChords);
        sortedChords.addAll(mChords);
        sortedChords.addAll(sevenChords);
        sortedChords.addAll(min7Chords);
        sortedChords.addAll(maj7Chords);
        sortedChords.addAll(sixChords);
        sortedChords.addAll(min6Chords);
        sortedChords.addAll(sus4Chords);
        sortedChords.addAll(sevenSus4Chords);
        sortedChords.addAll(sus2Chords);
        sortedChords.addAll(dimChords);
        sortedChords.addAll(augChords);
        ArrayAdapter<String> chordAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, sortedChords);
        chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chordSpinner.setAdapter(chordAdapter);
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
            case "Major Pentonic":
                steps = new int[] {2, 2, 3, 2, 3};
                break;
            case "Minor Pentonic":
                steps = new int[] {3, 2, 2, 3, 2};
                break;
            case "Blues":
                steps = new int[] {3, 2, 1, 1, 3, 2};
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
            case "(select)":
                steps = new int[] {};
                break;
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
        boolean isScale = false;
        boolean isChord = false;
        String keyID;
        for (int i = 0; i < keys.length; i++) {
                if (i == 0) {
                    keyID = "c1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 1) {
                    keyID = "cs1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 2) {
                    keyID = "d1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 3) {
                    keyID = "ds1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 4) {
                    keyID = "e1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 5) {
                    keyID = "f1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 6) {
                    keyID = "fs1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 7) {
                    keyID = "g1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 8) {
                    keyID = "gs1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 9) {
                    keyID = "a1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 10) {
                    keyID = "as1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 11) {
                    keyID = "b1";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 12) {
                    keyID = "c2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 13) {
                    keyID = "cs2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 14) {
                    keyID = "d2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 15) {
                    keyID = "ds2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 16) {
                    keyID = "e2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 17) {
                    keyID = "f2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 18) {
                    keyID = "fs2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 19) {
                    keyID = "g2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 20) {
                    keyID = "gs2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 21) {
                    keyID = "a2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 22) {
                    keyID = "as2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
                else if (i == 23) {
                    keyID = "b2";
                    key = new Key(keyID, isScale, isChord);
                    keys[i] = key;
                }
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
                if ((keyID.charAt(1)) == 's') keyButton.setImageResource(R.drawable.blackkey);
                else keyButton.setImageResource(R.drawable.whitekey);
            }
        }
    }


    //difficultyInitialization: Called when app is first launched to associate scales and chords with their set difficulties for quiz
    public void difficultyInitialization() {
        easyScales.add("Major Scale");
        easyScales.add("Minor Scale");
        easyScales.add("Major Pentonic Scale");
        easyScales.add("Minor Pentonic Scale");
        medScales.add("Blues Scale");
        medScales.add("Augmented Scale");
        medScales.add("Diminished Scale");
        hardScales.add("Dorian Scale");
        hardScales.add("Locrian Scale");
        hardScales.add("Lydian Scale");
        hardScales.add("Mixolydian Scale");
        hardScales.add("Phrygian Scale");

        easyChords.add("maj Chord");
        easyChords.add("m Chord");
        easyChords.add("7 Chord");
        medChords.add("min7 Chord");
        medChords.add("maj7 Chord");
        medChords.add("6 Chord");
        medChords.add("min6 Chord");
        hardChords.add("sus4 Chord");
        hardChords.add("7sus4 Chord");
        hardChords.add("sus2 Chord");
        hardChords.add("dim Chord");
        hardChords.add("aug Chord");
    }


    //randomizer: Called when quiz game is running -> chooses a random scale or chord for any one root note
    String randomizer (int type, int difficulty) {
        Random randomizer = new Random();
        int rIndex = randomizer.nextInt(rootNotesQuiz.length);
        int scalesCount;
        boolean isChord = false;
        String randomRoot = rootNotesQuiz[rIndex];
        String request = "";
        ArrayList<String> possibleRequests = new ArrayList<>();

        //Type 1 -> Only scales ; Type 2 -> Only chords ; Type 3 -> Scales and chords
        if (type == 1) {
            //Difficulty 1 -> Easy ; Difficulty 2 -> Medium ; Difficulty 3 -> Hard
            switch (difficulty) {
                case 1:
                    rIndex = randomizer.nextInt(easyScales.size());
                    request = easyScales.get(rIndex);
                    break;
                case 2:
                    possibleRequests.addAll(easyScales);
                    possibleRequests.addAll(medScales);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    break;
                case 3:
                    possibleRequests.addAll(easyScales);
                    possibleRequests.addAll(medScales);
                    possibleRequests.addAll(hardScales);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    break;
            }
        }
        else if (type == 2) {
            isChord = true;
            switch (difficulty) {
                case 1:
                    rIndex = randomizer.nextInt(easyChords.size());
                    request = easyChords.get(rIndex);
                    break;
                case 2:
                    possibleRequests.addAll(easyChords);
                    possibleRequests.addAll(medChords);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    break;
                case 3:
                    possibleRequests.addAll(easyChords);
                    possibleRequests.addAll(medChords);
                    possibleRequests.addAll(hardChords);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    break;
            }
        }
        else if (type == 3) {
            switch (difficulty) {
                case 1:
                    possibleRequests.addAll(easyScales);
                    scalesCount = possibleRequests.size();
                    possibleRequests.addAll(easyChords);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    if (rIndex >= scalesCount) isChord = true;
                    break;
                case 2:
                    possibleRequests.addAll(easyScales);
                    possibleRequests.addAll(medScales);
                    scalesCount = possibleRequests.size();
                    possibleRequests.addAll(easyChords);
                    possibleRequests.addAll(medChords);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    if (rIndex >= scalesCount) isChord = true;
                    break;
                case 3:
                    possibleRequests.addAll(easyScales);
                    possibleRequests.addAll(medScales);
                    possibleRequests.addAll(hardScales);
                    scalesCount = possibleRequests.size();
                    possibleRequests.addAll(easyChords);
                    possibleRequests.addAll(medChords);
                    possibleRequests.addAll(hardChords);
                    rIndex = randomizer.nextInt(possibleRequests.size());
                    request = possibleRequests.get(rIndex);
                    if (rIndex >= scalesCount) isChord = true;
                    break;
            }
        }
        possibleRequests.clear();
        if (isChord) return randomRoot + request;
        else return randomRoot + " " + request;
    }
}