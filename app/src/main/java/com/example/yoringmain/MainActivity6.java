package com.example.yoringmain;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;

public class MainActivity6 extends AppCompatActivity {
    private TextView modelNameTextView, tv6_5;
    private Spinner colorSpinner;
    private Spinner storageSpinner;
    private Button backButton;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> colorAdapter;
    private ArrayAdapter<String> storageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        colorAdapter = new ArrayAdapter<>(MainActivity6.this, android.R.layout.simple_spinner_item);
        storageAdapter = new ArrayAdapter<>(MainActivity6.this, android.R.layout.simple_spinner_item);

        tv6_5 = findViewById(R.id.tv6_5);
        modelNameTextView = findViewById(R.id.modelNameTextView);
        colorSpinner = findViewById(R.id.colorSpinner);
        storageSpinner = findViewById(R.id.storageSpinner);
        backButton = findViewById(R.id.backButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String modelName = getIntent().getStringExtra("modelName");
        modelNameTextView.setText(modelName);

        databaseReference.child("SmartPhone").orderByChild("modelName").equalTo(modelName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String color = snapshot.child("color").getValue(String.class);
                                String storageString = snapshot.child("storage").getValue(String.class);
                                String storageLabel;
                                if (storageString.equals("1 TB")) {
                                    storageLabel = "1024";
                                } else {
                                    storageLabel = storageString.replaceAll("[^\\d]", "");
                                }

                                if (colorAdapter.getPosition(color) == -1) {
                                    colorAdapter.add(color);
                                }

                                if (storageAdapter.getPosition(storageLabel) == -1) {
                                    storageAdapter.add(storageLabel);
                                }
                            }

                            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            colorSpinner.setAdapter(colorAdapter);

                            storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            storageSpinner.setAdapter(storageAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                    }
                });

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedColor = parent.getItemAtPosition(position).toString();
                updateModelPrice(selectedColor, storageSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });

        storageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStorage = parent.getItemAtPosition(position).toString();
                updateModelPrice(colorSpinner.getSelectedItem().toString(), selectedStorage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateModelPrice(String selectedColor, String selectedStorage) {
        databaseReference.child("SmartPhone").orderByChild("color").equalTo(selectedColor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String storage = snapshot.child("storage").getValue(String.class);
                                String modelPrice = snapshot.child("modelPrice").getValue(String.class);
                                if (storage.equals(selectedStorage)) {
                                    String priceText = "기기 가격 : " + modelPrice + "원";
                                    tv6_5.setText(priceText);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리
                    }
                });
    }
}
