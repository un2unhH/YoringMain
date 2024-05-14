package com.example.yoringmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ImageButton btnRecommend, btnChangeModel, btnDetailRecommend, btnAllSubPlan;
    TextView tvSubYet, tvCurrentSub, tvCurrentSubName;
    DatabaseReference userSubscriptionsRef;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        return true;
                    case R.id.nav_membership:
                        startActivity(new Intent(MainActivity.this, MembershipMain.class));
                        return true;
                    case R.id.nav_mypage:
                        startActivity(new Intent(MainActivity.this, MyPage.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        btnRecommend = findViewById(R.id.btn_recommend);
        btnChangeModel = findViewById(R.id.btn_change_model);
        btnDetailRecommend = findViewById(R.id.btn_detail_recommend);
        btnAllSubPlan = findViewById(R.id.btn_all_sub_plan);

        tvSubYet = findViewById(R.id.tv_sub_yet);
        tvCurrentSub = findViewById(R.id.tv_current_sub);
        tvCurrentSubName = findViewById(R.id.tv_current_sub_name);

        Intent intent = getIntent();
        if (intent != null) {
            String selectedSubscription = intent.getStringExtra("selected_subscription");
            if (selectedSubscription != null) {
                tvSubYet.setVisibility(View.GONE);
                tvCurrentSub.setVisibility(View.VISIBLE);
                tvCurrentSubName.setVisibility(View.VISIBLE);
                tvCurrentSubName.setText(selectedSubscription);
            }
        }

        String userToken = getUserToken();
        if (userToken != null) {
            userSubscriptionsRef = FirebaseDatabase.getInstance().getReference("UserSubscriptions").child(userToken);
            userSubscriptionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String selectedSubscription = dataSnapshot.getValue(String.class);
                        if (selectedSubscription != null) {
                            tvSubYet.setVisibility(View.GONE);
                            tvCurrentSub.setVisibility(View.VISIBLE);
                            tvCurrentSubName.setVisibility(View.VISIBLE);
                            tvCurrentSubName.setText(selectedSubscription);
                        }
                    } else {
                        tvSubYet.setVisibility(View.VISIBLE);
                        tvCurrentSub.setVisibility(View.GONE);
                        tvCurrentSubName.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "사용자의 요금제를 읽어오는 데 실패했습니다: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("Firebase", "사용자의 토큰 값을 가져오는 데 실패했습니다.");
        }


        tvSubYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity4.class);
                startActivity(intent);
            }
        });

        tvCurrentSubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity4.class);
                startActivity(intent);
            }
        });

        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        btnChangeModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        btnDetailRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity7.class);
                startActivity(intent);
            }
        });

        btnAllSubPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity5.class);
                startActivity(intent);
            }
        });
    }

    private String getUserToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

}