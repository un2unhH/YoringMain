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

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity6 extends AppCompatActivity {
    private TextView modelNameTextView, tv6_5, tv6_6;
    private Spinner colorSpinner, storageSpinner, monthSpinner;
    private Button backButton;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> colorAdapter, storageAdapter, monthAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        storageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        colorAdapter.add("선택하세요");
        storageAdapter.add("선택하세요");
        monthAdapter.add("선택하세요");
        monthAdapter.addAll("24", "30", "36");

        tv6_5 = findViewById(R.id.tv6_5);
        tv6_6 = findViewById(R.id.tv6_6);
        modelNameTextView = findViewById(R.id.modelNameTextView);
        colorSpinner = findViewById(R.id.colorSpinner);
        storageSpinner = findViewById(R.id.storageSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        backButton = findViewById(R.id.backButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String modelName = getIntent().getStringExtra("modelName");
        modelNameTextView.setText(modelName);

        setupDatabaseListeners();
        setupSpinners();
        setupAdapters();
        setupBackButton();
    }

    private void setupDatabaseListeners() {
        databaseReference.child("SmartPhone").orderByChild("modelName").equalTo(modelNameTextView.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String color = snapshot.child("color").getValue(String.class);
                                String storage = snapshot.child("storage").getValue(String.class);
                                addUniqueItemToAdapter(colorAdapter, color);
                                addUniqueItemToAdapter(storageAdapter, storage);
                            }
                            setSpinnerAdapter(colorSpinner, colorAdapter);
                            setSpinnerAdapter(storageSpinner, storageAdapter);
                            setSpinnerAdapter(monthSpinner, monthAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tv6_5.setText("데이터 로드 실패");
                    }
                });
    }

    private void setupSpinners() {
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isValidSelection()) {
                    updateModelPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        storageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isValidSelection()) {
                    updateModelPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    updateMonthlyPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean isValidSelection() {
        return !colorSpinner.getSelectedItem().toString().equals("선택하세요") &&
                !storageSpinner.getSelectedItem().toString().equals("선택하세요");
    }

    private boolean isValidFullSelection() {
        return isValidSelection() && !monthSpinner.getSelectedItem().toString().equals("선택하세요");
    }

    private void updateModelPrice() {
        String selectedColor = colorSpinner.getSelectedItem().toString();
        String selectedStorage = storageSpinner.getSelectedItem().toString();

        if (!isValidSelection()) {
            tv6_5.setText("모든 옵션을 선택해주세요.");
            return;
        }

        databaseReference.child("SmartPhone").orderByChild("color").equalTo(selectedColor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String storage = snapshot.child("storage").getValue(String.class);
                            if (storage != null && storage.equals(selectedStorage)) {
                                String modelPriceStr = snapshot.child("modelPrice").getValue(String.class);
                                if (modelPriceStr != null) {
                                    tv6_5.setText("기기 가격 : " + modelPriceStr + "원");
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tv6_5.setText("데이터 로드 실패");
                    }
                });
    }


    private void updateMonthlyPrice() {
        if (!isValidFullSelection()) {
            tv6_6.setText("모든 옵션을 선택해주세요.");
            return;
        }

        String selectedColor = colorSpinner.getSelectedItem().toString();
        String selectedStorage = storageSpinner.getSelectedItem().toString();
        int selectedMonths = Integer.parseInt(monthSpinner.getSelectedItem().toString());

        databaseReference.child("SmartPhone").orderByChild("color").equalTo(selectedColor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String storage = snapshot.child("storage").getValue(String.class);
                            if (storage != null && storage.equals(selectedStorage)) {
                                String modelPriceStr = snapshot.child("modelPrice").getValue(String.class);
                                if (modelPriceStr != null) {
                                    double modelPrice = Double.parseDouble(modelPriceStr.replaceAll("[^\\d.]", ""));
                                    int monthlyPrice = (int) Math.round(modelPrice / selectedMonths);
                                    tv6_6.setText("월 납부 금액 : " + monthlyPrice + "원");
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tv6_6.setText("데이터 로드 실패");
                    }
                });
    }


    private void addUniqueItemToAdapter(ArrayAdapter<String> adapter, String item) {
        if (item != null && adapter.getPosition(item) == -1) {
            adapter.add(item);
        }
    }

    private void setSpinnerAdapter(Spinner spinner, ArrayAdapter<String> adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupAdapters() {
        monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        monthAdapter.addAll("선택하세요", "24", "30", "36");
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

}
