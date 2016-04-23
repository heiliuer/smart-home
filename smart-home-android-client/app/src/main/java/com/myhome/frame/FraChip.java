package com.myhome.frame;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.InjectFragment;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnLongClick;
import com.myhome.frame.ServiceMain.OnDataIOListener;
import com.myhome.service.ComService;
import com.myhome.utils.MHjUtils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import shared.heiliuer.shared.Utils;

@ContentView(R.layout.fra_chip)
public class FraChip extends InjectFragment implements OnDataIOListener {
	private ServiceMain serviceMain;

	@ViewInject(R.id.txt_receive)
	private TextView txtReceive;
	@ViewInject(R.id.txt_time)
	private TextView txtTime;
	@ViewInject(R.id.txt_tasks)
	private TextView txtTasks;
	@ViewInject(R.id.edit_hours)
	private EditText editHours;
	@ViewInject(R.id.edit_minutes)
	private EditText editMinutes;
	@ViewInject(R.id.edit_seconds)
	private EditText editSeconds;
	@ViewInject(R.id.edit_cmd_type)
	private EditText editCmdType;
	@ViewInject(R.id.btn_add_task)
	private Button btnAddTask;
	private View handlerView;

	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initTimer();
		serviceMain = ServiceMain.getServiceMain();
		serviceMain.getComService().send(new byte[] { (byte) 0xaa, (byte) 0xff });
	}

	private final void initTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handlerView.post(handleOnTime);
			}
		}, 300, 400);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initViews(inflater, container, savedInstanceState);
	}

	private View initViews(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			super.onCreateView(inflater, container, savedInstanceState);
			ViewUtils.inject(this);
			handlerView = txtReceive;
		} else {
			((ViewGroup) rootView.getParent()).removeAllViews();
		}
		return rootView;
	}

	private Runnable handleOnTime = new Runnable() {

		@Override
		public void run() {
			Calendar cal = Calendar.getInstance();
			String time = cal.get(Calendar.HOUR_OF_DAY) + ":"
					+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
			txtTime.setText(time);
		}
	};

	private Integer formNumber(EditText et) throws NumberFormatException {
		String txt = et.getText().toString();
		if (txt == null || txt.trim().isEmpty()) {
			return 0;
		}
		return Integer.parseInt(txt);
	}

	@OnClick({ R.id.btn_add_task })
	public void btnAddTaskClick(View view) {
		try {
			Integer hours = formNumber(editHours);
			Integer minutes = formNumber(editMinutes);
			Integer seconds = formNumber(editSeconds);
			if (hours == 0 && minutes == 0 && seconds == 0) {
				seconds = 1;
			}
			final long when = ((hours * 60 + minutes) * 60 + seconds) * 1000;
			final Integer type = Integer.parseInt(editCmdType.getText()
					.toString().trim());
			int btnId = 0;
			switch (type) {
			case 0:
				btnId = R.id.send_p0_ff;
				break;
			case 1:
				btnId = R.id.send_p0_00;
				break;
			case 2:
				btnId = R.id.send_p1_ff;
				break;
			case 3:
				btnId = R.id.send_p1_00;
				break;
			case 4:
				btnId = R.id.send_p2_ff;
				break;
			case 5:
				btnId = R.id.send_p2_00;
				break;
			case 6:
				btnId = R.id.send_p3_ff;
				break;
			case 7:
				btnId = R.id.send_p3_00;
				break;
			}
			if (btnId != 0) {
				final int toClickBtn = btnId;
				handlerView.post(new Runnable() {
					@Override
					public void run() {
						txtTasks.append(DateFormat.format("yy年MM月dd日 H:mm:ss",
								System.currentTimeMillis() + when)
								+ "执行命令"
								+ type + "\n");
					}
				});
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						handlerView.post(new Runnable() {
							@Override
							public void run() {
								findViewById(toClickBtn).performClick();
							}
						});
					}
				}, when);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private Runnable clearReadDataTxt = new Runnable() {

		@Override
		public void run() {
			txtReceive.setTextColor(0xffffaaaa);
		}
	};

	@OnLongClick(R.id.txt_tasks)
	public boolean onTxtTasksLongClick(View v) {
		timer.cancel();
		timer.purge();
		initTimer();
		txtTasks.setText(null);
		return true;
	}

	@Override
	public void onReadData(final byte[] datas) {
		handlerView.post(new Runnable() {
			@Override
			public void run() {
				txtReceive.setText(MHjUtils.byteArrayToHexString(datas));
				txtReceive.setTextColor(0xffff4444);
				handlerView.removeCallbacks(clearReadDataTxt);
				handlerView.postDelayed(clearReadDataTxt, 1000);
			}
		});
		data=datas[1];
	}

	@Override
	public void onWriteData(byte[] datas) {
	}

	@OnClick({ R.id.send_p0_00, R.id.send_p0_ff, R.id.send_p1_00,
			R.id.send_p1_ff, R.id.send_p2_00, R.id.send_p2_ff, R.id.send_p3_00,
			R.id.send_p3_ff, R.id.txt_receive })
	public void onSendOnclick(View v) {
		ComService cs = serviceMain.getComService();
		switch (v.getId()) {
		case R.id.send_p0_00:
			cs.send(new byte[] { (byte) 0xa0, (byte) 0 });
			break;
		case R.id.send_p0_ff:
			cs.send(new byte[] { (byte) 0xa0, (byte) 0xff });
			break;
		case R.id.send_p1_00:
			cs.send(new byte[] { (byte) 0xa1, (byte) 0 });
			break;
		case R.id.send_p1_ff:
			cs.send(new byte[] { (byte) 0xa1, (byte) 0xff });
			break;
		case R.id.send_p2_00:
			cs.send(new byte[] { (byte) 0xa2, (byte) 0 });
			break;
		case R.id.send_p2_ff:
			cs.send(new byte[] { (byte) 0xa2, (byte) 0xff });
			break;
		case R.id.send_p3_00:
			cs.send(new byte[] { (byte) 0xa3, (byte) 0 });
			break;
		case R.id.send_p3_ff:
			cs.send(new byte[] { (byte) 0xa3, (byte) 0xff });
			break;
		case R.id.txt_receive:
			cs.send(new byte[] { (byte) 0xaa, (byte) 0xff });
			break;
		}
	}

	private int data = 0xff;

	@OnClick({ R.id.switch_p10, R.id.switch_p11, R.id.switch_p12,
			R.id.switch_p13 })
	public void onSwitchclick(View v) {
		ComService cs = serviceMain.getComService();
		int before = data;
		switch (v.getId()) {
		case R.id.switch_p10:
			int p10 = ((~data) & 1);
			data = data & 0xfe | p10;
			break;
		case R.id.switch_p11:
			int p11 = (~data) & 2;
			data = data & 0xfd | p11;
			break;
		case R.id.switch_p12:
			int p12 = (~data) & 4;
			data = data & 0xfb | p12;
			break;
		case R.id.switch_p13:
			int p13 = (~data) & 8;
			data = data & 0x07 | p13;
			break;
		}
		data &= 0xff;
		cs.send(new byte[] { (byte) 0xa1, (byte) (data + 0) });
		Utils.l("before:" + Integer.toBinaryString(before) + "data:"
				+ Integer.toBinaryString(data));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fra_chip, menu);
	}

	@Override
	public void onStart() {
		super.onStart();
		serviceMain.addOnDataIOListeners(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		serviceMain.removeOnDataIOListeners(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		timer = null;
	}

}
