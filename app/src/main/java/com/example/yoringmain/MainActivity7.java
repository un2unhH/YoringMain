package com.example.yoringmain;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity7 extends AppCompatActivity {
    Toolbar toolbar;
    Button btnHandOperated, btnAutomatic, btnFind;
    TextView tvPrintData;
    SeekBar seekBarDataUsage;
    private Spinner spinnerFamily1, spinnerFamily2, spinnerFamily3;
    ImageButton imbNetflix, imbTving, imbWavve, imbDisneyPlus,
            imbNotUseTv, imbTvSkt, imbTvKt, imbTvLg,
            imbNotUseInternet, imbInternetSkt, imbInternetKt, imbInternetLg;
    private boolean isNetflixPicked = false, isTvingPicked = false, isWavvePicked = false, isDisneyPlusPicked = false;
    LinearLayout linearTv2, linearInternet2;
    private static final float TEXT_SIZE_SELECTED = 12f;
    private static final float TEXT_SIZE_DEFAULT = 10f;
    private int selectedTvButtonId = 0;
    private int selectedInternetButtonId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("자세한 요금제 추천");

        btnAutomatic = findViewById(R.id.btn_automatic);
        btnHandOperated = findViewById(R.id.btn_hand_operated);
        btnFind = findViewById(R.id.btn_find);
        tvPrintData = findViewById(R.id.tv_print_data);
        seekBarDataUsage = findViewById(R.id.seekBar_data_usage);

        imbNetflix = findViewById(R.id.imb_netflix);
        imbDisneyPlus = findViewById(R.id.imb_disney_plus);
        imbTving = findViewById(R.id.imb_tving);
        imbWavve = findViewById(R.id.imb_wavve);

        imbNotUseTv= findViewById(R.id.imb_not_use_tv);
        imbTvSkt = findViewById(R.id.imb_tv_skt);
        imbTvKt = findViewById(R.id.imb_tv_kt);
        imbTvLg = findViewById(R.id.imb_tv_lg);

        imbNotUseInternet = findViewById(R.id.imb_not_use_internet);
        imbInternetSkt = findViewById(R.id.imb_internet_skt);
        imbInternetKt = findViewById(R.id.imb_internet_kt);
        imbInternetLg = findViewById(R.id.imb_internet_lg);

        linearTv2 = findViewById(R.id.linear_tv2);
        linearInternet2 = findViewById(R.id.linear_internet2);

        seekBarDataUsage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDataUsageText(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DetailRecommendPage2 액티비티로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity7.this, DetailRecommendPage2.class);
                startActivity(intent); // Intent 실행
            }
        });

        btnAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dataUsageBytes = getDataUsage(MainActivity7.this);
                int dataUsageGB = (int) (dataUsageBytes / (1024 * 1024 * 1024));
                seekBarDataUsage.setProgress(dataUsageGB);
                updateDataUsageText(dataUsageGB);
                Toast.makeText(MainActivity7.this, "자동 데이터 사용량 설정: " + dataUsageGB + " GB", Toast.LENGTH_SHORT).show();
                btnAutomatic.setTextSize(TEXT_SIZE_SELECTED);
                btnHandOperated.setTextSize(TEXT_SIZE_DEFAULT);
            }
        });

        imbNetflix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNetflixImage();
            }
        });

        imbTving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTvingImage();
            }
        });

        imbWavve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWavveImage();
            }
        });

        imbDisneyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDisneyPlusImage();
            }
        });

        imbNotUseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTvButtons();
            }
        });

        imbNotUseInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInternetButtons();
            }
        });

        imbTvSkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTvButtonClick(imbTvSkt, R.drawable.skt_pick, R.drawable.skt_not_pick);
            }
        });

        imbTvKt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTvButtonClick(imbTvKt, R.drawable.kt_pick, R.drawable.kt_not_pick);
            }
        });

        imbTvLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTvButtonClick(imbTvLg, R.drawable.uplus_pick, R.drawable.uplus_not_pick);
            }
        });

        imbInternetSkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInternetButtonClick(imbInternetSkt, R.drawable.skt_pick, R.drawable.skt_not_pick);
            }
        });

        imbInternetKt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInternetButtonClick(imbInternetKt, R.drawable.kt_pick, R.drawable.kt_not_pick);
            }
        });

        imbInternetLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInternetButtonClick(imbInternetLg, R.drawable.uplus_pick, R.drawable.uplus_not_pick);
            }
        });
        initSpinners();
    }

    private void toggleTvButtons() {
        boolean isEnabled = imbTvSkt.isEnabled();
        imbTvSkt.setEnabled(!isEnabled);
        imbTvKt.setEnabled(!isEnabled);
        imbTvLg.setEnabled(!isEnabled);

        float alphaValue = isEnabled ? 0.4f : 1.0f;
        linearTv2.setAlpha(alphaValue);
    }

    private void toggleInternetButtons() {
        boolean isEnabled = imbInternetSkt.isEnabled();
        imbInternetSkt.setEnabled(!isEnabled);
        imbInternetKt.setEnabled(!isEnabled);
        imbInternetLg.setEnabled(!isEnabled);

        float alphaValue = isEnabled ? 0.4f : 1.0f;
        linearInternet2.setAlpha(alphaValue);
    }

    private void toggleNetflixImage() {
        if (isNetflixPicked) {
            imbNetflix.setImageResource(R.drawable.netflix_not_pick);
            isNetflixPicked = false;
        } else {
            imbNetflix.setImageResource(R.drawable.netflix_pick);
            isNetflixPicked = true;
        }
    }

    private void toggleTvingImage() {
        if (isTvingPicked) {
            imbTving.setImageResource(R.drawable.tving_not_pick);
            isTvingPicked = false;
        } else {
            imbTving.setImageResource(R.drawable.tving_pick);
            isTvingPicked = true;
        }
    }

    private void toggleWavveImage() {
        if (isWavvePicked) {
            imbWavve.setImageResource(R.drawable.wavve_not_pick);
            isWavvePicked = false;
        } else {
            imbWavve.setImageResource(R.drawable.wavve_pick);
            isWavvePicked = true;
        }
    }

    private void toggleDisneyPlusImage() {
        if (isDisneyPlusPicked) {
            imbDisneyPlus.setImageResource(R.drawable.disney_not_pick);
            isDisneyPlusPicked = false;
        } else {
            imbDisneyPlus.setImageResource(R.drawable.disney_pick);
            isDisneyPlusPicked = true;
        }
    }

    private void updateDataUsageText(int dataUsage) {
        if (dataUsage == 301) {
            tvPrintData.setText("무제한");
        } else {
            tvPrintData.setText(dataUsage + " GB");
        }
    }

    private long getDataUsage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
            if (networkStatsManager != null) {
                NetworkStats networkStats;
                try {
                    networkStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, null, 0, System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                long totalBytes = 0;
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucket);
                    totalBytes += bucket.getRxBytes() + bucket.getTxBytes();
                }
                networkStats.close();
                return totalBytes;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void handleTvButtonClick(ImageButton button, int pickedResId, int notPickedResId) {
        if (selectedTvButtonId == button.getId()) {
            button.setImageResource(notPickedResId);
            selectedTvButtonId = 0;
        } else {
            if (selectedTvButtonId != 0) {
                ImageButton previousButton = findViewById(selectedTvButtonId);
                if (previousButton != null) {
                    int previousNotPickedResId = getNotPickedTv(previousButton.getId());
                    previousButton.setImageResource(previousNotPickedResId);
                }
            }
            button.setImageResource(pickedResId);
            selectedTvButtonId = button.getId();
        }
    }

    private int getNotPickedTv(int buttonId) {
        if (buttonId == R.id.imb_tv_skt) return R.drawable.skt_not_pick;
        if (buttonId == R.id.imb_tv_kt) return R.drawable.kt_not_pick;
        if (buttonId == R.id.imb_tv_lg) return R.drawable.uplus_not_pick;
        return 0;
    }

    private void handleInternetButtonClick(ImageButton button, int pickedResId, int notPickedResId) {
        if (selectedInternetButtonId == button.getId()) {
            button.setImageResource(notPickedResId);
            selectedInternetButtonId = 0;
        } else {
            if (selectedTvButtonId != 0) {
                ImageButton previousButton = findViewById(selectedTvButtonId);
                if (previousButton != null) {
                    int previousNotPickedResId = getNotPickedInternet(previousButton.getId());
                    previousButton.setImageResource(previousNotPickedResId);
                }
            }
            button.setImageResource(pickedResId);
            selectedInternetButtonId = button.getId();
        }
    }

    private int getNotPickedInternet(int buttonId) {
        if (buttonId == R.id.imb_internet_skt) return R.drawable.skt_not_pick;
        if (buttonId == R.id.imb_internet_kt) return R.drawable.kt_not_pick;
        if (buttonId == R.id.imb_internet_lg) return R.drawable.uplus_not_pick;
        return 0;
    }

    private void initSpinners() {
        spinnerFamily1 = findViewById(R.id.spinner_family1);
        spinnerFamily2 = findViewById(R.id.spinner_family2);
        spinnerFamily3 = findViewById(R.id.spinner_family3);

        List<String> telecomCompanies = new ArrayList<>();
        telecomCompanies.add("SKT");
        telecomCompanies.add("KT");
        telecomCompanies.add("LG U+");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, telecomCompanies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFamily1.setAdapter(adapter);
        spinnerFamily2.setAdapter(adapter);
        spinnerFamily3.setAdapter(adapter);

        int offsetPx = 20;
        spinnerFamily1.setDropDownVerticalOffset(offsetPx);
        spinnerFamily2.setDropDownVerticalOffset(offsetPx);
        spinnerFamily3.setDropDownVerticalOffset(offsetPx);
    }

}