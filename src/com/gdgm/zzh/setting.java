package com.gdgm.zzh;

import java.security.PublicKey;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class setting extends Activity {
	private Button save;
	private EditText zigbeeip, zigbeeport, serverip, serverport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		zigbeeip = (EditText) findViewById(R.id.et_zigbeeip);
		zigbeeport = (EditText) findViewById(R.id.et_zigbeeport);
		serverip = (EditText) findViewById(R.id.et_serverip);
		serverport = (EditText) findViewById(R.id.et_serverport);
		save = (Button) findViewById(R.id.bt_saveconfig);
		readinfo();
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String zigbeeiptext = zigbeeip.getText().toString();
				int zigbeeporttext = Integer.parseInt(zigbeeport.getText().toString());
				String serveriptext = serverip.getText().toString();
				int serverporttext = Integer.parseInt(serverport.getText().toString());
				// ���û�������棬�ѱ���������ļ�д�����ص�sharepreference��
				SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
				Editor ed = sp.edit();
				ed.putString("zigbeeip", zigbeeiptext);
				ed.putInt("zigbeeport", zigbeeporttext);
				ed.putString("serverip", serveriptext);
				ed.putInt("serverport", serverporttext);
				ed.commit();
				Toast.makeText(setting.this, "����ɹ�", 0).show();
			}
		});

	}

	public void readinfo() {
		SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
		// ��������������ļ���Ϣд�뵽���ý����ip��Ϣ��

		zigbeeip.setText(sp.getString("zigbeeip", "192.168.4.1"));
		zigbeeport.setText(sp.getInt("zigbeeport", 5000) + "");

		serverport.setText(sp.getInt("serverport", 80) + "");
		serverip.setText(sp.getString("serverip", "10.0.2.2"));
	}
}
