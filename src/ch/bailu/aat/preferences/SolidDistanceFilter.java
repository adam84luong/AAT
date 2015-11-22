package ch.bailu.aat.preferences;

import ch.bailu.aat.R;
import android.content.Context;

public class SolidDistanceFilter extends SolidIndexList {
    private static final String KEY="distance_filter_";
    
    private static final float[] VALUE_LIST = {
    0f,
    1f,
    2f,
    4f,
    6f,
    8f,
    10f,
    15f,
    20f,
    25f,
    30f,
    99f,
    };
    
    private final SolidUnit sunit;

    public SolidDistanceFilter(Context c, int i) {
        super(Storage.preset(c), KEY+i);
        sunit = new SolidUnit(c);
    }
    
    
    public float getMinDistance() {
        return VALUE_LIST[getIndex()];
    }
    
    
    @Override
    public String getLabel() {
        return "Distance filter*";
    }

    @Override
    public int length() {
        return VALUE_LIST.length;
    }

    
    private String getString(int i) {
        if (i==0) return getContext().getString(R.string.off);
        if (i==length()-1) getContext().getString(R.string.auto);
        
        return String.format("%.2f%s", VALUE_LIST[i] * sunit.getAltitudeFactor(), sunit.getAltitudeUnit());
    }
    
    @Override
    public String getString() {
        return getString(getIndex());
    }

    @Override
    public String[] getStringArray() {
        String[] list = new String[length()];
        for (int i=0; i<length(); i++){
            list[i]=getString(i);
        }
        return list;
    }

}
