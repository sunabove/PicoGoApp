package com.pico;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class DriveActivity extends ComActivity {

    private Spinner blueToothSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        this.blueToothSpinner = this.findViewById(R.id.blueToothSpinner);

        StringList list = new StringList();
        list.add( "Mercury" );
        list.add( "Venus" );
        list.add( "Mercury" );
        list.add( "Earth" );
        list.add( "Mars" );
        list.add( "Jupiter" );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        blueToothSpinner.setAdapter(adapter);

    }
}