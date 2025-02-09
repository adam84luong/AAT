package ch.bailu.aat.preferences.map;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import ch.bailu.aat.factory.AndroidFocFactory;
import ch.bailu.aat.preferences.Storage;
import ch.bailu.aat.util.fs.AndroidVolumes;
import ch.bailu.aat_lib.preferences.SolidFile;
import ch.bailu.aat_lib.resources.Res;
import ch.bailu.aat_lib.util.fs.AppDirectory;
import ch.bailu.foc.Foc;
import ch.bailu.foc_android.FocAndroid;

public class SolidMapsForgeDirectory extends SolidFile {

    public final static String EXTENSION = ".map";
    public final static String MAPS_DIR = "maps";
    public final static String ORUX_MAPS_DIR = "oruxmaps/mapfiles";

    private final static String KEY = SolidMapsForgeDirectory.class.getSimpleName();

    private final Context context;
    public SolidMapsForgeDirectory(Context c) {
        super(new Storage(c), KEY, new AndroidFocFactory(c));
        context = c;
    }

    public Context getContext() {
        return context;
    }
    @Override
    public String getLabel() {
        return Res.str().p_mapsforge_directory();
    }




    @Override
    public String getValueAsString() {
        String r = super.getValueAsString();


        if (getStorage().isDefaultString(r)) {
            r = getDefaultValue();

            setValue(r);
        }
        return r;
    }


    public String getDefaultValue() {
        ArrayList<String> list = new ArrayList<>(5);

        list = buildSelection(list);

        File external = getContext().getExternalFilesDir(null);

        if (external != null) {
            add_w(list, FocAndroid.factory(getContext(), external.getAbsolutePath()));
        }

        if (list.size()>0) {
            return list.get(0);
        }
        return "";
    }


    @Override
    public ArrayList<String> buildSelection(ArrayList<String> list) {
        final ArrayList<Foc> dirs = getWellKnownMapDirs();

        for (Foc dir : dirs) {
            add_r(list, dir);
            add_subdirectories_r(list, dir);
        }
        return list;
    }


    public ArrayList<Foc> getWellKnownMapDirs() {
        final ArrayList<Foc> dirs = new ArrayList<>(5);
        final AndroidVolumes volumes = new AndroidVolumes(getContext());

        for (Foc f : volumes.getVolumes()) {
            add_dr(dirs, f.child(MAPS_DIR));
            add_dr(dirs, f.child(AppDirectory.DIR_AAT_DATA + "/" + MAPS_DIR));
            add_dr(dirs, f.child(ORUX_MAPS_DIR));
        }

        return dirs;
    }

    private static void add_dr(ArrayList<Foc> dirs, Foc file) {
        if (file.canRead() && file.isDir()) {
            dirs.add(file);
        }
    }
}
