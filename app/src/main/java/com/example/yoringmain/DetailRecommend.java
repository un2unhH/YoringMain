package com.example.yoringmain;

import android.os.Bundle;
import android.telephony.SubscriptionPlan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import java.util.Comparator;

public class DetailRecommend extends AppCompatActivity {

    private TextView tvCurrentSubName, tvPriceText, tvDataText;
    private ImageView imvTelecom;
    private DatabaseReference databaseRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommend);

        tvCurrentSubName = findViewById(R.id.tv_current_sub_name);
        tvPriceText = findViewById(R.id.tv_price_text);
        tvDataText = findViewById(R.id.tv_data_text);
        imvTelecom = findViewById(R.id.imv_telecom);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            String userUid = user.getUid();
            DatabaseReference userSubRef = databaseRef.child("UserSubscriptions").child(userUid);

            userSubRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String subscriptionName = dataSnapshot.getValue(String.class);
                    tvCurrentSubName.setText(subscriptionName);

                    DatabaseReference subPlanRef = databaseRef.child("SubscriptionPlan");
                    subPlanRef.orderByChild("sub_name").equalTo(subscriptionName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                                        if (plan != null) {
                                            int dataValue = parseData(plan.getData());
                                            displayData(dataValue);
                                            tvPriceText.setText(plan.getPrice() + "원");
                                            setTelecomImage(plan.getTelecom_name());
                                            TextView tvCurrentUsePrice1 = findViewById(R.id.tv_current_use_price1);
                                            TextView tvCurrentUsePrice2 = findViewById(R.id.tv_current_use_price2);
                                            TextView tvCurrentUsePrice3 = findViewById(R.id.tv_current_use_price3);
                                            TextView tvCurrentUsePrice4 = findViewById(R.id.tv_current_use_price4);
                                            tvCurrentUsePrice1.setText(plan.getPrice() + "원        ->");
                                            tvCurrentUsePrice2.setText(plan.getPrice() + "원        ->");
                                            tvCurrentUsePrice3.setText((plan.getPrice() + 9900) + "원        ->");
                                            tvCurrentUsePrice4.setText((plan.getPrice() + 9900) + "원        ->");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
                }
            });

        }
        int dataUsage = getIntent().getIntExtra("dataUsage", 0);
        boolean isDisneyPlusPicked = getIntent().getBooleanExtra("isDisneyPlusPicked", false);
        Log.d("ReceivedIntentData", "Received dataUsage: " + dataUsage + ", DisneyPlusPicked: " + isDisneyPlusPicked);

        findEligiblePlans(dataUsage);

        findLGPlansForDisneyPlus(dataUsage);

    }

    public static class SubscriptionPlan implements Comparable<SubscriptionPlan> {
        private String sub_name, telecom_name, usage_network, data, message, sale_price, call, real_data;
        private Long price;
        private Double speed_limit;

        public String getSub_name() {
            return sub_name;
        }

        public void setSub_name(String sub_name) {
            this.sub_name = sub_name;
        }

        public String getReal_data() {
            return real_data;
        }

        public void setReal_data(String real_data) {
            this.real_data = real_data;
        }

        public String getTelecom_name() {
            return telecom_name;
        }

        public void setTelecom_name(String telecom_name) {
            this.telecom_name = telecom_name;
        }

        public String getUsage_network() {
            return usage_network;
        }

        public void setUsage_network(String usage_network) {
            this.usage_network = usage_network;
        }

        public Double getSpeed_limit() {
            return speed_limit;
        }

        public void setSpeed_limit(Double speed_limit) {
            this.speed_limit = speed_limit;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSale_price() {
            return sale_price;
        }

        public void setSale_price(String sale_price) {
            this.sale_price = sale_price;
        }

        public String getCall() {
            return call;
        }

        public void setCall(String call) {
            this.call = call;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        @Override
        public int compareTo(SubscriptionPlan o) {
            return Long.compare(this.price, o.price);
        }
    }

    private void setTelecomImage(String telecomName, ImageView imageView) {
        if (telecomName != null) {
            int resId = getTelecomResourceId(telecomName);
            imageView.setImageResource(resId);
        }
    }

    private int getTelecomResourceId(String telecomName) {
        switch (telecomName) {
            case "프리티":
                return R.drawable.freet_logo;
            case "SKT":
                return R.drawable.skt_logo;
            case "LG":
                return R.drawable.lg_u_plus_logo;
            case "KT":
                return R.drawable.kt_logo;
            case "스마텔":
                return R.drawable.smt_logo;
            case "T Plus":
                return R.drawable.tplus_logo;
            case "SK 세븐모바일":
                return R.drawable.sk_7mobile_logo;
            case "헬로모바일":
                return R.drawable.hello_mobile_logo;
            default:
                return android.R.color.transparent;
        }
    }

    private void setTelecomImage(String telecomName) {
        ImageView imageView = findViewById(R.id.imv_telecom);
        setTelecomImage(telecomName, imageView);
    }

    private void findEligiblePlans(int userUsageData) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("data").startAt(userUsageData);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= userUsageData) {
                        eligiblePlans.add(plan);
                    }
                }

                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));
                if (eligiblePlans.size() > 0) {
                    displayPlanDetails(eligiblePlans.get(0), 1);
                    if (eligiblePlans.size() > 1) {
                        displayPlanDetails(eligiblePlans.get(1), 2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DBError", "findEligiblePlans:onCancelled", databaseError.toException());
            }
        });
    }


    private void displayPlanDetails(SubscriptionPlan plan, int planNumber) {
        TextView tvName = findViewById(getResources().getIdentifier("tv_cheap" + planNumber, "id", getPackageName()));
        TextView tvPrice = findViewById(getResources().getIdentifier("tv_cheap_price" + planNumber, "id", getPackageName()));
        TextView tvData = findViewById(getResources().getIdentifier("tv_cheap_data" + planNumber, "id", getPackageName()));
        ImageView imvTelecom = findViewById(getResources().getIdentifier("imv_cheap" + planNumber, "id", getPackageName()));

        tvName.setText(plan.getSub_name());
        tvPrice.setText(plan.getPrice() + "원");
        if (parseData(plan.getData()) == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(plan.getData());
        }
        setTelecomImage(plan.getTelecom_name(), imvTelecom);
    }

    private int parseData(String data) {
        if (data == null) {
            return 0;
        }
        if (data.equals("무제한")) {
            return 301;
        }
        try {
            String numericData = data.replaceAll("[^0-9.]", "");
            return (int) Double.parseDouble(numericData);
        } catch (NumberFormatException e) {
            Log.e("parseData", "Invalid number format: " + data, e);
            return 0;
        }
    }

    private void displayData(int data) {
        TextView tvData = findViewById(R.id.tv_data_text);
        if (data == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(data + " GB");
        }
    }

    private void findLGPlansForDisneyPlus(int userUsageData) {
        Log.d("DetailRecommend", "findLGPlansForDisneyPlus called with dataUsage: " + userUsageData);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("telecom_name").equalTo("LG");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DetailRecommend", "DataSnapshot received, count: " + dataSnapshot.getChildrenCount());
                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= userUsageData) {
                        plan.setPrice(adjustedPrice(plan.getPrice()));
                        eligiblePlans.add(plan);
                        Log.d("DetailRecommend", "Plan added: " + plan.getSub_name() + ", Price after adjustment: " + plan.getPrice());
                    }
                }

                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));
                if (eligiblePlans.size() > 0) {
                    displayRecommendedPlanDetails(eligiblePlans.get(0), 1);
                    if (eligiblePlans.size() > 1) {
                        displayRecommendedPlanDetails(eligiblePlans.get(1), 2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DetailRecommend", "Database query cancelled", databaseError.toException());
            }
        });
    }


    private Long adjustedPrice(Long originalPrice) {
        if (originalPrice < 55000) {
            return originalPrice + 8910;
        } else if (originalPrice >= 55000 && originalPrice < 61000) {
            return originalPrice + 6930;
        } else if (originalPrice >= 61000 && originalPrice < 75000) {
            return originalPrice + 5940;
        } else {
            return originalPrice + 1940;
        }
    }

    private void displayRecommendedPlanDetails(SubscriptionPlan plan, int planNumber) {
        TextView tvName = findViewById(getResources().getIdentifier("tv_recommended" + planNumber, "id", getPackageName()));
        TextView tvPrice = findViewById(getResources().getIdentifier("tv_recommended_price" + planNumber, "id", getPackageName()));
        TextView tvData = findViewById(getResources().getIdentifier("tv_recommended_data" + planNumber, "id", getPackageName()));
        ImageView imvTelecom = findViewById(getResources().getIdentifier("imv_recommended" + planNumber, "id", getPackageName()));

        tvName.setText(plan.getSub_name());
        tvPrice.setText(plan.getPrice() + "원");
        if (parseData(plan.getData()) == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(plan.getData());
        }
        setTelecomImage(plan.getTelecom_name(), imvTelecom);
    }
}