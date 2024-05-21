package com.example.yoringmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
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
    ImageButton imbRecommend, imbDetailRecommend, imbNews1, imbNews2, imbNews3, imbSktMembership, imbKtMembership, imbLgMembership;
    TextView tvSubYet, tvCurrentSub, tvCurrentSubName;
    DatabaseReference userSubscriptionsRef;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_all_menu:
                        startActivity(new Intent(MainActivity.this, AllMenu.class));
                        return true;
                    case R.id.nav_telsearch:
                        startActivity(new Intent(MainActivity.this, MainActivity5.class));
                        return true;
                    case R.id.nav_chain:
                        startActivity(new Intent(MainActivity.this, MainActivity3.class));
                        return true;
                    case R.id.nav_mypage:
                        startActivity(new Intent(MainActivity.this, MyPage.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        imbRecommend = findViewById(R.id.btn_recommend);
        imbDetailRecommend = findViewById(R.id.btn_detail_recommend);
        imbKtMembership = findViewById(R.id.imb_kt_membership);
        imbSktMembership = findViewById(R.id.imb_skt_membership);
        imbLgMembership = findViewById(R.id.imb_lg_membership);
        imbNews1 = findViewById(R.id.imb_news1);
        imbNews2 = findViewById(R.id.imb_news2);
        imbNews3 = findViewById(R.id.imb_news3);

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

        imbRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });


        imbDetailRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity7.class);
                startActivity(intent);
            }
        });

        imbSktMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.tworld.co.kr/membership/benefit/brand"));
                startActivity(browserIntent);
            }
        });

        imbKtMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.membership.kt.com/discount/partner/s_PartnerList.do"));
                startActivity(browserIntent);
            }
        });

        imbLgMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.lguplus.com/benefit-membership?urcMbspDivsCd=01&urcMbspBnftDivsCd=02"));
                startActivity(browserIntent);
            }
        });

        imbNews1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/028/0002685521?sid=101"));
                startActivity(browserIntent);
            }
        });

        imbNews2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/003/0012493379?sid=105"));
                startActivity(browserIntent);
            }
        });

        imbNews3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/015/0004965728?sid=105"));
                startActivity(browserIntent);
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