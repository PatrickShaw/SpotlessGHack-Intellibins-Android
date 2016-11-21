package com.intellibins.intellibinsandroid.restdata;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class BinInformation implements Identifiable {
    public Integer id;
    public int full;
    public String level;
    public double[] coord;

    @Override
    public Integer getId() {
        return id;
    }
}
