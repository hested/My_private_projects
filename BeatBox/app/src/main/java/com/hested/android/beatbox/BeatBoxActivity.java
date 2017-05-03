package com.hested.android.beatbox;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Project:  BeatBox
 * Package:  com.hested.android.beatbox
 * Date:     02-05-2017
 * Time:     15:30
 * Author:   Johnni Hested
 */

public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}
