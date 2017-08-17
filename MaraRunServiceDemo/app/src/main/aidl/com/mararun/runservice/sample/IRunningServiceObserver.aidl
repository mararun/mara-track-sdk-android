package com.mararun.runservice.sample;
import com.mararun.runservice.sample.model.IpcRunInfo;

interface IRunningServiceObserver {
    void onRunDataChanged(in IpcRunInfo runInfo);
    void onPauseStatusChanged(boolean isPaused);
    void onRunStopped(long runId);
}





