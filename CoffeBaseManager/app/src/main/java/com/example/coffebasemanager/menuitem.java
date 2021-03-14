package com.example.coffebasemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class menuitem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuitem);
        NumberPicker calories=  findViewById(R.id.calories);
        calories.setMaxValue(0);
        calories.setMinValue(1000);
        calories.setWrapSelectorWheel(true);
        Spinner categoryspinner = (Spinner) findViewById(R.id.categoryspinner);
        String[] arraySpinner = new String[] {
                "Cold Drink","Hot Drink","Main Course","Deserts"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryspinner.setAdapter(adapter);
    }

}

