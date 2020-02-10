package com.murphdev.keysnewb;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Context context = this;
    MediaPlayer mp;
    Spinner scaleSpinner;
    Spinner chordSpinner;
    RadioGroup rootNotes;
    RadioButton root;
    TextView infoScale;
    TextView infoChord;
    ImageButton key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rootNotes = findViewById(R.id.rootNotes);
        infoScale = findViewById(R.id.infoSelectedScale);
        infoChord = findViewById(R.id.infoSelectedChord);

        //Toast.makeText(this, noteId, Toast.LENGTH_SHORT).show();

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

    }


    //playNote: Called when a key is clicked
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
        key = findViewById(id);
        final int imageId = res.getIdentifier(noteId, "drawable", context.getPackageName());
        if (noteId.equals("cs1") || noteId.equals("ds1") || noteId.equals("fs1") || noteId.equals("gs1") || noteId.equals("as1") ||
                noteId.equals("cs2") || noteId.equals("ds2") || noteId.equals("fs2") || noteId.equals("gs2") || noteId.equals("as2"))
            key.setImageResource(R.drawable.blackkeyfeedback);
        else key.setImageResource(R.drawable.whitekeyfeedback);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                key.setImageResource(imageId);
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
        //not used
    }
}