package ch.bailu.aat.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import ch.bailu.aat.R;
import ch.bailu.aat.description.AltitudeDescription;
import ch.bailu.aat.description.AverageSpeedDescription;
import ch.bailu.aat.description.ContentDescription;
import ch.bailu.aat.description.CurrentSpeedDescription;
import ch.bailu.aat.description.DescriptionInterface;
import ch.bailu.aat.description.DistanceDescription;
import ch.bailu.aat.description.MaximumSpeedDescription;
import ch.bailu.aat.description.TimeDescription;
import ch.bailu.aat.description.TrackerStateDescription;
import ch.bailu.aat.dispatcher.ContentDispatcher;
import ch.bailu.aat.dispatcher.ContentSource;
import ch.bailu.aat.dispatcher.CurrentLocationSource;
import ch.bailu.aat.dispatcher.EditorSource;
import ch.bailu.aat.dispatcher.OverlaySource;
import ch.bailu.aat.dispatcher.TrackerSource;
import ch.bailu.aat.gpx.GpxInformation;
import ch.bailu.aat.helpers.AppLayout;
import ch.bailu.aat.helpers.AppLog;
import ch.bailu.aat.services.ServiceContext.ServiceNotUpException;
import ch.bailu.aat.services.editor.EditorHelper;
import ch.bailu.aat.views.CockpitView;
import ch.bailu.aat.views.ContentView;
import ch.bailu.aat.views.ControlBar;
import ch.bailu.aat.views.MultiView;
import ch.bailu.aat.views.NumberView;
import ch.bailu.aat.views.TrackDescriptionView;
import ch.bailu.aat.views.VerticalView;
import ch.bailu.aat.views.graph.DistanceAltitudeGraphView;
import ch.bailu.aat.views.graph.DistanceSpeedGraphView;
import ch.bailu.aat.views.map.OsmInteractiveView;
import ch.bailu.aat.views.map.overlay.CurrentLocationOverlay;
import ch.bailu.aat.views.map.overlay.OsmOverlay;
import ch.bailu.aat.views.map.overlay.control.EditorOverlay;
import ch.bailu.aat.views.map.overlay.control.InformationBarOverlay;
import ch.bailu.aat.views.map.overlay.control.NavigationBarOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxDynOverlay;
import ch.bailu.aat.views.map.overlay.gpx.GpxOverlayListOverlay;
import ch.bailu.aat.views.map.overlay.grid.GridDynOverlay;

public class TrackerActivity extends AbsDispatcher implements OnClickListener{
 
    
    
    private static final String SOLID_KEY="tracker";

    private LinearLayout contentView;
    private Button       startPause;
    private ImageButton  activityCycle, multiCycle;
    private NumberView   trackerState;
    private MultiView    multiView;
    private OsmInteractiveView   map;
    
    private EditorHelper edit;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edit = new EditorHelper(getServiceContext());
        contentView = new ContentView(this);
        contentView.addView(createButtonBar());
        multiView = createMultiView();
        contentView.addView(multiView);
        setContentView(contentView);

        createDispatcher();
        
    }


    @Override
    public void onDestroy() {
        edit.close();
        super.onDestroy();
    }


    private MultiView createMultiView() {
        ContentDescription[] data = new ContentDescription[] { 
                new CurrentSpeedDescription(this),
                new AltitudeDescription(this),
                new TimeDescription(this),
                new DistanceDescription(this),
                new AverageSpeedDescription(this),
                new MaximumSpeedDescription(this),
        };

        CockpitView cockpit = new CockpitView(this, SOLID_KEY, INFO_ID_TRACKER, data);

        map = new OsmInteractiveView(getServiceContext(), SOLID_KEY);
        TrackDescriptionView viewData[] = {
                cockpit, 
                map,
                new VerticalView(this, SOLID_KEY, INFO_ID_TRACKER, new TrackDescriptionView[] {
                        new DistanceAltitudeGraphView(this, SOLID_KEY),
                        new DistanceSpeedGraphView(this, SOLID_KEY)})
        };   

        return new MultiView(this, SOLID_KEY, INFO_ID_ALL, viewData);
    }

    private ControlBar createButtonBar() {
        ControlBar bar = new ControlBar(
                this, 
                AppLayout.getOrientationAlongSmallSide(this));

        startPause = bar.addButton("");
        activityCycle = bar.addImageButton(R.drawable.go_down_inverse);
        multiCycle = bar.addImageButton(R.drawable.go_next_inverse);

        trackerState = new NumberView(new TrackerStateDescription(this), 
                GpxInformation.ID.INFO_ID_TRACKER);
        bar.addView(trackerState);
        bar.setOnClickListener1(this);

        return bar;
    }




    @Override
    public void onClick(View v) {
        if (v==startPause) {
            try {
                onStartPauseClick();
            } catch (ServiceNotUpException e) {
                AppLog.e(this, e);
            }

        } else if (v == activityCycle) {
            ActivitySwitcher.cycle(this);

        } else if (v ==multiCycle) {
            multiView.setNext();
        }
    }

    
    private void createDispatcher() {
            OsmOverlay overlayList[] = {
                    new GpxOverlayListOverlay(map, getServiceContext()),
                    new GpxDynOverlay(map, getServiceContext(), GpxInformation.ID.INFO_ID_TRACKER), 
                    new CurrentLocationOverlay(map),
                    new GridDynOverlay(map, getServiceContext()),
                    new NavigationBarOverlay(map),
                    new InformationBarOverlay(map),
                    new EditorOverlay(map, getServiceContext(), GpxInformation.ID.INFO_ID_EDITOR_DRAFT, edit),
            };
            map.setOverlayList(overlayList);
            
            DescriptionInterface[] target = new DescriptionInterface[] {
                    multiView,trackerState,this
            };

            ContentSource[] source = new ContentSource[] {
                    new EditorSource(getServiceContext(), edit),
                    new TrackerSource(getServiceContext()),
                    new CurrentLocationSource(getServiceContext()),
                    new OverlaySource(getServiceContext()),
            };
            setDispatcher(new ContentDispatcher(this,source, target));
    }


    @Override
    public void updateGpxContent(GpxInformation info) {
        updateStartButtonText(startPause, info);
    }
}
