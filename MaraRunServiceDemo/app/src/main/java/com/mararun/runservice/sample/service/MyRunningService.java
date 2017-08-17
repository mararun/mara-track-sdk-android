package com.mararun.runservice.sample.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mararun.runservice.engine.MaraRunningEngineService;
import com.mararun.runservice.pedometer.AccelerometerPedometer;
import com.mararun.runservice.pedometer.Pedometer;
import com.mararun.runservice.sample.IRunningService;
import com.mararun.runservice.sample.IRunningServiceObserver;
import com.mararun.runservice.sample.model.IpcRunInfo;
import com.mararun.runservice.sample.util.FsRunDetailAccessHelper;
import com.mararun.runservice.sample.util.RunDetailAccessHelper;
import com.mararun.runservice.util.MaraLogger;

import java.lang.ref.WeakReference;

/**
 * Created by mararun
 */

public class MyRunningService extends MaraRunningEngineService implements AMapLocationListener {
    private AMapLocationClient mLocationClient;

    private RunDetailAccessHelper mRunDetailSaver = new FsRunDetailAccessHelper();
    private RemoteCallbackList<IRunningServiceObserver> mObservers = new RemoteCallbackList<>();
    private IpcRunInfo mRunInfo = new IpcRunInfo();

    /* MaraRunningEngineService */
    @Override
    protected Pedometer createPedometer() {
        SensorManager sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return new AccelerometerPedometer(sensorManager, sensor);
    }

    @Override
    protected void preRunStarted() {
        // 此方法在执行startRun和收到onRunStarted回调之间调用
        // 用于进行一些service需要的工作，例如保活机制初始化等
        startLocation();
    }

    @Override
    protected void onRunStarted(boolean isSucceeded) {
        // 在Engine调用startRun成功后调用
    }

    @Override
    protected void onRunPaused(boolean isAutoPaused) {
        // 在发生暂停时调用，无论是用户触发还是使用了自动暂停后满足条件触发暂停
        // 参数isAutoPaused指示是否为自动暂停
        safeObserverBroadcast(observer -> observer.onPauseStatusChanged(true));
    }

    @Override
    protected void onRunResumed() {
        // 在继续跑步时，无论是用户触发还是使用了自动暂停后满足条件触发继续跑步
        safeObserverBroadcast(observer -> observer.onPauseStatusChanged(false));
    }

    @Override
    protected void onRunStopped(String runDetailJson) {
        // 在跑步完成后调用，参数传递为RunDetail的原始格式
        // 可以参考文档，以及DEMO代码中的RunDetailAccessHelper和RunDetail实体获取具体格式信息
        long runId = mRunDetailSaver.save(getApplicationContext(), runDetailJson);
        safeObserverBroadcast(observer -> observer.onRunStopped(runId));
    }

    @Override
    protected void onRunFinished() {
        // 在引擎完成close操作后调用
        stopLocation();
    }

    @Override
    protected void onRunDataUpdated(double distance, long duration, int avgPace, int lapPace, boolean lastLocationValid) {
        safeObserverBroadcast(observer -> {
            mRunInfo.setDistance(distance);
            mRunInfo.setDuration(duration);
            mRunInfo.setPace(avgPace);
            mRunInfo.setLapPace(lapPace);
            observer.onRunDataChanged(mRunInfo);
        });
    }
    /* MaraRunningEngineService end */

    /* Service */
    @Override
    public void onCreate() {
        super.onCreate();
        initLocationClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }
    /* Service end*/

    /* AMapLocationListener */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mRunInfo.setLat(aMapLocation.getLatitude());
        mRunInfo.setLon(aMapLocation.getLongitude());
        updateLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation.getAltitude());
    }
    /* AMapLocationListener end */

    private void initLocationClient() {
        mLocationClient = new AMapLocationClient(this.getApplicationContext());

        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationClientOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        locationClientOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationClientOption.setInterval(1000);//可选，设置定位间隔，单位毫秒
        locationClientOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        locationClientOption.setLocationCacheEnable(false);// 设置是否开启缓存
        locationClientOption.setMockEnable(true);
        locationClientOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用

        mLocationClient.setLocationOption(locationClientOption);
    }

    // 开始定位
    private void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.setLocationListener(this);
            mLocationClient.startLocation();
        }
    }

    // 停止定位
    private void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.unRegisterLocationListener(this);
        }
    }

    private void safeObserverBroadcast(RunningServiceObserverCallback observerCallback) {
        int num = mObservers.beginBroadcast();
        for (int i = 0; i < num; ++i) {
            try {
                observerCallback.call(mObservers.getBroadcastItem(i));
            } catch (NullPointerException | RemoteException | IllegalStateException e) {
                MaraLogger.e("service update data ex:" + e.getMessage());
            }
        }

        try {
            mObservers.finishBroadcast();
        } catch (NullPointerException | IllegalStateException e) {
            MaraLogger.e(e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IRunningServiceStub(this);
    }

    static class IRunningServiceStub extends IRunningService.Stub {

        private WeakReference<MyRunningService> myRunningServiceWR;

        IRunningServiceStub(MyRunningService myRunningService) {
            myRunningServiceWR = new WeakReference<>(myRunningService);
        }

        private boolean engineNotNull() {
            return myRunningServiceWR != null && myRunningServiceWR.get() != null;
        }

        @Override
        public void startRun() throws RemoteException {
            if (engineNotNull())
                myRunningServiceWR.get().startRun();
        }

        @Override
        public void pauseRun() throws RemoteException {
            if (engineNotNull())
                myRunningServiceWR.get().pauseRun();
        }

        @Override
        public void stopRun() throws RemoteException {
            if (engineNotNull()) {
                myRunningServiceWR.get().stopRun();
            }
        }

        @Override
        public void resumeRun() throws RemoteException {
            if (engineNotNull())
                myRunningServiceWR.get().resumeRun();
        }

        @Override
        public int getRunStatus() throws RemoteException {
            int status = STATUS_NOT_START;
            if (engineNotNull())
                status = myRunningServiceWR.get().getRunStatus();
            return status;
        }

        @Override
        public void registerObserver(IRunningServiceObserver listener) throws RemoteException {
            if (engineNotNull())
                myRunningServiceWR.get().mObservers.register(listener);
        }

        @Override
        public void unregisterObserver(IRunningServiceObserver listener) throws RemoteException {
            if (engineNotNull())
                myRunningServiceWR.get().mObservers.unregister(listener);
        }
    }

    @FunctionalInterface
    private interface RunningServiceObserverCallback {
        void call(IRunningServiceObserver observer) throws RemoteException;
    }
}
