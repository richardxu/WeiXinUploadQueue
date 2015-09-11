package cn.ihealthbaby.weitaixin.ui.monitor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AskPurposeType;
import cn.ihealthbaby.client.model.CommonConfig;
import cn.ihealthbaby.client.model.FeelingType;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class GuardianStateActivity extends BaseActivity {
	public List<AskPurposeType> askPurposetypes;
	public List<FeelingType> feelingTypes;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView title_text;
	@Bind(R.id.function)
	TextView function;
	//
	@Bind(R.id.flGuardianPurpose)
	FrameLayout flGuardianPurpose;
	@Bind(R.id.flGuardianMood)
	FrameLayout flGuardianMood;
	@Bind(R.id.ivFooter)
	TextView ivFooter;
	@Bind(R.id.tvGuardianPurposeText)
	TextView tvGuardianPurposeText;
	@Bind(R.id.tvGuardianMoodText)
	TextView tvGuardianMoodText;
	private MyPoPoWinGuardian myPoPoWinGuardian;
	private MyPoPoWinGuardian myPoPoWinGuardian1;
	private String purposeText;
	private String moodText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guardian_state);
		ButterKnife.bind(this);
		title_text.setText("监护状态");
		final CustomDialog customDialog = new CustomDialog();
		Dialog dialog = customDialog.createDialog1(this, "加载中...");
		dialog.show();
		ApiManager.getInstance().commonApi.getCommonConfig(new HttpClientAdapter.Callback<CommonConfig>() {
			@Override
			public void call(Result<CommonConfig> t) {
				if (t.isSuccess()) {
					CommonConfig data = t.getData();
					askPurposetypes = data.getAskPurposetypes();
					feelingTypes = data.getFeelingTypes();
				} else {
					ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
				}
				customDialog.dismiss();
			}
		}, getRequestTag());
	}

	@OnClick(R.id.back)
	public void onBack() {
		this.finish();
	}

	@OnClick(R.id.flGuardianPurpose)
	public void GuardianPurpose(FrameLayout flGuardianPurpose) {
		if (askPurposetypes == null) {
			ToastUtil.show(getApplicationContext(), "没数据~~~");
			return;
		}
		myPoPoWinGuardian = new MyPoPoWinGuardian(this);
		myPoPoWinGuardian.initPurposetData(askPurposetypes);
		myPoPoWinGuardian.showAtLocation(flGuardianPurpose);
		myPoPoWinGuardian.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (myPoPoWinGuardian.indexPosition == -1) {
					purposeText = null;
				} else {
					purposeText = askPurposetypes.get(myPoPoWinGuardian.indexPosition).getValue();
					tvGuardianPurposeText.setText(purposeText + "");
				}
			}
		});
	}

	@OnClick(R.id.flGuardianMood)
	public void GuardianMood(FrameLayout flGuardianMood) {
		if (feelingTypes == null) {
			ToastUtil.show(getApplicationContext(), "没数据~~~");
			return;
		}
		myPoPoWinGuardian1 = new MyPoPoWinGuardian(this);
		myPoPoWinGuardian1.initFeelingTypeData(feelingTypes);
		myPoPoWinGuardian1.showAtLocation(flGuardianMood);
		myPoPoWinGuardian1.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (myPoPoWinGuardian1.indexPosition == -1) {
					moodText=null;
				} else {
					moodText = feelingTypes.get(myPoPoWinGuardian1.indexPosition).getValue();
					tvGuardianMoodText.setText(moodText + "");
				}
			}
		});
	}


	//100全空    200第一个空   300第一个空   400都不空
	public int isNullText(){
		if (TextUtils.isEmpty(purposeText)&&TextUtils.isEmpty(moodText)) {
			return 100;
		}
		if (TextUtils.isEmpty(purposeText)) {
			return 200;
		}
		if (TextUtils.isEmpty(moodText)) {
			return 300;
		}
		return 400;
	}



	@OnClick(R.id.ivFooter)
	public void Footer() {
		final CustomDialog customDialog = new CustomDialog();
		Dialog dialog = customDialog.createDialog1(this, "加载中...");
		dialog.show();
//        ApiManager.getInstance().adviceApi.
		Intent intent = getIntent();
		intent.setClass(getApplicationContext(), CurvePlayActivity.class);
		intent.putExtra("AskPurposeType", askPurposetypes.get(myPoPoWinGuardian.indexPosition).getValue());
		intent.putExtra("FeelingType", feelingTypes.get(myPoPoWinGuardian1.indexPosition).getValue());
		startActivity(intent);
	}
}








