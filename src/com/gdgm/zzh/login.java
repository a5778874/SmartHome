package com.gdgm.zzh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class login extends Activity {
	private TextView tv_zhuce;
	private ImageView iv_login_return;
	private Button bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		tv_zhuce = (TextView) findViewById(R.id.login_zhuce);
		iv_login_return = (ImageView) findViewById(R.id.login_return);
		bt_login = (Button) findViewById(R.id.btn_login);
		
		//ע�ᰴť
		tv_zhuce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(login.this, register.class);
				startActivity(it);

			}
		});
	}

	//���ذ�ť
	public void login_return(View v) {
		finish();
	}
	
	//��¼��ť
	public void login(View v) {
		Toast.makeText(login.this, "�˹�����δʵ��", 1).show();
	}

}
