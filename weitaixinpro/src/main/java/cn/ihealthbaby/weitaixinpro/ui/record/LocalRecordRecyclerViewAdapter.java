package cn.ihealthbaby.weitaixinpro.ui.record;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.ui.monitor.LocalRecordPlayActivity;
import cn.ihealthbaby.weitaixinpro.ui.widget.ChooseUploadContentPopupWindow;
import cn.ihealthbaby.weitaixinpro.ui.widget.SoundUploadedEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/9/24 13:48.
 */
public class LocalRecordRecyclerViewAdapter extends RecyclerView.Adapter<LocalRecordRecyclerViewAdapter.ViewHolder> {
	private static final int UPLOAD_ALL = 1;
	private static final int UPLOAD_DATA = 2;
	private final Activity activity;
	private final List<Record> list;
	public HashMap<Integer, Boolean> deleteMap = new HashMap<Integer, Boolean>();
	@Bind(R.id.tv_begin)
	TextView tvBegin;
	@Bind(R.id.tv_name)
	TextView tvName;
	@Bind(R.id.tv_gestational_weeks)
	TextView tvDate;
	@Bind(R.id.tv_time)
	TextView tvTime;
	private boolean isDelFalg = false;

	public LocalRecordRecyclerViewAdapter(Activity activity, ArrayList<Record> list) {
		this.activity = activity;
		this.list = list;
		initMap(getItemCount());
		EventBus.getDefault().register(this);
	}

	public boolean isDelFalg() {
		return isDelFalg;
	}

	public void setIsDelFalg(boolean isDelFalg) {
		this.isDelFalg = isDelFalg;
	}

	public void initMap(int size) {
		deleteMap.clear();
		for (int i = 0; i < size; i++) {
			deleteMap.put(i, false);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.item_record, null);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Record record = list.get(position);
		holder.tvDate.setText(DateTimeTool.date2StrAndTime(record.getRecordStartTime()));
		holder.tvName.setText(record.getUserName());
		holder.tvDuration.setText(DateTimeTool.getTime2(record.getDuration() * 1000) + "");
		final int uploadState = record.getUploadState();
		switch (uploadState) {
			case Record.UPLOAD_STATE_LOCAL:
			case Record.UPLOAD_STATE_UPLOADING:
				holder.tvUploadStatus.setText("需上传");
				holder.tvUploadStatus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//显示对话框,用户选择上传曲线还是全部上传
						ChooseUploadContentPopupWindow chooseUploadContentPopupWindow = new ChooseUploadContentPopupWindow(activity, record, position);
						chooseUploadContentPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
					}
				});
				break;
			case Record.UPLOAD_STATE_CLOUD:
				holder.tvUploadStatus.setText("已上传");
				holder.tvUploadStatus.setOnClickListener(null);
				break;
			default:
				break;
		}
		if (isDelFalg()) {
			holder.checkboxDelete.setVisibility(View.VISIBLE);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteMap.put(position, !deleteMap.get(position));
					if (deleteMap.get(position)) {
						holder.checkboxDelete.setSelected(true);
					} else {
						holder.checkboxDelete.setSelected(false);
					}
				}
			});
			if (!deleteMap.isEmpty()) {
				if (deleteMap.get(position)) {
					holder.checkboxDelete.setSelected(true);
				} else {
					holder.checkboxDelete.setSelected(false);
				}
			}
		} else {
			holder.checkboxDelete.setVisibility(View.GONE);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, LocalRecordPlayActivity.class);
					intent.putExtra(Constants.INTENT_LOCAL_RECORD_ID, record.getLocalRecordId());
					activity.startActivity(intent);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public void doDeleteAction() {
		Set<Map.Entry<Integer, Boolean>> entries = deleteMap.entrySet();
		final ArrayList<Record> delRecords = new ArrayList<Record>();
		for (Map.Entry<Integer, Boolean> entry : entries) {
			if (entry.getValue()) {
				//删除
				delRecords.add(this.list.get(entry.getKey()));
			}
		}
//		删除文件
		new Thread() {
			@Override
			public void run() {
				super.run();
				for (Record record : delRecords) {
					final String localRecordId = record.getLocalRecordId();
					final File voiceFile = FileUtil.getVoiceFile(activity, localRecordId);
					voiceFile.delete();
				}
			}
		}.start();
		this.list.removeAll(delRecords);
		try {
			RecordBusinessDao.getInstance(activity).deleteRecords(delRecords);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initMap(getItemCount());
		notifyDataSetChanged();
	}

	public void onEventMainThread(SoundUploadedEvent event) {
		int position = event.getPosition();
		notifyItemChanged(position);
	}

	public void notifyAllDataSetChanged() {
		notifyDataSetChanged();
		initMap(list.size());
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvUploadStatus;
		public TextView tvName;
		public TextView tvDuration;
		public TextView tvDate;
		public CheckBox checkboxDelete;

		public ViewHolder(View itemView) {
			super(itemView);
			tvUploadStatus = (TextView) itemView.findViewById(R.id.tv_upload_status);
			tvName = (TextView) itemView.findViewById(R.id.tv_name);
			tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
			tvDate = (TextView) itemView.findViewById(R.id.tv_date);
			checkboxDelete = (CheckBox) itemView.findViewById(R.id.checkboxDelete);
		}
	}
}
