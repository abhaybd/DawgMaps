package com.example.heatmap;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBase {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final int AREA_PRECISION = 8;

    public interface Callback<T> {
        void accept(T value);
    }

    public static void logLocation(double latitude, double longitude) {
        double timestamp = System.currentTimeMillis(); // TODO: replace with call to NTP server
        String id = getMacAddress();
        String hash = GeoUtil.geoHash(latitude, longitude);

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", timestamp);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("location", hash);

        FirebaseFirestore.getInstance().collection("users").document(id).set(data);
        Log.d("mac address", id);
    }

    public static void getUsersInRange(double latitude, double longitude, double distanceMeters, Callback<Integer> callback) {
        String hash = GeoUtil.geoHash(latitude, longitude);

        if (hash.length() > AREA_PRECISION) {
            hash = hash.substring(0, AREA_PRECISION);
        }

        String[] areas = new String[9];
        areas[0] = hash;
        String[] neighbors = GeoUtil.getNeighbors(hash);
        System.arraycopy(neighbors, 0, areas, 1, neighbors.length);

        CollectionReference users = FirebaseFirestore.getInstance().collection("users");
        final AtomicInteger numUsers = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(areas.length);
        for (String start : areas) {
            String end = start.substring(0, start.length() - 1) + (char) (start.charAt(start.length() - 1) + 1);
            users.whereGreaterThanOrEqualTo("location", start)
                    .whereLessThan("location", end)
                    .get().addOnSuccessListener(task -> {
                        if (task.getDocuments() != null) {
                            for (DocumentSnapshot doc : task.getDocuments()) {
                                try {
                                    double lat = doc.getDouble("latitude");
                                    double lng = doc.getDouble("longitude");

                                    double dist = GeoUtil.getGCDistance(latitude, longitude, lat, lng);
                                    if (dist <= distanceMeters) {
                                        numUsers.incrementAndGet();
                                    }
                                } catch (NullPointerException e) {
                                    Log.w("DataBase", "Invalid document found in db!");
                                }
                            }
                            latch.countDown();
                        }
                    }
            );
        }

        executorService.execute(() -> {
            try {
                latch.await();
                callback.accept(numUsers.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static String getMacAddress() {
        String stringMac = "";
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaceList) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    for (int i = 0; i < networkInterface.getHardwareAddress().length; i++) {
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }

                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return stringMac;
    }

}
