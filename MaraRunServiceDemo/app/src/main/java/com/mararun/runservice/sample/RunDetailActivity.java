package com.mararun.runservice.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.mararun.runservice.sample.model.RunDetail;
import com.mararun.runservice.sample.model.TrackPoint;
import com.mararun.runservice.sample.util.FsRunDetailAccessHelper;
import com.mararun.runservice.sample.util.RunDetailAccessHelper;
import com.mararun.runservice.sample.util.RunInfoFormatter;

import java.util.Collections;
import java.util.List;

/**
 * Created by mararun
 */

public class RunDetailActivity extends Activity {
    private MapView mapView;
    private AMap mAMap;
    private PolylineOptions mAMapLineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_detail);

        initMap(savedInstanceState);

        handleRunInfo(getIntent().getLongExtra(Constants.RUN_INFO_KEY, RunDetailAccessHelper.INVALID_RUN_DETAIL_ID));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initMap(Bundle bundle) {
        mapView = findViewById(R.id.v_map);
        mapView.onCreate(bundle);
        mAMap = mapView.getMap();

        mAMapLineOptions = new PolylineOptions()
                .color(ContextCompat.getColor(this, R.color.runDetailLine))
                .width(10f);
    }

    private void handleRunInfo(long runId) {
        if (runId == RunDetailAccessHelper.INVALID_RUN_DETAIL_ID)
            return;

        RunDetail runDetail = new FsRunDetailAccessHelper().load(this, runId);
        if (runDetail == null)
            return;
        
        ((TextView)findViewById(R.id.tv_distance)).setText(RunInfoFormatter.formatDistanceMeter(runDetail.getDistance()));
        ((TextView)findViewById(R.id.tv_duration)).setText(RunInfoFormatter.formatDuration((long)runDetail.getDuration()));
        ((TextView)findViewById(R.id.tv_pace_avg)).setText(RunInfoFormatter.formatMMSSPace((int)runDetail.getAvrPace()));

        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        List<TrackPoint> trackPoints = runDetail.getTrackpoints();
        if (trackPoints != null && !trackPoints.isEmpty()) {
            for (TrackPoint trackPoint : trackPoints) {
                LatLng latLng = new LatLng(trackPoint.getLatitude(), trackPoint.getLongitude());
                mAMapLineOptions.add(latLng);
                boundBuilder.include(latLng);
            }
            mAMap.addPolyline(mAMapLineOptions);
        }
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                boundBuilder.build(),
                mapView.getWidth(), mapView.getHeight(),
                200)
        );
    }
}
