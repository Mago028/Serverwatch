package com.serverwatch.notifier;

import com.serverwatch.entity.AlarmLog;

public interface Notifier {

    /**
     * 알람 하나에 대해 외부 알림 전송 (이메일, 슬랙, SMS 등)
     */
    void notify(AlarmLog alarmLog);
}
