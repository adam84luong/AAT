package ch.bailu.aat_lib.dispatcher;

import ch.bailu.aat_lib.gpx.GpxInformation;

public abstract class ContentSource  {
    private OnContentUpdatedInterface target = OnContentUpdatedInterface.NULL;

    public void setTarget(OnContentUpdatedInterface t) {
        target = t;
    }

    public abstract void requestUpdate();

    public void sendUpdate(int iid, GpxInformation info) {
        target.onContentUpdated(iid, info);
    }


    public abstract void onPause();
    public abstract void onResume();

    public abstract int getIID();
    public abstract GpxInformation getInfo();
}
