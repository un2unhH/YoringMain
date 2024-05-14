package com.example.yoringmain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity8 extends AppCompatActivity {

    ListView lv_sub;


    private ArrayAdapter<String> adapter;

    private List<String> feePlansList = new ArrayList<>();
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);

        Button BtnReFind;
        BtnReFind = findViewById(R.id.BtnReFind);
        BtnReFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity8.this, MainActivity7.class);
                intent.putExtra("selected_data", "");
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        int dataUsage = intent.getIntExtra("dataUsage", 0);
        int callUsage = intent.getIntExtra("callUsage", 0);
        int messageUsage = intent.getIntExtra("messageUsage", 0);

        lv_sub = findViewById(R.id.lv_sub);

        adapter = new ArrayAdapter<>(MainActivity8.this, android.R.layout.simple_list_item_1, feePlansList);
        lv_sub.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("fee_plans");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feePlansList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String planInfo = snapshot.child("sub_name").getValue(String.class) +
                            "       통신사: " + snapshot.child("telecom_name").getValue(String.class) +
                            "\n가격: " + snapshot.child("price").getValue(String.class) +
                            "                           통신망: " + snapshot.child("usage_network").getValue(String.class);

                    if (satisfiesRequirements(snapshot, dataUsage, callUsage, messageUsage)) {
                        feePlansList.add(planInfo);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
                Log.w("MainActivity8", "loadFeePlans:onCancelled", databaseError.toException());
            }
        });
    }

    private boolean satisfiesRequirements(DataSnapshot snapshot, int dataUsage, int callUsage, int messageUsage) {
        String dataStr = snapshot.child("data").getValue(String.class);
        double data;

        if ("무제한".equals(dataStr)) {
            data = Double.POSITIVE_INFINITY;
        } else {
            dataStr = dataStr.replaceAll("[^\\d.]", "");
            data = dataStr.isEmpty() ? 0.0 : Double.parseDouble(dataStr);
        }

        int callMinutes = snapshot.child("call_minutes").getValue(Integer.class);
        int messageCount = snapshot.child("message_count").getValue(Integer.class);

        return (data <= dataUsage) && (callMinutes <= callUsage) && (messageCount <= messageUsage);
    }
}
