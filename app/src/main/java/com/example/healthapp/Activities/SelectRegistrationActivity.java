package com.example.healthapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.healthapp.R;

public class SelectRegistrationActivity extends AppCompatActivity {

    private TextView back;
    private Button patientReg, doctorReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        patientReg = findViewById(R.id.patientReg);
        doctorReg = findViewById(R.id.doctorReg);

        patientReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, PatientRegistrationActivity.class);
                startActivity(intent);
            }
        });
        doctorReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, DoctorRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}