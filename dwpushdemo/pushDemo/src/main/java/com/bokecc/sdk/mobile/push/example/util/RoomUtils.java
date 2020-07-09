package com.bokecc.sdk.mobile.push.example.util;

import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.entity.RoomInfo;

/**
 * Created by dds on 2020/4/28.
 * ddssingsong@163.com
 */
public class RoomUtils {


    /**
     * 是否显示聊天
     *
     * @return 类型
     */
    public static boolean isShowChat() {
        RoomInfo roomInfo = DWPushSession.getInstance().getRoomInfo();
        return !(roomInfo.getModule() == 1 || roomInfo.getModule() == 6);
    }
}
