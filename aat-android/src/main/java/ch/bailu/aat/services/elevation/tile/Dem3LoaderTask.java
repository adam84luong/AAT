package ch.bailu.aat.services.elevation.tile;

import java.util.zip.ZipInputStream;

import ch.bailu.aat.services.ServiceContext;
import ch.bailu.aat.services.background.FileTask;
import ch.bailu.aat.util.OldAppBroadcaster;
import ch.bailu.aat_lib.dispatcher.AppBroadcaster;
import ch.bailu.aat_lib.logger.AppLog;
import ch.bailu.foc.Foc;

public final class Dem3LoaderTask extends FileTask {

    private final Dem3Array array;
    private final Dem3Status status;

    public Dem3LoaderTask(Foc f, Dem3Array a, Dem3Status s) {
        super(f);
        array = a;
        status = s;
    }


    @Override
    public long bgOnProcess(ServiceContext sc) {
        ZipInputStream zip = null;


        synchronized (array) {
            try {
                zip = new ZipInputStream(getFile().openR());


                int total = 0;

                zip.getNextEntry();

                int count;

                do {
                    count = zip.read(array.data, total, array.data.length - total);
                    total += count;

                } while (count > 0 && total < array.data.length && canContinue());


                if (canContinue()) {
                    status.setStatus(Dem3Status.VALID);
                }

            } catch (Exception e) {


                AppLog.w(this, e.getMessage());
                status.setStatus(Dem3Status.EMPTY);
            }

            Foc.close(zip);
        }


        if (status.getStatus() == Dem3Status.VALID || status.getStatus() == Dem3Status.EMPTY) {
            OldAppBroadcaster.broadcast(sc.getContext(),
                    AppBroadcaster.FILE_CHANGED_INCACHE, getFile());

            return array.data.length;
        }

        return 0;
    }
}
