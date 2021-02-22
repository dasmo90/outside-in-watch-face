package de.dasmo90.wear.myfirstapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

  public static final String SHARED_PREF_KEY = "de.dasmo90.wear.myfirstapplication";

  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = findViewById(R.id.text);
    mTextView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        long timer = SystemClock.elapsedRealtime() + 1000 * 60;
        sharedPref.edit().putLong("timer", timer).apply();
        mTextView.setTextColor(getResources().getColor(R.color.red, null));
      }
    });

    // Enables Always-on
    setAmbientEnabled();
  }

  @Override
  protected void onResume() {
    super.onResume();

    SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    long timer = sharedPref.getLong("timer", 0L);
    long current = SystemClock.elapsedRealtime();
    if (timer > current) {
      mTextView.setTextColor(getResources().getColor(R.color.red, null));
    } else {
      mTextView.setTextColor(getResources().getColor(R.color.white, null));
    }
  }
}
