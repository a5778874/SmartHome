package com.gdgm.zzh;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private Button bt_openBT, bt_scanBT, bt_stopscan, bt_closeBT, bt_connectzigbee;
	private TextView tv_led1, tv_led2, tv_led3, tv_led4;
	private ToggleButton tg_led1, tg_led2, tg_led3, tg_led4;
	private ImageView iv_setting, iv_login;
	private ListView lv;
	private lvadapter myadapter;
	private BluetoothDevice device;
	private BluetoothSocket btsocket;
	private OutputStream os = null;
	private ArrayList<Map<String, String>> bluetoothtList = new ArrayList<Map<String, String>>();// 保存获得的蓝牙列表
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothReceiver mBluetoothReceiver;
	public Socket zigbeesocket;
	public OutputStream out = null;
	public String zigbeeConnectIP;
	public int zigbeeConnectPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv_setting = (ImageView) findViewById(R.id.iv_setting);
		iv_login = (ImageView) findViewById(R.id.iv_login);
		tv_led1 = (TextView) findViewById(R.id.tv_led1);
		tv_led2 = (TextView) findViewById(R.id.tv_led2);
		tv_led3 = (TextView) findViewById(R.id.tv_led3);
		tv_led4 = (TextView) findViewById(R.id.tv_led4);
		tg_led1 = (ToggleButton) findViewById(R.id.tg_led1);
		tg_led2 = (ToggleButton) findViewById(R.id.tg_led2);
		tg_led3 = (ToggleButton) findViewById(R.id.tg_led3);
		tg_led4 = (ToggleButton) findViewById(R.id.tg_led4);
		bt_openBT = (Button) findViewById(R.id.bt_openBT);
		bt_scanBT = (Button) findViewById(R.id.bt_scanBT);
		bt_stopscan = (Button) findViewById(R.id.bt_StopScan);
		bt_closeBT = (Button) findViewById(R.id.bt_closeBT);
		bt_connectzigbee = (Button) findViewById(R.id.bt_zigbeeconnect);
		initlistView(); // 初始化listview
		initBlueTooth();
		initLedButton(); // 初始化控制按钮功能

	}

	//读取本地配置信息
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
		zigbeeConnectIP = sp.getString("zigbeeip", "192.168.4.1");
		zigbeeConnectPort = sp.getInt("zigbeeport", 5000);
		System.out.println(zigbeeConnectIP + ":" + zigbeeConnectPort);
	}

	// 打开蓝牙按钮
	public void openBluetooth(View view) {
		try {
			mBluetoothAdapter.enable(); // 打开蓝牙
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "蓝牙设备有问题", 0).show();
			e.printStackTrace();
		}
	}

	// 扫描蓝牙按钮
	public void scanBluetooth(View view) {
		try {
			bluetoothtList.clear(); // 清空listview的数据
			// 开始扫描蓝牙设备
			mBluetoothAdapter.startDiscovery();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "蓝牙设备有问题", 0).show();
			e.printStackTrace();

		}
	}

	// 停止扫描蓝牙按钮
	public void stopScan(View view) {
		try {
			// 停止扫描蓝牙设备
			mBluetoothAdapter.cancelDiscovery();
			Toast.makeText(MainActivity.this, "已停止扫描", 0).show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "蓝牙设备有问题", 0).show();
			e.printStackTrace();
		}
	}

	// 关闭蓝牙按钮
	public void closeBluetooth(View view) {
		try {
			mBluetoothAdapter.disable(); // 关闭蓝牙
			Toast.makeText(MainActivity.this, "蓝牙已关闭", 0).show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "蓝牙设备有问题", 0).show();
			e.printStackTrace();
		}
	}

	// 登录按钮
	public void login(View view) {
		Intent it = new Intent(MainActivity.this, login.class);
		startActivity(it);
	}

	// 设置按钮,点击弹出popumenu
	public void setting(View view) {
		PopupMenu menu = new PopupMenu(MainActivity.this, iv_setting);
		menu.getMenuInflater().inflate(R.menu.settingmenu, menu.getMenu());
		menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menuset:
					Intent intent = new Intent(MainActivity.this, setting.class);
					startActivity(intent);
					break;
				case R.id.menuabout:
					new AlertDialog.Builder(MainActivity.this).setTitle("关于").setMessage("1342101钟子豪，智能家具毕业设计")
							.setPositiveButton("OK", null).show();
					break;
				default:
					break;
				}
				return true;
			}
		});
		menu.show();
	}

	// 连接zigbee按钮
	public void connect(View v) {
		Toast.makeText(MainActivity.this, "正在连接中...", 0).show();
		Thread t = new Thread(new zigbeeConnectThread());// 开启连接zigbee线程
		t.start();
	}

	// 初始化listview
	private void initlistView() {

		lv = (ListView) findViewById(R.id.lv);
		myadapter = new lvadapter();
		lv.setAdapter(myadapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			// listview点击事件，点击后连接蓝牙
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Toast.makeText(getApplicationContext(), "开始连接", 0).show();
					new Thread(new Runnable() {
						public void run() {
							try {
								btsocket = device.createRfcommSocketToServiceRecord(
										UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")); // 通过蓝牙的UUID连接蓝牙串口服务
								btsocket.connect();
								os = btsocket.getOutputStream();

								Looper.prepare(); // 子线程不能toast，需要用looper线程toast
								Toast.makeText(getApplicationContext(), "连接完成", 0).show();
								Looper.loop();

							} catch (Exception e) {
								Looper.prepare(); // 子线程不能toast，需要用looper线程toast
								Toast.makeText(getApplicationContext(), "连接失败", 0).show();
								Looper.loop();
							}

						}
					}).start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	// 初始化蓝牙设备
	private void initBlueTooth() {
		try {
			// 获取本地蓝牙适配器
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "本机可能没有蓝牙适配器", 0).show();
		}

		// 添加蓝牙广播接受者
		IntentFilter filter = new IntentFilter();
		// 开始扫描的广播
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		// 扫描完成的广播
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		// 发现一个可用的设备的广播
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		mBluetoothReceiver = new BluetoothReceiver();
		// 注册监听
		registerReceiver(mBluetoothReceiver, filter);

	}

	/** 定义一个广播接受者接收蓝牙广播事件 */
	class BluetoothReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Toast.makeText(MainActivity.this, "开始扫描蓝牙", 0).show();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(MainActivity.this, "扫描蓝牙完成", 0).show();
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 得到发现的蓝牙设备
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(MainActivity.this, "发现了蓝牙设备 " + device.getName() + " : " + device.getAddress(), 0)
						.show();
				Map<String, String> btmap = new HashMap<String, String>(); // 保存蓝牙地址和蓝牙名字的集合
				btmap.put("btname", device.getName());
				btmap.put("btaddress", device.getAddress());
				bluetoothtList.add(btmap);
				myadapter.notifyDataSetChanged();// 有新消息时通知listview更新

			}

		}

	}

	// 初始化控制按钮功能
	private void initLedButton() {
		// led1按钮，0199020299控制蓝牙P2口的高电平，0199020199控制蓝牙P2口的低电平
		tg_led1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tg_led1.isChecked()) {
					try {

						os.write(0x01);
						os.write(0x99);
						os.write(0x02);
						os.write(0x02);
						os.write(0x99);
						os.flush();
						tv_led1.setText("灯1:开");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "发送命令失败，确认是否连接上蓝牙", 0).show();
					}

				} else {
					try {
						os.write(0x01);
						os.write(0x99);
						os.write(0x02);
						os.write(0x01);
						os.write(0x99);
						os.flush();
						tv_led1.setText("灯1:关");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "发送命令失败，确认是否连接上蓝牙", 0).show();
					}
				}

			}
		});

		// led2按钮，0199030299控制蓝牙P3口的高电平，0199030199控制蓝牙P3口的低电平
		tg_led2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tg_led2.isChecked()) {
					try {

						os.write(0x01);
						os.write(0x99);
						os.write(0x03);
						os.write(0x02);
						os.write(0x99);
						os.flush();
						tv_led2.setText("灯2:开");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "发送命令失败，确认是否连接上蓝牙", 0).show();
					}

				} else {

					try {

						os.write(0x01);
						os.write(0x99);
						os.write(0x03);
						os.write(0x01);
						os.write(0x99);
						os.flush();
						tv_led2.setText("灯2:关");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "发送命令失败，确认是否连接上蓝牙", 0).show();
					}

				}

			}
		});

		// led3按钮
		tg_led3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tg_led3.isChecked()) {
					try {
						if (out != null) { // 如果获取到输出流，正常操作！如果没获取到输出流，则抛异常！
							out.write("ESPGLED1".getBytes());
							tv_led3.setText("灯3:开");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "操作失败", 0).show();
					}
				} else {
					try {
						if (out != null) {
							out.write("ESPKLED1".getBytes());
							tv_led3.setText("灯3:关");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "操作失败", 0).show();
					}
				}

			}
		});

		// led4按钮
		tg_led4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tg_led4.isChecked()) {
					try {
						if (out != null) {

							out.write("ESPGLED2".getBytes());
							tv_led4.setText("灯4:开");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "操作失败", 0).show();
					}
				} else {
					try {
						if (out != null) {
							out.write("ESPKLED2".getBytes());
							tv_led4.setText("灯4:关");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "操作失败", 0).show();
					}
				}

			}
		});

	}

	// 定义listview适配器
	class lvadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bluetoothtList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				// 把布局文件填充成view对象
				view = getLayoutInflater().inflate(R.layout.listview_item, null);

			} else {
				view = convertView;
			}

			TextView btname = (TextView) view.findViewById(R.id.tv_btname);
			TextView btaddress = (TextView) view.findViewById(R.id.tv_btaddress);
			Map map = bluetoothtList.get(position);
			btname.setText(map.get("btname").toString());
			btaddress.setText(map.get("btaddress").toString());
			return view;
		}
	}

	// Activity销毁时取消广播接受者
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBluetoothReceiver); // 取消注册广播接受者
		mBluetoothReceiver = null; // 把广播接受者置为空
		try {
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// zigbee TCP连接线程连接到zigbee服务器端
	class zigbeeConnectThread implements Runnable {

		@Override
		public void run() {
			try {
				zigbeesocket = new Socket(InetAddress.getByName(zigbeeConnectIP), zigbeeConnectPort);
				out = zigbeesocket.getOutputStream();
				Looper.prepare();
				Toast.makeText(MainActivity.this, "连接成功", 0).show();
				Looper.loop();

			} catch (Exception e) {
				Log.d("ec", e.getStackTrace().toString());
				Looper.prepare();
				Toast.makeText(MainActivity.this, "连接失败，确保先连接上zigbee网关wifi", 0).show();
				Looper.loop();
			}

		}

	}

}
