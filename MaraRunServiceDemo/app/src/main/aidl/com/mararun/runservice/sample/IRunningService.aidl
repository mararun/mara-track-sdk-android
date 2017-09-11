// RemoteRunningEngineService.aidl
package com.mararun.runservice.sample;
import com.mararun.runservice.sample.IRunningServiceObserver;
import com.mararun.runservice.sample.model.IpcRunInfo;

// Declare any non-default types here with import statements

interface IRunningService {
       void startRun();//开始跑步
       void pauseRun();//暂停跑步
       void stopRun();//停止跑步并获取保存的跑步id
       void resumeRun();//继续跑步
       int getRunStatus();//获取跑步状态
       void setAutoPause(boolean autoPause);//设置自动暂停

       void registerObserver(IRunningServiceObserver listener);//注册监听
       void unregisterObserver(IRunningServiceObserver listener);//取消注册监听
       String restoreInterruptData();//跑步恢复
}
