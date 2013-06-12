package com.jeradmeisner.sickbeardalpha;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by jerad on 6/10/13.
 */
public class ShowsActivity extends SherlockFragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawable abBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background_light_green);
        getSupportActionBar().setBackgroundDrawable(abBackgroundDrawable);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}