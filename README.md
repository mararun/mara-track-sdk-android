本文档记述马拉马拉跑步引擎（Mararun Run Engine，下称引擎或Engine）SDK Android版本（下称SDK）的介绍和集成指引。

## SDK结构
### 整体结构
SDK整体结构的UML如下图所示。

>产品 > Android SDK集成文档 > tracker-sdk-android.png

#### MaraRunEngineManager
对Engine进行生命周期管理和初始化。
#### MaraTrackerConfig
封装了初始化引擎时需要使用的配置项。
#### MaraRunningEngineService
对Engine接口调用进行封装的service基类。使用时继承此类，重写相关方法，利用接口进行控制和数据获取。
#### Pedometer
包括两种计步器的实现，可用于初始化service。如果必要，也可以实现自定义的`Pedometer`版本。

### 功能范围
SDK对于Engine进行了简单封装，对外提供初始化、控制、数据获取接口以及相关回调。
引擎相关服务的保活、定位数据的获取，请在继承基类的服务中实现。

## SDK集成和使用
### 集成SDK

声明一个Service（例如`MyRunningEngineService`）继承自`MaraRunningEngineService`，并实现必须实现的protected方法，具体实现请参考之后的章节。
在Manifest中声明此Service，注意`process`必须为`:run`：
```xml
<service
    android:name=".service.MyRunningEngineService"
    android:exported="true"
    android:process=":run" />
```

### KEY的保存和认证

>todo

### 配置文件
`MaraTrackerConfig`封装引擎初始化和配置需要的一些选项，包括：
* `userId` 用户标识，会记录在最终的跑步详情中
* `lapDistance` 每圈距离，单位米，影响`LapInfo`的计算，默认为1000
* `environment` 跑步类型（室外跑`Indoor`、室内跑`Road`），参看`MaraTrackerConfig.ENVIRONMENT_*`
* `enableAutoPause`是否开启自动暂停，默认关闭
* `enableDistanceProvision`是否启用步数补充跑步里程，默认开启
* `enableHA`是否启用高可靠机制。启用以后引擎会在存储器保存跑步数据。app意外中断后可以恢复跑步，默认开启
* `enableExceptionalPointFilter`是否开启飘移点过滤，默认开启
如果希望使用自定义配置，重写MaraRunningEngineService基类的下列方法：

```java
/**
 * 创建引擎配置，详细信息参看<code>MaraTrackerConfig</code>
 * @return config实例
 */
protected MaraTrackerConfig createRunConfig();
```

### 状态控制
Engine的状态转换如下图所示：
> 产品 > Android SDK集成文档 > tracker-sdk-android-run-stat.png

利用上图所示的下列方法对Engine跑步状态进行控制：

```java
/**
 * 开始跑步
 */
protected void startRun();

/**
 * 停止跑步
 */
protected void stopRun();

/**
 * 暂停跑步
 */
protected void pauseRun();

/**
 * 继续跑步
 */
protected void resumeRun();
```

### 数据更新
跑步过程中需要向Service基类更新跑步定位数据，默认期望为1秒一次。

```java
/**
 * 更新定位数据，数据类似于116.1/90.0，精度可以自由掌控
 * @param lat 纬度
 * @param lon 精度
 * @param alt 海拔，单位米
 */
protected void updateLocation(double lat, double lon, double alt);
```

需要注意的是，Engine会动态建议定位数据的更新间隔，并在下列回调中返回建议值：

```java
/**
 * 引擎的定位间隔产生变化，如果需要更新定位相关配置，可以override使用
 * @param interval 建议定位间隔，单位秒
 */
protected void onRunLocationIntervalChanged(int interval);
```

如果希望根据建议更新间隔重新设置定位配置，那么重写此方法获取相关间隔信息。

### 状态和数据更新回调
操作引擎、更新数据后，相关的状态会在更改后进行回调，数据则以1秒的间隔进行回调：
```java
/**
 * 在真正开始启动跑步引擎前被调用，用于使用者进行一些lazy initiation实例的初始化
 */
protected abstract void preRunStarted();

/**
 * 在引擎启动后被调用
 * @param startSucceeded 引擎是否启动成功
 */
protected abstract void onRunStarted(boolean startSucceeded);

/**
 * 在跑步暂停时被调用
 * @param isAutoPause 是否为自动暂停
 */
protected abstract void onRunPaused(boolean isAutoPause);

/**
 * 在跑步继续时调用
 */
protected abstract void onRunResumed();

/**
 * 在跑步停止时调用，此时引擎已经停止，但还并未关闭
 * @param runDetailJson 跑步详情的json数据
 */
protected abstract void onRunStopped(String runDetailJson);

/**
 * 在跑步完成时调用，调用的时机在引擎关闭之后。此时如果再次需要引擎启动，需要进行初始化。
 */
protected abstract void onRunFinished();

/**
 * 在跑步数据进行更新时被调用。
 * 如果跑步处于暂停状态，则不会调用
 * @param distance 距离，单位米
 * @param duration 时长，单位秒
 * @param pace 平均配速，单位秒
 * @param lapPace 当圈平均配速，单位秒
 * @param locationValid 上一个定位点是否合法
 */
protected abstract void onRunDataUpdated(double distance, long duration, int pace, int lapPace, boolean locationValid);
```

### 数据获取
利用下列接口同步获取跑步相关数据：
```java
/**
 * 获取当前跑步距离
 * @return 距离，单位米
 */
protected double getDistance();

/**
 * 获取平均配速
 * @return 配速，单位秒
 */
protected int getAveragePace();

/**
 * 获取当圈配速
 * @return 配速，单位秒
 */
protected int getLapPace();

/**
 * 获取当前跑步时长
 * @return 时长，单位秒
 */
protected long getDuration();

/**
 * 获取暂停时长
 * @return 时长，单位秒
 */
protected long getHaltDuration();

/**
 * 获取当前海拔
 * @return 海拔，单位米
 */
protected int getAltitude();

/**
 * 获取总爬升
 * @return 爬升，单位米
 */
protected int getTotalAscend();

/**
 * 获取总下降
 * @return 下降，单位米
 */
protected int getTotalDescend();

/**
 * 获取当圈爬升
 * @return 爬升，单位米
 */
protected int getLapAscend();

/**
 * 获取当圈下降
 * @return 下降，单位米
 */
protected int getLapDescend();

/**
 * 获取平均步频
 * @return 平均步频，单位步/分钟
 */
protected int getAverageCadence();

/**
 * 获取跑步详细数据，具体格式信息请参考文档
 * @return 跑步详细数据，格式为json
 */
protected String getRunDetailJson();

/**
 * 获取数据最后一次更新时间
 * @return 时间，为unix时间戳
 */
protected long getLastUpdateTime();

/**
 * 获取跑步模式
 * @return 模式，室内 or 路跑
 */
protected int getRunningMode();

/**
 * 获取跑步状态
 * @return 状态
 */
protected int getRunStatus();
```

### 跑步详情结构
从Engine获取的跑步详情，格式为json。
其中大多数字段的用途一目了然，下面数据结构进行介绍。
```json
{
    "altitude_up": 0,                             // 海拔总爬升，单位米
    "altitude_down": 62.200000000000003,          // 海拔总下降，单位米
    "avr_pace": 265.53340766565816,               // 平均配速，单位秒
    "avr_step_freq": 3.0218874997147789,          // 平均步频，单位步/秒
    "avr_step_length": 1.2462424460176627,        // 平均步长，单位米
    "cheat_flag": 0,                              // 是否为可疑数据，1为可疑
    "disabled_duration": 0.060811042785644531,    // GPS信号不可用时间，单位秒
    "distance": 11568.868626381962,               // 跑步距离，单位米
    "duration": 3071.9211091995239,               // 跑步时长，单位秒       
    "halted_duration": 573.08153891563416,        // 暂停时间，单位秒
    "init_time": 1501072208,                      // 引擎初始化时间戳，unix epoc time
    "start_time": 1501104372,                     // 跑步开始时间戳，unix epoc time
    "end_time": 1501108017,                       // 跑步完成时间戳，unix epoc time
    "environment": 1,                             // 跑步类型，1为路跑
    "faint_distance": 0,                          // GPS不可用时用计步补充距离，单位米
    "faint_duration": 0,                          // GPS不可用时间，单位秒
    "lapinfo": {
        "lap_detail": [{
            "asc": 0,                             // 圈总爬升，单位米
            "dec": 37.100000000000001,            // 圈总下降，单位米
            "distance": 1000,                     // 圈距离，单位米
            "duration": 263.18735673677486,       // 圈时长，单位秒
            "endpoint": {                         // 圈完结点，经纬度
                "latitude": 39.866745062934029,
                "longitude": 116.50043294270833
            },
            "lap_no": 1,                          // 圈编号
            "pace": 263.18735673677486,           // 圈配速，单位秒
            "start_time": 1501104372.9105909,     // 圈开始时间戳，unix epoc time
            "step_freq": 2.8230839399443743,      // 圈平均步频，单位步/秒
            "step_length": 1.3458950201884252,    // 圈平均步长，单位米
            "step_num": 743                       // 圈总步数
        },
        //...,
        "lap_num": 12                             // 总圈数，包括不足一圈距离的最后一圈
    },
    "max_pace": 256.68859662604513,               // 最快配速，单位秒/每圈距离
    "min_pace": 275.99028879037826,               // 最慢配速，单位秒/每圈距离
    "max_altitude": 58.200000000000003,           // 最高海拔，单位米
    "min_altitude": 2.2000000000000002,           // 最低海拔，单位米
    "max_step_freq": 3.2333333333333334,          // 单位时间（1分钟）最大步频，单位步/秒
    "max_step_length": 2.789730014175178,         // 单位时间（1分钟）最大步长，单位米
    "min_step_freq": 0,                           // 单位时间（1分钟）最小步频，单位步/秒
    "min_step_length": 0,                         // 单位时间（1分钟）最小步长，单位米
    "step_info": [{                               // 一个计步单位时间内数据
        "distance": 217.39579980598864,           // 距离，单位米
        "duration": 60,                           // 时长，单位秒
        "seq_no": 0,                              // 数据编号
        "step": 123                               // 单位总步数
    },
    "total_step_num": 1684.0,                     // 总步数
    "trackpoints": [{
        "altitude": 62.5,                         // 定位点海拔
        "latitude": 39.85809348,                  // 定位点纬度
        "longitude": 116.4951745,                 // 定位点经度
        "seq_no": 0,                              // 定位点编号
        "speed": 2.45,                            // 定位点瞬时速度，单位米/秒
        "status": 1,                              // 定位点状态，1为跑步中，2为手动暂停，3为自动暂停
        "timestamp": 1496744718                   // 定位点时间戳
    },
    //...
}
```






 