package ch.bailu.aat.map.tile.source;

import android.content.Context;

import org.mapsforge.core.model.Tile;

import java.util.Random;

import ch.bailu.aat.map.tile.TileFlags;
import ch.bailu.aat.preferences.map.AndroidSolidTileCacheDirectory;
import ch.bailu.aat.services.cache.Obj;
import ch.bailu.aat.services.cache.ObjTileDownloadable;
import ch.bailu.aat_lib.util.fs.AppDirectory;

public class DownloadSource extends Source {


    public static final int MIN_ZOOM = 1;
    public static final int MAX_ZOOM =17; // 18 takes way too much space for the gain.


    private final Random random = new Random();
    private final int minZoom, maxZoom;

    private final String name;
    private final String apiKey;

    private final String[] urls;

    private final int alpha;
    private final boolean transparent;



    public DownloadSource(String n, int a, final String... url) {
        this(n, "", MIN_ZOOM, MAX_ZOOM, a, (a != OPAQUE), url);
    }

    public DownloadSource(String n, String k, int a, final String... url) {
        this(n, k, MIN_ZOOM, MAX_ZOOM, a, (a != OPAQUE), url);
    }
    public DownloadSource(String n, int minZ, int maxZ, int a, final String... url) {
        this(n, "",minZ, maxZ, a, (a != OPAQUE), url);
    }


    public DownloadSource(String n, String k, int minZ, int maxZ, int a, boolean t, String... u) {
        name = n;
        apiKey = k;
        minZoom = minZ;
        maxZoom = maxZ;
        urls = u;
        alpha = a;
        transparent = (a != OPAQUE);
    }



    public String getName() {
        return name;
    }


    @Override
    public String getID(Tile tile, Context context) {
        return AppDirectory.getTileFile(genRelativeFilePath(tile, name), new AndroidSolidTileCacheDirectory(context)).getPath();
    }


    @Override
    public int getMinimumZoomLevel() {
        return minZoom;
    }

    @Override
    public int getMaximumZoomLevel() {
        return maxZoom;
    }

    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public int getAlpha() {
        return alpha;
    }



    @Override
    public Obj.Factory getFactory(Tile t) {
        return new ObjTileDownloadable.Factory(t, this);
    }

    public String getTileURLString(Tile tile) {
        return getBaseUrl() + tile.zoomLevel + "/" + tile.tileX + "/" + tile.tileY + EXT + apiKey;
    }

    private String getBaseUrl() {
        return urls[random.nextInt(urls.length)];
    }


    public static boolean isDownloadBackgroundSource(Source source) {
        return (source == MAPNIK || source == OPEN_TOPO_MAP || source == OPEN_CYCLE_MAP);
    }


    public final static DownloadSource MAPNIK =
            new DownloadSource("Mapnik",
                    OPAQUE,
                    "https://a.tile.openstreetmap.org/",
                    "https://b.tile.openstreetmap.org/",
                    "https://c.tile.openstreetmap.org/") {

                @Override
                public int getPaintFlags() {
                    return TileFlags.SCALE_FLAGS;
                }
            };


    public final static DownloadSource OPEN_TOPO_MAP =
            new DownloadSource("OpenTopoMap",
                    OPAQUE,
                    "https://a.tile.opentopomap.org/",
                    "https://b.tile.opentopomap.org/",
                    "https://c.tile.opentopomap.org/") {

                @Override
                public int getPaintFlags() {
                    return TileFlags.SCALE_FLAGS;
                }
            };

    public final static DownloadSource OPEN_CYCLE_MAP =
            new DownloadSource("OpenCycleMap",
                    "?apikey=4fc8425f35f44f11a59407ef5de1e2c2",
                    OPAQUE,
                    "https://tile.thunderforest.com/cycle/") {

                @Override
                public int getPaintFlags() {
                    return TileFlags.SCALE_FLAGS;
                }
            };


    public final static DownloadSource TRAIL_MTB =
            new DownloadSource("TrailMTB",
                    TRANSPARENT,
                    "https://tile.waymarkedtrails.org/mtb/");

    public final static DownloadSource TRAIL_SKATING =
            new DownloadSource("TrailSkating",
                    TRANSPARENT,
                    "https://tile.waymarkedtrails.org/skating/");


    public final static DownloadSource TRAIL_HIKING =
            new DownloadSource("TrailHiking",
                    TRANSPARENT,
                    "https://tile.waymarkedtrails.org/hiking/");


    public final static DownloadSource TRAIL_CYCLING =
            new DownloadSource("TrailCycling",
                    TRANSPARENT,
                    "https://tile.waymarkedtrails.org/cycling/");



    public final static DownloadSource TRANSPORT_OVERLAY =
            new DownloadSource("OpenPtMap",
                    5, 16,
                    TRANSPARENT,
                    "http://openptmap.org/tiles/");

}
