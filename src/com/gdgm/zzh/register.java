package com.gdgm.zzh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class register extends Activity {
	private TextView return_login;
	private ImageView iv_register_return;
	private Button bt_zhuce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		return_login = (TextView) findViewById(R.id.return_login);
		iv_register_return = (ImageView) findViewById(R.id.register_return);
		bt_zhuce = (Button) findViewById(R.id.btn_zhuce);
		return_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register.this.finish();

			}
		});
	}

	public void register_return(View v) {
		finish();
	}

	public void zhuce(View v) {
		Toast.makeText(register.this, "此功能暂未实现", 1).show();
	}

}
