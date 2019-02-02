package ch.bailu.aat.gpx;

public abstract class GpxAttributes {

    public final static GpxAttributes NULL = new GpxAttributesNull();
    public final static String NULL_VALUE="";


    public abstract String get(String key);
    public abstract String getValue(int index);
    public abstract String getKey(int index);

    public abstract void put(String key, String value);
    public abstract int size();
    public abstract void remove(String key);


    public float getFloatValue(int index) {
        try {
            return Float.valueOf(getValue(index));
        } catch (NumberFormatException e) {
            return 0f;
        }
    }


    public long getLongValue(int index) {
        try {
            return Long.valueOf(getValue(index));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
