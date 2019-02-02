package ch.bailu.aat.description;

import android.content.Context;

import ch.bailu.aat.R;
import ch.bailu.aat.gpx.GpxTrackAttributes;
import ch.bailu.aat.gpx.GpxInformation;

public class AutoPauseDescription extends TimeDescription {
    public AutoPauseDescription(Context context) {
        super(context);
    }


    @Override
    public String getLabel() {
        return getContext().getString(R.string.p_autopause);
    }

    @Override
    public void onContentUpdated(int iid, GpxInformation info) {
        final long autoPause = info.getAttributes().getLongValue(GpxTrackAttributes.INDEX_AUTO_PAUSE);
        setCache(autoPause);
    }
}

