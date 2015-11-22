package ch.bailu.aat.views.map.overlay.grid;

import org.osmdroid.util.GeoPoint;

import ch.bailu.aat.coordinates.MeterCoordinates;
import ch.bailu.aat.coordinates.UTMCoordinates;
import ch.bailu.aat.views.map.AbsOsmView;


public class UTMGridOverlay extends MeterGridOverlay {
    public UTMGridOverlay(AbsOsmView osmPreview) {
        super(osmPreview);
    }
    
    @Override
    public MeterCoordinates getCoordinates(GeoPoint p) {
        return new UTMCoordinates(p);
    }
}
