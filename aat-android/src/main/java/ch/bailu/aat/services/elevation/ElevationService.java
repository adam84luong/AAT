package ch.bailu.aat.services.elevation;

import ch.bailu.aat_lib.coordinates.Dem3Coordinates;
import ch.bailu.aat.services.ServiceContext;
import ch.bailu.aat.services.elevation.loader.Dem3Loader;
import ch.bailu.aat.services.elevation.loader.Dem3Tiles;
import ch.bailu.aat.services.elevation.updater.ElevationUpdaterClient;
import ch.bailu.aat.services.elevation.updater.ElevationUpdater;
import ch.bailu.aat_lib.service.VirtualService;
import ch.bailu.aat_lib.service.elevation.ElevationProvider;
import ch.bailu.aat_lib.service.elevation.ElevetionServiceInterface;

public final class ElevationService extends VirtualService implements ElevationProvider, ElevetionServiceInterface {

    private final ElevationUpdater updater;
    private final Dem3Loader loader;


    public ElevationService(ServiceContext sc) {


        Dem3Tiles tiles = new Dem3Tiles();
        loader = new Dem3Loader(sc, tiles);
        updater = new ElevationUpdater(sc, loader, tiles);

    }

    public void requestElevationUpdates(ElevationUpdaterClient e, Dem3Coordinates[] c) {
        updater.requestElevationUpdates(e, c);
    }

    public void requestElevationUpdates() {
        updater.requestElevationUpdates();
    }


    public void cancelElevationUpdates(ElevationUpdaterClient e) {
        updater.cancelElevationUpdates(e);
    }



    @Override
    public short getElevation(int laE6, int loE6) {
        return loader.getElevation(laE6, loE6);
    }


    public void close() {
        updater.close();
        loader.close();
    }
}
