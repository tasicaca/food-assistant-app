package com.skywalkers.foodassistant;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class FADBConnector {

    final private static String lokacija="192.168.43.79"; // Kada testirate na telefonu, izmenite ovo u adresu vaseg servera/kompa
    final public static String adresa = "http://"+lokacija+"/faapp/index.php";
    private static FADBConnector mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private FADBConnector(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized FADBConnector getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FADBConnector(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
