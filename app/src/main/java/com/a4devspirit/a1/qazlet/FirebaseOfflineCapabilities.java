package com.a4devspirit.a1.qazlet;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by PKTL on 6/12/17.
 */

public class FirebaseOfflineCapabilities extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
