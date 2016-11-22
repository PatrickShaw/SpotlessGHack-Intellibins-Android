package com.intellibins.intellibinsandroid.restdata;

import android.location.Location;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class BinInformation implements Identifiable {
    public Integer id;
    public int full;
    public String level;
    public double[] coord;
    public Location getLocation() {
        Location location = new Location("bin " + Integer.toString(id));
        location.setLatitude(coord[0]);
        location.setLongitude(coord[1]);
        return location;
    }
    @Override
    public Integer getId() {
        return id;
    }
}
