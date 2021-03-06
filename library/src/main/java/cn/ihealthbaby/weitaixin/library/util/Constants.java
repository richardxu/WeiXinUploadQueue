package cn.ihealthbaby.weitaixin.library.util;

import android.support.v4.BuildConfig;

import java.util.UUID;

/**
 * Created by Think on 2015/6/16.
 */
public class Constants {
	/**
	 * 应用模式，暂时只区分发布和调试模式
	 */
	public static final boolean MODE_RELEASE = !BuildConfig.DEBUG;
	public static final boolean MODE_DEBUG = BuildConfig.DEBUG;
	public static final boolean MODE = MODE_DEBUG;
	public static final boolean MODE_LOG = MODE;
	public static final boolean MODE_TOAST = MODE;
	//内网
//	public static final String SERVER_URL = "http://192.168.1.253:8080/port/v1/";
	//外网
	public static final String SERVER_URL = "http://dev.ihealthbaby.cn:8280/v1/";
	//@顾文昌pc
//	public static final String SERVER_URL = "http://192.168.1.37:8080/ihealthbaby-port/v1/";
	//@jinliqiang
//	public static final String SERVER_URL = "http://192.168.1.2:8080/port/v1/";
	public static final String MOCK_SERVER_URL = "http://localhost:9800/";
	public static final String MIME_TYPE_WAV = "audio/x-wav";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_STATE_FAIL = 7;
	public static final int MESSAGE_READ_FETAL_DATA = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_CANNOT_CONNECT = 5;
	public static final int MESSAGE_CONNECTION_LOST = 6;
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final UUID COMMON_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String RECORD_PATH = "";
	public static final String TEMP_FILE_NAME = "TEMP";
	public static final String EXTENTION_NAME = ".WAV";
	public static final String INTENT_LOCAL_RECORD_ID = "LOCAL_RECORD_ID";
	public static final String INTENT_URL = "URL";
	public static final String INTENT_CONSUMED_TIME = "CONSUMED_TIME";
	public static final String INTENT_DURATION = "DURATION";
	public static final String INTENT_INTERVAL = "INTERVAL";
	public static final String INTENT_ID = "ID";
	public static final int CODE_200_OK = 200;
	public static final String INTENT_STATUS = "STATUS";
	public static final String INTENT_FEELING = "FEELING";
	public static final String INTENT_PURPOSE = "PURPOSE";
	public static final String INTENT_POSITION = "POSITION";
	public static final String INTENT_USER_ID = "USER_ID";
	public static final String BUNDLE_USER = "USER";
	public static final String INTENT_USER_NAME = "USER_NAME";
	public static final String INTENT_DELIVERY_TIME = "DELIVERY_TIME";
	public static final String INTENT_HID = "HID";
	public static final String INTENT_SERVICE_TYPE = "SERVICE_TYPE";
	public static final String INTENT_SERVICE_ID = "SERVICE_ID";
	public static final String BUG_HD_SDK_GENERAL_KEY = "BUG_HD_SDK_GENERAL_KEY";
}
