package ch.bailu.aat.services.elevation.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Closeable;

import ch.bailu.aat.preferences.map.SolidDem3Directory;
import ch.bailu.aat.preferences.map.SolidDem3EnableDownload;
import ch.bailu.aat.services.ServiceContext;
import ch.bailu.aat.services.background.DownloadTask;
import ch.bailu.aat.services.elevation.tile.Dem3Tile;
import ch.bailu.aat.util.AppIntent;
import ch.bailu.aat.util.OldAppBroadcaster;
import ch.bailu.aat.util.Timer;
import ch.bailu.aat_lib.coordinates.Dem3Coordinates;
import ch.bailu.aat_lib.dispatcher.AppBroadcaster;
import ch.bailu.foc.Foc;

public final class Dem3TileLoader implements Closeable {
    private static final long MILLIS=2000;

    private final ServiceContext scontext;
    private final Dem3Tiles tiles;

    private Dem3Coordinates pending = null;



    public Dem3TileLoader(ServiceContext sc, Dem3Tiles t) {
        tiles = t;
        scontext = sc;

        OldAppBroadcaster.register(sc.getContext(), onFileDownloaded, AppBroadcaster.FILE_CHANGED_ONDISK);

    }


    private final Timer timer = new Timer(() -> {
        if (havePending()) {
            loadOrDownloadPending();
            stopTimer();
        }
    }, MILLIS);


    private final BroadcastReceiver onFileDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = AppIntent.getFile(intent);
            Dem3Tile tile = tiles.get(id);

            if (tile != null) {
                tile.reload(scontext);
            }
        }
    };


    private void loadOrDownloadPending() {
        final Dem3Coordinates toLoad = pending;
        pending = null;

        if (toLoad != null) {
            loadNow(toLoad);

            if (new SolidDem3EnableDownload(scontext.getContext()).getValue()) {
                downloadNow(toLoad);
            }
        }
    }


    public Dem3Tile loadNow(Dem3Coordinates c) {
        if (havePending()) cancelPending();
        return loadIntoOldestSlot(c);
    }

    private Dem3Tile loadIntoOldestSlot(Dem3Coordinates c) {
        if (tiles.have(c) == false) {
            final Dem3Tile slot = tiles.getOldestProcessed();

            if (slot != null && !slot.isLocked()) {
                slot.load(scontext, c);
            }
        }

        return tiles.get(c);
    }



    public void loadOrDownloadLater(Dem3Coordinates c) {
        if (pending == null) { // first BitmapRequest
            startTimer();
        }
        pending = c;
    }


    public void cancelPending() {
        pending = null;
        stopTimer();
    }

    private boolean havePending() {
        return pending != null;
    }


    private void startTimer() {
        timer.close();
        timer.kick();
    }

    private void stopTimer() {
        timer.close();
    }



    private void downloadNow(Dem3Coordinates c) {
        Foc file = new SolidDem3Directory(scontext.getContext()).toFile(c);
        if (!file.exists()) {
            DownloadTask handle = new DownloadTask(scontext.getContext(), c.toURL(), file);
            scontext.getBackgroundService().process(handle);
        }
    }


    @Override
    public void close() {
        scontext.getContext().unregisterReceiver(onFileDownloaded);
    }
}
