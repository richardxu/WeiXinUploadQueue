package cn.ihealthbaby.weitaixinpro.blue;

/**
 * Created by jinliang on 15/11/7.
 * 蓝牙服务的实体消息类型
 */
public class BlueServiceEvent {
    private int  blueServiceType ;// 发送消息的类型

    /**
     * 事件的类型
     */

    public static final int STOP_WRITE_VOICE_FILE = 0x01;
    //停止写入语音
    public static final int stopWriteVoiceFile = 0x02;

    // 开始记录心率
    public static final int  startRecord=0x03;
    // 停止记录心率
    public static final int  endRecord =0x04 ;


    private  String recordId ;//记录的id

    private int  voiceVolume;//传输的是蓝牙的声音的大小


    public BlueServiceEvent(int blueServiceType) {
        this.blueServiceType = blueServiceType;
    }

    public BlueServiceEvent(int blueServiceType, String recordId) {
        this.blueServiceType = blueServiceType;
        this.recordId = recordId;
    }

    public int getBlueServiceType() {
        return blueServiceType;
    }

    public void setBlueServiceType(int blueServiceType) {
        this.blueServiceType = blueServiceType;
    }

    public int getVoiceVolume() {
        return voiceVolume;
    }

    public void setVoiceVolume(int voiceVolume) {
        this.voiceVolume = voiceVolume;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}
