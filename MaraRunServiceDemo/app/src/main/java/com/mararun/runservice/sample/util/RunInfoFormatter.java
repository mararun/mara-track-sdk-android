package com.mararun.runservice.sample.util;

import com.mararun.runservice.sample.Constants;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by mararun
 */

public class RunInfoFormatter {


    /**
     * 格式化距离
     * @param distanceInMeter 米
     * @return 格式化距离
     */
    public static String formatDistanceMeter(double distanceInMeter) {
        boolean oneHundredOrMore = Double.compare(distanceInMeter, 100d * Constants.LAP_METER) >= 0;
        int trails = oneHundredOrMore ? 1 : 2;
        return formatDistanceMeter(distanceInMeter, trails);
    }

    /**
     * 格式化距离
     * @param distanceInMeter 米
     * @param trails 保留小数个数
     * @return 格式化距离
     */
    public static String formatDistanceMeter(double distanceInMeter, int trails) {
        DecimalFormat distanceFormatter = new DecimalFormat();
        distanceFormatter.setMaximumFractionDigits(trails);
        distanceFormatter.setMinimumFractionDigits(trails);
        distanceFormatter.setGroupingSize(0);
        distanceFormatter.setRoundingMode(RoundingMode.FLOOR);
        return distanceFormatter.format(distanceInMeter / Constants.LAP_METER);
    }

    /**
     * 格式化时长
     * @param seconds 秒
     * @return 时长
     */
    public static String formatDuration(long seconds) {
        long second = seconds % 60;
        long minutes = seconds / 60;
        long minute = minutes % 60;
        long hours = minutes / 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minute, second);
    }

    /**
     * 格式化配速
     * @param time 时间
     * @return 配速
     */
    public static String formatMMSSPace(long time) {
        long second = time % 60;
        long minutes = time / 60;
        return String.format(Locale.US, "%02d'%02d\"", minutes, second);
    }
}
