package com.hested.android.nerdlauncher;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Project:  NerdLauncher
 * Package:  com.hested.android.nerdlauncher
 * Date:     03-05-2017
 * Time:     00:03
 * Author:   Johnni Hested
 */

public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }

}
