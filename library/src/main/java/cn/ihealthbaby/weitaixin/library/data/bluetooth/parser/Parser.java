package cn.ihealthbaby.weitaixin.library.data.bluetooth.parser;

import android.content.Context;
import android.media.AudioTrack;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.AudioPlayer;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.FHRParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ParseException;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.exception.ValidationParseException;
import cn.ihealthbaby.weitaixin.library.event.MonitorStartEvent;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ByteUtil;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.LocalRecordIdUtil;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/7/17 12:52.
 */
public class Parser {
	//header
	private static final int HEADER_0 = 0X55;
	private static final int HEADER_1 = 0XAA;
	//取后两位
	private static final int LAST2BYTE = 0XFF;
	private static final int LAST2BIT = 0X03;
	//controller
	//心率信息
	@Deprecated
	private static final int CONTROLLER_HEART_BEAT_RATE_V1 = 0X01;
	private static final int CONTROLLER_HEART_BEAT_RATE_V2 = 0X03;
	//声音信息
	@Deprecated
	private static final int CONTROLLER_SOUND_V1 = 0X08;
	private static final int CONTROLLER_SOUND_V2 = 0X09;
	//    private static final int VERSION_1 = 1;
	//    private static final int VERSION_2 = 2;
	//    private static final int CONTROLLER_QUERY_VERSION = 0X56;
	//版本信息
	private static final int CONTROLLER_VERSION_INFO = 0X3F;
	//版本初始信息
	private static final int VERSION_NONE = -1;
	private static final String TAG = "Parser";
	public byte[] oneByte;
	public FileOutputStream fileOutputStream;
	public boolean needPlay;
	private AudioTrack audioTrack;
	private Context context;
	private Handler handler;
	//
	private byte[] fetalDataBufferV1 = new byte[4];
	private byte[] fetalDataBufferV2 = new byte[7];
	//	private byte[] bytes321 = new byte[321];
//	private byte[] bytes101 = new byte[101];
//	private int[] soundDataBufferV1 = new int[321];
//	private int[] soundDataBufferV2 = new int[101];
//修改
	private int[] soundDataBufferV1 = new int[320];
	private int[] soundDataBufferV2 = new int[100];
	private boolean startMonitor;
	private String localRecordId;

	public Parser(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		EventBus.getDefault().register(this);
		audioTrack = AudioPlayer.getInstance().getmAudioTrack();
		audioTrack.play();
	}

	/**
	 * 协议中中间若干个数据包，最后一个为sum校验数据,校验规则为验证sum%256
	 *
	 * @param buffer
	 * @return
	 */
	public boolean validateData(byte[] buffer) throws ValidationParseException {
		int sum = 0;
		for (int i = 0; i < buffer.length - 1; i++) {
			sum += buffer[i];
		}
		if ((sum - buffer[buffer.length - 1]) % 256 == 0) {
			return true;
		} else {
			LogUtil.v(TAG, "validateData::%s %s", sum % 256, buffer[buffer.length - 1]);
			throw new ValidationParseException();
		}
	}

	public FHRPackage parseFHR(byte[] buffer, String version) throws FHRParseException {
		switch (version) {
			case "1":
				return parseFHBV1(buffer);
			case "2":
				return parseFHBV2(buffer);
			default:
				throw new FHRParseException("no such version");
		}
	}

	private int translate(byte oneByte) {
		return oneByte & LAST2BYTE;
	}

	private FHRPackage parseFHBV1(byte[] buffer) {
		FHRPackage FHRPackage1 = new FHRPackage();
		FHRPackage1.setTime(System.currentTimeMillis());
		FHRPackage1.setVersion("1");
		FHRPackage1.setFHR1(buffer[0] & LAST2BYTE);
		FHRPackage1.setAFM(buffer[1] == 0);
		FHRPackage1.setSignalStrength(buffer[2] & LAST2BIT);
		return FHRPackage1;
	}

	private FHRPackage parseFHBV2(byte[] buffer) {
		FHRPackage FHRPackage2 = new FHRPackage();
		FHRPackage2.setTime(System.currentTimeMillis());
		FHRPackage2.setVersion("2");
		FHRPackage2.setFHR1(buffer[0] & LAST2BYTE);
		FHRPackage2.setFHR2(buffer[1] & LAST2BYTE);
		FHRPackage2.setTOCO(buffer[2] & LAST2BYTE);
		FHRPackage2.setAFM((buffer[3] & LAST2BYTE) == 0);
		return FHRPackage2;
	}

	public void parsePackageData(InputStream mmInStream) throws IOException, ParseException {
		oneByte = new byte[1];
		mmInStream.read(oneByte);
		if (translate(oneByte[0]) == 0x55) {
			mmInStream.read(oneByte);
			if (translate(oneByte[0]) == 0xaa) {
				mmInStream.read(oneByte);
				switch (translate(oneByte[0])) {
					//v1,胎心
					case CONTROLLER_HEART_BEAT_RATE_V1:
						mmInStream.read(fetalDataBufferV1);
						validateData(fetalDataBufferV1);
						FHRPackage fhrPackage1 = parseFHR(fetalDataBufferV1, "1");
						DataStorage.fhrPackage = fhrPackage1;
						break;
					//v2,胎心
					case CONTROLLER_HEART_BEAT_RATE_V2:
						mmInStream.read(fetalDataBufferV2);
						FHRPackage fhrPackage2 = parseFHR(fetalDataBufferV2, "2");
						DataStorage.fhrPackage = fhrPackage2;
						break;
					//v1,声音
					case CONTROLLER_SOUND_V1:
						int[] voice = getVoice(mmInStream);
						byte[] v = intForByte(ByteUtil.analysePackage(voice));
						audioTrack.write(v, 0, v.length);
						if (startMonitor) {
							if (fileOutputStream != null) {
								try {
									fileOutputStream.write(v);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						break;
					//v2,声音
					case CONTROLLER_SOUND_V2:
						int[] voiceAd = getVoiceAd(mmInStream);
						byte[] adv = intForByte(ByteUtil.anylyseData(voiceAd, 1));
						audioTrack.write(adv, 0, adv.length);
						if (startMonitor) {
							if (fileOutputStream != null) {
								try {
									fileOutputStream.write(adv);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						break;
					default:
						throw new ParseException("no such controller");
				}
			}
		}
	}

	/**
	 * 解析声音数据
	 *
	 * @param buffer
	 * @return
	 */
	public byte[] parseSound(byte[] buffer, String version) throws ParseException {
		byte[] bytes = null;
		switch (version) {
			case "1":
				bytes = parseSoundV1(buffer);
				break;
			case "2":
				bytes = parseSoundV2(buffer);
				break;
			default:
				break;
		}
		// TODO: 15/8/12 音频解码
		return bytes;
	}

	/**
	 * 获取一个原始的胎声包。
	 *
	 * @param inputStream
	 * @return
	 */
	private int[] getVoice(InputStream inputStream) {
		for (int i = 0; i < 320; i++) {
			try {
				soundDataBufferV1[i] = inputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return soundDataBufferV1;
	}

	private int[] getVoiceAd(InputStream inputStream) {
		for (int i = 0; i < 100; i++) {
			try {
				soundDataBufferV2[i] = inputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return soundDataBufferV2;
	}

	/**
	 * int数组转成byte数组。
	 *
	 * @param ints
	 * @return
	 */
	private byte[] intForByte(int[] ints) {
		int size = ints.length;
		byte[] shorts = new byte[size];
		for (int i = 0; i < size; i++) {
			shorts[i] = (byte) ints[i];
		}
		return shorts;
	}

	private byte[] parseSoundV2(byte[] buffer) {
		return buffer;
	}

	private byte[] parseSoundV1(byte[] buffer) throws ParseException {
		if (buffer.length != 321) {
			throw new ParseException();
		}
		byte[] bytes = new byte[buffer.length - 1];
		for (int i = 0; i < (bytes.length - 1) / 5; i++) {
			bytes[i + 0] = ((byte) (buffer[i + 5] & 0xC0 + buffer[i] & 0x3F));
			bytes[i + 1] = ((byte) (buffer[i] & 0x03 + buffer[i + 5] & 0x30 + buffer[i + 1] & 0xF0));
			bytes[i + 2] = ((byte) (buffer[i + 1] & 0x0F + buffer[i + 5] & 0x0C + buffer[i + 2] & 0xC0));
			bytes[i + 3] = ((byte) (buffer[i + 2] & 0x3F + buffer[i + 5] & 0xC0));
			bytes[i + 4] = (byte) (buffer[i + 5] & 0x03 + buffer[i + 4]);
		}
		return bytes;
	}

	public void onEventMainThread(MonitorStartEvent event) {
		startMonitor = true;
		localRecordId = event.getLocalRecordId();
		try {
			final File voiceFile = FileUtil.getVoiceFile(context, localRecordId);
			if (voiceFile.createNewFile()) {
				fileOutputStream = new FileOutputStream(voiceFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onEventMainThread(MonitorTerminateEvent event) {
		final int reason = event.getEvent();
		switch (reason) {
			case MonitorTerminateEvent.EVENT_MANUAL_CANCEL_NOT_START:
				break;
			case MonitorTerminateEvent.EVENT_MANUAL_CANCEL_STARTED:
				break;
			case MonitorTerminateEvent.EVENT_MANUAL:
			case MonitorTerminateEvent.EVENT_AUTO:
			case MonitorTerminateEvent.EVENT_UNKNOWN:
				handleFileOnTerminate();
				break;
			default:
				break;
		}
		//关闭流
		try {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleFileOnTerminate() {
		startMonitor = false;
		localRecordId = LocalRecordIdUtil.getSavedId(context);
		File file = FileUtil.getVoiceFile(context, localRecordId);
		FileUtil.addFileHead(file);
	}
}
