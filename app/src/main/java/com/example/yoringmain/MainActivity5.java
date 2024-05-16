package com.example.yoringmain;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {
    private ListView lvAllSub;
    private DatabaseReference databaseReference;
    private List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("전체 요금제 조회");

        RadioButton rbPrice = findViewById(R.id.rb_price);
        rbPrice.setChecked(true);

        lvAllSub = findViewById(R.id.lv_all_sub);
        databaseReference = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");

        Button btnFind = findViewById(R.id.btn_find);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSubscriptionPlans();
            }
        });

    }

    private void fetchSubscriptionPlans() {
        Query query = databaseReference.orderByChild("price");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subscriptionPlans.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null) {
                        subscriptionPlans.add(plan);
                    }
                }
                Collections.sort(subscriptionPlans);
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    private void updateListView() {
        MainActivity5.CustomAdapter adapter = new MainActivity5.CustomAdapter(this, R.layout.list_item, subscriptionPlans);
        lvAllSub.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<MainActivity5.SubscriptionPlan> {
        private DatabaseReference favRef;
        private int favoriteCount = 0;
        private HashMap<String, Boolean> favoritesMap = new HashMap<>();

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<MainActivity5.SubscriptionPlan> objects) {
            super(context, resource, objects);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                favRef = FirebaseDatabase.getInstance().getReference("UserFavorites").child(user.getUid());
                favRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        favoriteCount = (int) dataSnapshot.getChildrenCount();
                        favoritesMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String subName = snapshot.getKey();
                            String decodedSubName = decodeFromFirebaseKey(subName);
                            favoritesMap.put(decodedSubName, true);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching favorite count");
                    }
                });
            }
        }

        private String decodeFromFirebaseKey(String encodedKey) {
            return encodedKey.replace(',', '.').replace('-', '#').replace('+', '$').replace('(', '[').replace(')', ']');
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            SubscriptionPlan plan = getItem(position);
            TextView tvPlanName = convertView.findViewById(R.id.tvPlanName);
            TextView tvTelecomName = convertView.findViewById(R.id.tvTelecomName);
            TextView tvPrice = convertView.findViewById(R.id.tvPrice);
            TextView tvSpeedLimit = convertView.findViewById(R.id.tvSpeedLimit);
            ImageButton imbEmptyHeart = convertView.findViewById(R.id.imb_empty_heart);

            tvPlanName.setText(plan.getSub_name());
            tvTelecomName.setText(plan.getTelecomName());
            tvPrice.setText(String.format("가격: %s", plan.getPrice()));
            tvSpeedLimit.setText(String.format("통신망: %s", plan.getUsageNetwork()));
            tvSpeedLimit.setText(String.format("%s", plan.getSpeedLimit()));

            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nanumsbold_e);
            tvPlanName.setTypeface(typeface);
            tvTelecomName.setTypeface(typeface);
            tvPrice.setTypeface(typeface);
            tvSpeedLimit.setTypeface(typeface);

            String encodedSubName = encodeForFirebaseKey(plan.getSub_name());
            if (favoritesMap.containsKey(encodedSubName)) {
                imbEmptyHeart.setImageResource(R.drawable.full_heart);
                imbEmptyHeart.setTag("full");
            } else {
                imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                imbEmptyHeart.setTag("empty");
            }

            imbEmptyHeart.setOnClickListener(v -> {
                boolean isFavorited = imbEmptyHeart.getTag() != null && imbEmptyHeart.getTag().equals("full");

                if (!isFavorited) {
                    if (favoriteCount >= 3) {
                        Toast.makeText(getContext(), "찜 목록이 가득 찼습니다. 비우고 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    imbEmptyHeart.setImageResource(R.drawable.full_heart);
                    imbEmptyHeart.setTag("full");
                    Toast.makeText(getContext(), plan.getSub_name() + " 찜 목록에 추가!", Toast.LENGTH_SHORT).show();

                    favRef.child(encodedSubName).setValue(true);
                    favoriteCount++;
                } else {
                    imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                    imbEmptyHeart.setTag("empty");
                    Toast.makeText(getContext(), plan.getSub_name() + " 찜 목록에서 제거", Toast.LENGTH_SHORT).show();

                    favRef.child(encodedSubName).removeValue();
                    favoriteCount--;
                }
            });

            return convertView;
        }
    }

    public static class SubscriptionPlan implements Comparable<MainActivity5.SubscriptionPlan> {
        private String sub_name, price, telecom_name, usage_network, speed_limit;

        public String getSub_name() {
            return sub_name;
        }

        public void setSub_name(String sub_name) {
            this.sub_name = sub_name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getPriceInt() {
            try {
                return Integer.parseInt(price.replaceAll("\\D+", ""));
            } catch (NumberFormatException e) {
                Log.e("NumberFormatException", "Failed to parse price: " + price);
                return 0;
            }
        }

        public String getTelecomName() {
            return telecom_name;
        }

        public void setTelecomName(String telecom_name) {
            this.telecom_name = telecom_name;
        }

        public String getUsageNetwork() {
            return usage_network;
        }

        public void setUsageNetwork(String usage_network) {
            this.usage_network = usage_network;
        }

        public String getSpeedLimit() {
            return speed_limit;
        }

        public void setSpeedLimit(String speed_limit) {
            this.speed_limit = speed_limit;
        }

        @Override
        public int compareTo(SubscriptionPlan o) {
            return Integer.compare(this.getPriceInt(), o.getPriceInt());
        }
    }

    private String encodeForFirebaseKey(String key) {
        key = key.replace('.', ',');
        key = key.replace('#', '-');
        key = key.replace('$', '+');
        key = key.replace('[', '(');
        key = key.replace(']', ')');
        return key;
    }
}
