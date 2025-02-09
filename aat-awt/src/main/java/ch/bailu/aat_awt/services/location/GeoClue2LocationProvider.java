package ch.bailu.aat_awt.services.location;

import org.freedesktop.dbus.exceptions.DBusException;

import ch.bailu.aat_awt.services.location.interfaces.Client;
import ch.bailu.aat_lib.gpx.StateID;
import ch.bailu.aat_lib.service.location.LocationInformation;
import ch.bailu.aat_lib.service.location.LocationService;
import ch.bailu.aat_lib.service.location.LocationServiceInterface;
import ch.bailu.aat_lib.service.location.LocationStackChainedItem;
import ch.bailu.aat_lib.service.location.LocationStackItem;

/**
 * GeoClue2 DBus interface:
 * https://www.freedesktop.org/software/geoclue/docs/
 *
 * Java-DBus library:
 * https://github.com/hypfvieh/dbus-java
 *
 * Nice GUI DBus debugger:
 * https://wiki.gnome.org/Apps/DFeet/
 *
 * How to create GeoClue2 java interfaces:
 * 1. Install 'geoclue-2.0' package
 * 2. Get introspection files (xml-format):
 *    'ls /usr/share/dbus-1/interfaces/* | grep GeoClue2'
 * 3. Clone 'https://github.com/hypfvieh/dbus-java' and read 'docs/code-generation.html'
 * 4. Generate java classes from introspection files according to documentation
 * 5. Manually adjust java files
 *
 */
public class GeoClue2LocationProvider extends LocationStackChainedItem {


    private GeoClue2Dbus geoClue2;
    private final Object lock;


    public GeoClue2LocationProvider(LocationServiceInterface serviceInterface, LocationStackItem item) {
        super(item);

        lock = serviceInterface;

        new Thread(GeoClue2Dbus.class.getSimpleName()) {
            @Override
            public void run() {
                try {
                    geoClue2 = new GeoClue2Dbus();
                    geoClue2.connect(signal -> updateStateAndLocation(signal));
                    geoClue2.start();
                    updateState();

                } catch (DBusException e) {
                    passState(StateID.NOSERVICE);
                }
            }
        }.start();
    }

    private void updateStateAndLocation(Client.LocationUpdated signal) {
        synchronized (lock) {
            try {
                LocationInformation location = geoClue2.getLocation(signal.getNew());
                passLocation(location);
            } catch (Exception e) {
                passState(StateID.NOSERVICE);
            }
        }
    }

    private void updateState() {
        synchronized (lock) {
            if (geoClue2.getActive()) {
                passState(LocationService.INITIAL_STATE);
            } else {
                passState(StateID.OFF);
            }
        }
    }

    @Override
    public void close() {
        if (geoClue2 != null) {
            geoClue2.stop();
        }
    }

}
