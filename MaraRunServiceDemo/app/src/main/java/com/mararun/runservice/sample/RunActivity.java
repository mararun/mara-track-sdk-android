package com.mararun.runservice.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.mararun.runservice.sample.model.IpcRunInfo;
import com.mararun.runservice.sample.service.MyRunningService;
import com.mararun.runservice.sample.util.RunInfoFormatter;
import com.mararun.runservice.util.MaraLogger;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by mararun
 * 运动UI基类
 */

public class RunActivity extends Activity implements View.OnClickListener {
    private ServiceConnection mServiceConnection;
    private IRunningService mRunningService;
    private final AppRunningObserver mRunningServiceObserver = new AppRunningObserver();
    private IpcRunInfo mLastRunInfo;

    private TextView tvDistance;
    private TextView tvDuration;
    private TextView tvAvgPace;
    private TextView tvLapPace;
    private Button btnRun;

    private MapView mapView;
    private AMap mAMap;
    private PolylineOptions mAMapLineOptions;

    private boolean mRunStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        initMap(savedInstanceState);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService();
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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.stop_run_before_exit, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_run:
                if (isPaused()) {
                    continueRun();
                } else {
                    pauseRun();
                }
                break;
            case R.id.btn_stop:
                stopRun();
                break;
            default:
                break;
        }
    }

    private void initMap(Bundle bundle) {
        mapView = findViewById(R.id.v_map);
        mapView.onCreate(bundle);
        mAMap = mapView.getMap();

        mAMapLineOptions = new PolylineOptions()
                .color(ContextCompat.getColor(this, R.color.colorAccent))
                .width(10f);
    }

    private void initViews() {
        tvDistance = findViewById(R.id.tv_distance);
        tvDuration = findViewById(R.id.tv_duration);
        tvAvgPace = findViewById(R.id.tv_pace_avg);
        tvLapPace = findViewById(R.id.tv_pace_lap);
        btnRun = findViewById(R.id.btn_run);
        Button btnStop = findViewById(R.id.btn_stop);

        btnRun.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private boolean isPaused() {
        try {
            int status = mRunningService.getRunStatus();
            return status == MyRunningService.STATUS_AUTO_PAUSE || status == MyRunningService.STATUS_PAUSE;
        } catch (RemoteException e) {
            MaraLogger.e("app checkPaused ex:" + e.getMessage());
        }
        return false;
    }

    private void pauseRun() {
        try {
            mRunningService.pauseRun();
        } catch (RemoteException e) {
            MaraLogger.e("app pauseRun ex:" + e.getMessage());
        }
    }

    private void continueRun() {
        try {
            mRunningService.resumeRun();
        } catch (RemoteException e) {
            MaraLogger.e("app continueRun ex:" + e.getMessage());
        }
    }

    private void startRun() {
        try {
            mRunningService.startRun();
        } catch (RemoteException e) {
            MaraLogger.e("app startRun ex:" + e.getMessage());
        }
    }

    private void stopRun() {
        Observable.just(null)
                .subscribeOn(Schedulers.newThread())
                .subscribe(runId -> {
                    doStopRun();
                    afterServiceDisconnected();
                }, t -> MaraLogger.e("app stop run ex:" + t.getMessage()));
    }

    private void doStopRun() {
        try {
            // DEMO中，stopRun为异步操作，并且通过runId进行交互
            // 在Observer中收到保存完毕跑步信息的回调
            mRunningService.stopRun();
        } catch (RemoteException e) {
            MaraLogger.e("app engine stopRun ex:" + e.getMessage());
        }
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        MaraLogger.i(">app bindService");

        try {
            startService(new Intent(this, MyRunningService.class));
            new Thread(() -> {
                Intent intent = new Intent(RunActivity.this, MyRunningService.class);
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        MaraLogger.i("app bindService connected");
                        mRunningService = IRunningService.Stub.asInterface(service);
                        afterServiceConnected();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        MaraLogger.i("app bindService disconnected");
                    }
                };
                bindService(intent, mServiceConnection, BIND_AUTO_CREATE | BIND_ABOVE_CLIENT);
                MaraLogger.i("app bindService requested");
            }).start();
        } catch (SecurityException e) {
            MaraLogger.e("app bindService ex:" + e.getMessage());
        }
    }

    private void afterServiceConnected() {
        try {
            mRunningService.registerObserver(mRunningServiceObserver);
        } catch (RemoteException e) {
            MaraLogger.e("app afterServiceConnected ex:" + e.getMessage());
        }

        if (!mRunStopped) {
            startRun();
        }
    }

    private void afterServiceDisconnected() {
        try {
            mRunningService.unregisterObserver(mRunningServiceObserver);
        } catch (RemoteException e) {
            MaraLogger.e("app afterServiceDisconnected ex:" + e.getMessage());
        }
    }

    private void handleRunInfo(@NonNull IpcRunInfo runInfo) {
        runOnUiThread(() -> {
            updateBasicInfo(runInfo);
            updateMap(runInfo);
            mLastRunInfo = runInfo;
        });
    }

    private void updateBasicInfo(IpcRunInfo runInfo) {
        // 基本跑步信息
        tvDistance.setText(RunInfoFormatter.formatDistanceMeter(runInfo.getDistance()));
        tvDuration.setText(RunInfoFormatter.formatDuration(runInfo.getDuration()));
        tvAvgPace.setText(RunInfoFormatter.formatMMSSPace(runInfo.getPace()));
        tvLapPace.setText(RunInfoFormatter.formatMMSSPace(runInfo.getLapPace()));
    }

    private void updateMap(IpcRunInfo runInfo) {
        // 路线绘制
        if (mLastRunInfo == null) {
            return;
        }
        if (Double.compare(runInfo.getLat(), mLastRunInfo.getLat()) != 0
                || Double.compare(runInfo.getLon(), mLastRunInfo.getLon()) != 0) {
            List<LatLng> points = mAMapLineOptions.getPoints();
            LatLng lastPoint = points == null || points.isEmpty() ? null : points.get(points.size() - 1);
            LatLng currentPoint = new LatLng(runInfo.getLat(), runInfo.getLon());
            if (lastPoint != null) {
                mAMapLineOptions.getPoints().clear();
                mAMapLineOptions.getPoints().add(lastPoint);
                mAMapLineOptions.getPoints().add(currentPoint);
                mAMap.addPolyline(mAMapLineOptions);
            } else {
                mAMapLineOptions.getPoints().add(currentPoint);
            }

            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPoint, 17));
        }
    }

    private void handlePauseStatusChanged(boolean isPaused) {
        runOnUiThread(() -> {
            btnRun.setText(isPaused ? R.string.continue_run : R.string.pause_run);
            btnRun.setBackgroundColor(ContextCompat.getColor(this, isPaused ? R.color.runContinueBg : R.color.colorAccent));
        });
    }

    private void handleRunStopped(long runId) {
        mRunStopped = true;
        Log.e("stopRun ex", "stopRun ex unbindService aaaa");
        unbindService(mServiceConnection);
        runOnUiThread(() -> {
            startActivity(new Intent(this, RunDetailActivity.class).putExtra(Constants.RUN_INFO_KEY, runId));
            finish();
        });
    }

    /* As IRunningServiceObserver */
    private final class AppRunningObserver extends IRunningServiceObserver.Stub {
        @Override
        public void onRunDataChanged(IpcRunInfo runInfo) throws RemoteException {
            if (runInfo == null) {
                return;
            }
            handleRunInfo(runInfo);
        }

        @Override
        public void onPauseStatusChanged(boolean isPaused) throws RemoteException {
            handlePauseStatusChanged(isPaused);
        }

        @Override
        public void onRunStopped(long runId) throws RemoteException {
            handleRunStopped(runId);
        }
    }
    /* As IRunningServiceObserver end*/
}
