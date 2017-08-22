package com.mararun.runservice.sample.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.mararun.runservice.MaraTrackerConfig;
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

public class MyRunningService extends MaraRunningEngineService {

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

    }

    @Override
    protected MaraTrackerConfig createRunConfig() {
        MaraTrackerConfig maraTrackerConfig = MaraTrackerConfig.createDefault();
        maraTrackerConfig.setEnableAutoPause(false);//自动暂停关闭
        maraTrackerConfig.setEnvironment(1);//1路跑，2室内跑
        return maraTrackerConfig;
    }

    @Override
    protected void onRunDataUpdated(double distance, long duration, int avgPace, int lapPace, boolean lastLocationValid, double latitude, double longitude) {
        safeObserverBroadcast(observer -> {
            mRunInfo.setDistance(distance);
            mRunInfo.setDuration(duration);
            mRunInfo.setPace(avgPace);
            mRunInfo.setLapPace(lapPace);
            mRunInfo.setLat(latitude);
            mRunInfo.setLon(longitude);
            observer.onRunDataChanged(mRunInfo);
        });
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
