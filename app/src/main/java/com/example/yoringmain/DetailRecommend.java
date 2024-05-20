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
import com.google.firebase.database.ValueEventListener;

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
                                            tvPriceText.setText(String.valueOf(plan.getPrice()) + "원");
                                            tvDataText.setText(plan.getData());
                                            setTelecomImage(plan.getTelecom_name());
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
    }

    public static class SubscriptionPlan implements Comparable<SubscriptionPlan> {
        private String sub_name, telecom_name, usage_network, data, message, sale_price, call;
        private Long price;
        private Double speed_limit;

        public String getSub_name() {
            return sub_name;
        }

        public void setSub_name(String sub_name) {
            this.sub_name = sub_name;
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

    private void setTelecomImage(String telecomName) {
        if (telecomName != null) {
            switch (telecomName) {
                case "프리티":
                    imvTelecom.setImageResource(R.drawable.freet_logo);
                    break;
                case "SKT":
                    imvTelecom.setImageResource(R.drawable.skt_logo);
                    break;
                case "LG":
                    imvTelecom.setImageResource(R.drawable.lg_u_plus_logo);
                    break;
                case "KT":
                    imvTelecom.setImageResource(R.drawable.kt_logo);
                    break;
                case "스마텔":
                    imvTelecom.setImageResource(R.drawable.smt_logo);
                    break;
                case "T Plus":
                    imvTelecom.setImageResource(R.drawable.tplus_logo);
                    break;
                case "SK 세븐모바일":
                    imvTelecom.setImageResource(R.drawable.sk_7mobile_logo);
                    break;
                case "헬로모바일":
                    imvTelecom.setImageResource(R.drawable.hello_mobile_logo);
                    break;
                default:
                    imvTelecom.setImageResource(android.R.color.transparent); // Default or unknown
                    break;
            }
        }
    }

}