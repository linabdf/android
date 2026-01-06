package com.example.nutriscore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileEditActivity extends AppCompatActivity {

    RadioButton rdbtnHomme, rdbtnFemme;
    TextInputLayout txtinputAge, txtinputTaille, txtinputPoids;
    Spinner spinnerActPhys;
    Button btnValider;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
        rdbtnHomme = findViewById(R.id.rdbtnHomme);
        rdbtnFemme = findViewById(R.id.rdbtnFemme);
        txtinputAge = findViewById(R.id.txtinputAge);
        txtinputTaille = findViewById(R.id.txtinputTaille);
        txtinputPoids = findViewById(R.id.txtinputPoids);
        spinnerActPhys = findViewById(R.id.spinActPhys);
        btnValider = findViewById(R.id.btnValider);

        String sexe = pref.getString("sexe", "N/A");
        String age = pref.getString("age", "N/A");
        String taille = pref.getString("taille", "N/A");
        String poids = pref.getString("poids", "N/A");
        String actPhys = pref.getString("actPhys", "N/A");

        if(!sexe.equals("N/A")){
            if(sexe.equals("Homme")) {
                rdbtnHomme.setChecked(true);
                rdbtnFemme.setChecked(false);
            } else {
                rdbtnHomme.setChecked(false);
                rdbtnFemme.setChecked(true);
            }
        }

        if(!age.equals("N/A")){
            ((TextInputEditText) txtinputAge.getEditText()).setText(age);
        }

        if(!taille.equals("N/A")) {
            ((TextInputEditText) txtinputTaille.getEditText()).setText(taille);
        }

        if(!poids.equals("N/A")) {
            ((TextInputEditText) txtinputPoids.getEditText()).setText(poids);
        }

        if(!actPhys.equals("N/A")) {
            SpinnerAdapter adapter = spinnerActPhys.getAdapter();
            int position = -1; // Default to an invalid position

            // A simple loop is often more reliable and clear than getPosition(),
            // especially if the adapter contains complex objects.
            for (int i = 0; i < adapter.getCount(); i++) {
                // Assuming the adapter is for CharSequence or String. Adjust if it's a custom object.
                if (actPhys.equals(adapter.getItem(i).toString())) {
                    position = i;
                    break; // Found it, no need to keep searching
                }
            }
            // Only set the selection if the item was actually found in the adapter.
            if (position != -1) {
                spinnerActPhys.setSelection(position);
            } else {
                spinnerActPhys.setSelection(0); // Reset to a default position (e.g., the first item)
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        rdbtnHomme.setOnClickListener(v -> {
            if(rdbtnHomme.isChecked()){
                rdbtnHomme.setChecked(true);
                rdbtnFemme.setChecked(false);
            }
            if(rdbtnFemme.isChecked()){
                rdbtnHomme.setChecked(false);
                rdbtnFemme.setChecked(true);
            }
        });

        rdbtnFemme.setOnClickListener(v -> {
            if(rdbtnFemme.isChecked()){
                rdbtnHomme.setChecked(true);
                rdbtnFemme.setChecked(false);
            }
            if(rdbtnHomme.isChecked()){
                rdbtnHomme.setChecked(false);
                rdbtnFemme.setChecked(true);
            }
        });

        btnValider.setOnClickListener(v -> {
            String sexe = "N/A";
            if (rdbtnHomme.isChecked()) {
                sexe = "Homme";
            }
            if (rdbtnFemme.isChecked()) {
                sexe = "Femme";
            }
            String age = ((TextInputEditText) txtinputAge.getEditText()).getText().toString();
            String taille = ((TextInputEditText) txtinputTaille.getEditText()).getText().toString();
            String poids = ((TextInputEditText) txtinputPoids.getEditText()).getText().toString();
            String actPhys = spinnerActPhys.getSelectedItem().toString();

            if (sexe.equals("N/A")) {
                Toast.makeText(this, "Veuillez sélectionner un sexe", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty((age))) {
                Toast.makeText(this, "Veuillez entrer votre âge", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(taille)) {
                Toast.makeText(this, "Veuillez entrer votre taille", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(poids)) {
                Toast.makeText(this, "Veuillez entrer votre poids", Toast.LENGTH_SHORT).show();
            } else if (actPhys.equals("Choississez votre niveau d'activité…")) {
                Toast.makeText(this, "Veuillez sélectionner un niveau d'activité", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences pref = getPreferences(Activity.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("sexe", sexe);
                ed.putString("age", age);
                ed.putString("taille", taille);
                ed.putString("poids", poids);
                ed.putString("actPhys", actPhys);
                ed.apply();
                Toast.makeText(this, "Profil sauvegardé", Toast.LENGTH_SHORT).show();

            }
        });
    }

}