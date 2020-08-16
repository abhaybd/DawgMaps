package com.example.heatmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Reference: https://github.com/chrisveness/latlon-geohash/blob/master/latlon-geohash.js
 */
public class GeoUtil {
    private static final int HASH_PRECISION = 9;
    private static final Map<Direction, String[]> borderMap = new HashMap<>();
    private static final Map<Direction, String[]> neighborMap = new HashMap<>();
    private static final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    private static final double EARTH_RAD_METERS = 6378000;

    static {
        borderMap.put(Direction.North, new String[]{"prxz", "bcfguvyz"});
        borderMap.put(Direction.East, new String[]{"bcfguvyz", "prxz"});
        borderMap.put(Direction.South, new String[]{"028b", "0145hjnp"});
        borderMap.put(Direction.West, new String[]{"0145hjnp", "028b"});

        neighborMap.put(Direction.North, new String[]{"p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx"});
        neighborMap.put(Direction.East, new String[]{"bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy"});
        neighborMap.put(Direction.South, new String[]{"14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp"});
        neighborMap.put(Direction.West, new String[]{"238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb"});
    }

    public enum Direction {
        North, East, South, West
    }

    /**
     * Implements haversine formula.
     *
     * @param fromLat  Latitude of first point.
     * @param fromLong Longitude of first point.
     * @param toLat    Latitude of second point.
     * @param toLong   Longitude of second point.
     * @return Distance between them, in meters.
     */
    public static double getGCDistance(double fromLat, double fromLong, double toLat, double toLong) {
        double fromLatRad = fromLat * Math.PI / 180;
        double toLatRad = toLat * Math.PI / 180;
        double deltaLatRad = (toLat - fromLat) * Math.PI / 180;
        double deltaLongRad = (toLong - fromLong) * Math.PI / 180;

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(fromLatRad) * Math.cos(toLatRad) *
                        Math.sin(deltaLongRad / 2) * Math.sin(deltaLongRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RAD_METERS * c;
    }

    public static String[] getNeighbors(String hash) {
        String[] neighbors = new String[8];
        int i = 0;
        for (Direction d : Direction.values()) {
            neighbors[i++] = getAdjacent(hash, d);
        }

        neighbors[i++] = getAdjacent(neighbors[0], Direction.East);
        neighbors[i++] = getAdjacent(neighbors[1], Direction.South);
        neighbors[i++] = getAdjacent(neighbors[2], Direction.West);
        neighbors[i] = getAdjacent(neighbors[3], Direction.North);

        return neighbors;
    }

    public static String getAdjacent(String hash, Direction direction) {
        hash = hash.toLowerCase();

        char lastChar = hash.charAt(hash.length() - 1);
        String parent = hash.substring(0, hash.length() - 1);
        int type = hash.length() % 2;

        if (borderMap.get(direction)[type].indexOf(lastChar) != -1 && parent.length() > 0) {
            parent = getAdjacent(parent, direction);
        }

        return parent + base32.charAt(neighborMap.get(direction)[type].indexOf(lastChar));
    }

    public static String geoHash(double latitude, double longitude) {
        int idx = 0;
        int bit = 0;
        boolean evenBit = true;
        StringBuilder hash = new StringBuilder();

        double latMin = -90;
        double latMax = 90;
        double longMin = -180;
        double longMax = 180;

        while (hash.length() < HASH_PRECISION) {
            if (evenBit) {
                double longMid = (longMin + longMax) / 2;
                idx <<= 1;
                if (longitude >= longMid) {
                    idx++;
                    longMin = longMid;
                } else {
                    longMax = longMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2;
                idx <<= 1;
                if (latitude >= latMid) {
                    idx++;
                    latMin = latMid;
                } else {
                    latMax = latMid;
                }
            }
            evenBit = !evenBit;

            if (++bit == 5) {
                hash.append(base32.charAt(idx));
                bit = 0;
                idx = 0;
            }
        }

        return hash.toString();
    }
}
