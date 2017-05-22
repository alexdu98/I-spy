package com.um.asn.i_spy;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Alexandre on 22/05/2017.
 */

public class SlaveService extends IntentService {

    public SlaveService(){
        super("SlaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        stopSelf();
    }

}
