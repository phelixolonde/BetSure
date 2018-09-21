package com.automata.betsure;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HANSEN on 5/7/2017.
 */

public class FireIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String tkn = FirebaseInstanceId.getInstance().getToken();
        Log.d("Not","Token ["+tkn+"]");

    }
}