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
	private ArrayList<Map<String, String>> bluetoothtList = new ArrayList<Map<String, String>>();// �����õ������б�
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
		initlistView(); // ��ʼ��listview
		initBlueTooth();
		initLedButton(); // ��ʼ�����ư�ť����

	}

	//��ȡ����������Ϣ
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
		zigbeeConnectIP = sp.getString("zigbeeip", "192.168.4.1");
		zigbeeConnectPort = sp.getInt("zigbeeport", 5000);
		System.out.println(zigbeeConnectIP + ":" + zigbeeConnectPort);
	}

	// ��������ť
	public void openBluetooth(View view) {
		try {
			mBluetoothAdapter.enable(); // ������
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "�����豸������", 0).show();
			e.printStackTrace();
		}
	}

	// ɨ��������ť
	public void scanBluetooth(View view) {
		try {
			bluetoothtList.clear(); // ���listview������
			// ��ʼɨ�������豸
			mBluetoothAdapter.startDiscovery();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "�����豸������", 0).show();
			e.printStackTrace();

		}
	}

	// ֹͣɨ��������ť
	public void stopScan(View view) {
		try {
			// ֹͣɨ�������豸
			mBluetoothAdapter.cancelDiscovery();
			Toast.makeText(MainActivity.this, "��ֹͣɨ��", 0).show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "�����豸������", 0).show();
			e.printStackTrace();
		}
	}

	// �ر�������ť
	public void closeBluetooth(View view) {
		try {
			mBluetoothAdapter.disable(); // �ر�����
			Toast.makeText(MainActivity.this, "�����ѹر�", 0).show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "�����豸������", 0).show();
			e.printStackTrace();
		}
	}

	// ��¼��ť
	public void login(View view) {
		Intent it = new Intent(MainActivity.this, login.class);
		startActivity(it);
	}

	// ���ð�ť,�������popumenu
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
					new AlertDialog.Builder(MainActivity.this).setTitle("����").setMessage("1342101���Ӻ������ܼҾ߱�ҵ���")
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

	// ����zigbee��ť
	public void connect(View v) {
		Toast.makeText(MainActivity.this, "����������...", 0).show();
		Thread t = new Thread(new zigbeeConnectThread());// ��������zigbee�߳�
		t.start();
	}

	// ��ʼ��listview
	private void initlistView() {

		lv = (ListView) findViewById(R.id.lv);
		myadapter = new lvadapter();
		lv.setAdapter(myadapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			// listview����¼����������������
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Toast.makeText(getApplicationContext(), "��ʼ����", 0).show();
					new Thread(new Runnable() {
						public void run() {
							try {
								btsocket = device.createRfcommSocketToServiceRecord(
										UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")); // ͨ��������UUID�����������ڷ���
								btsocket.connect();
								os = btsocket.getOutputStream();

								Looper.prepare(); // ���̲߳���toast����Ҫ��looper�߳�toast
								Toast.makeText(getApplicationContext(), "�������", 0).show();
								Looper.loop();

							} catch (Exception e) {
								Looper.prepare(); // ���̲߳���toast����Ҫ��looper�߳�toast
								Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
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

	// ��ʼ�������豸
	private void initBlueTooth() {
		try {
			// ��ȡ��������������
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "��������û������������", 0).show();
		}

		// ��������㲥������
		IntentFilter filter = new IntentFilter();
		// ��ʼɨ��Ĺ㲥
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		// ɨ����ɵĹ㲥
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		// ����һ�����õ��豸�Ĺ㲥
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		mBluetoothReceiver = new BluetoothReceiver();
		// ע�����
		registerReceiver(mBluetoothReceiver, filter);

	}

	/** ����һ���㲥�����߽��������㲥�¼� */
	class BluetoothReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Toast.makeText(MainActivity.this, "��ʼɨ������", 0).show();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(MainActivity.this, "ɨ���������", 0).show();
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// �õ����ֵ������豸
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(MainActivity.this, "�����������豸 " + device.getName() + " : " + device.getAddress(), 0)
						.show();
				Map<String, String> btmap = new HashMap<String, String>(); // ����������ַ���������ֵļ���
				btmap.put("btname", device.getName());
				btmap.put("btaddress", device.getAddress());
				bluetoothtList.add(btmap);
				myadapter.notifyDataSetChanged();// ������Ϣʱ֪ͨlistview����

			}

		}

	}

	// ��ʼ�����ư�ť����
	private void initLedButton() {
		// led1��ť��0199020299��������P2�ڵĸߵ�ƽ��0199020199��������P2�ڵĵ͵�ƽ
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
						tv_led1.setText("��1:��");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "��������ʧ�ܣ�ȷ���Ƿ�����������", 0).show();
					}

				} else {
					try {
						os.write(0x01);
						os.write(0x99);
						os.write(0x02);
						os.write(0x01);
						os.write(0x99);
						os.flush();
						tv_led1.setText("��1:��");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "��������ʧ�ܣ�ȷ���Ƿ�����������", 0).show();
					}
				}

			}
		});

		// led2��ť��0199030299��������P3�ڵĸߵ�ƽ��0199030199��������P3�ڵĵ͵�ƽ
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
						tv_led2.setText("��2:��");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "��������ʧ�ܣ�ȷ���Ƿ�����������", 0).show();
					}

				} else {

					try {

						os.write(0x01);
						os.write(0x99);
						os.write(0x03);
						os.write(0x01);
						os.write(0x99);
						os.flush();
						tv_led2.setText("��2:��");
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "��������ʧ�ܣ�ȷ���Ƿ�����������", 0).show();
					}

				}

			}
		});

		// led3��ť
		tg_led3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tg_led3.isChecked()) {
					try {
						if (out != null) { // �����ȡ����������������������û��ȡ��������������쳣��
							out.write("ESPGLED1".getBytes());
							tv_led3.setText("��3:��");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "����ʧ��", 0).show();
					}
				} else {
					try {
						if (out != null) {
							out.write("ESPKLED1".getBytes());
							tv_led3.setText("��3:��");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "����ʧ��", 0).show();
					}
				}

			}
		});

		// led4��ť
		tg_led4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tg_led4.isChecked()) {
					try {
						if (out != null) {

							out.write("ESPGLED2".getBytes());
							tv_led4.setText("��4:��");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "����ʧ��", 0).show();
					}
				} else {
					try {
						if (out != null) {
							out.write("ESPKLED2".getBytes());
							tv_led4.setText("��4:��");
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, "����ʧ��", 0).show();
					}
				}

			}
		});

	}

	// ����listview������
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
				// �Ѳ����ļ�����view����
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

	// Activity����ʱȡ���㲥������
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBluetoothReceiver); // ȡ��ע��㲥������
		mBluetoothReceiver = null; // �ѹ㲥��������Ϊ��
		try {
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// zigbee TCP�����߳����ӵ�zigbee��������
	class zigbeeConnectThread implements Runnable {

		@Override
		public void run() {
			try {
				zigbeesocket = new Socket(InetAddress.getByName(zigbeeConnectIP), zigbeeConnectPort);
				out = zigbeesocket.getOutputStream();
				Looper.prepare();
				Toast.makeText(MainActivity.this, "���ӳɹ�", 0).show();
				Looper.loop();

			} catch (Exception e) {
				Log.d("ec", e.getStackTrace().toString());
				Looper.prepare();
				Toast.makeText(MainActivity.this, "����ʧ�ܣ�ȷ����������zigbee����wifi", 0).show();
				Looper.loop();
			}

		}

	}

}
