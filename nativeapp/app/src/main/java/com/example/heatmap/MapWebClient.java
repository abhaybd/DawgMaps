package com.example.heatmap;

import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;

public class MapWebClient extends WebChromeClient {
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        Log.d("MAPCLIENT", "Geoloc request!");
        callback.invoke(origin, true, true);
    }
}
