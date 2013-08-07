package com.lin.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;

public class SetDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		final EditText et=(EditText)findViewById(R.id.setting_text);
		final Intent intent=this.getIntent();
		et.setText(intent.getIntExtra("mine_number", 10)+"");
		Button bn=(Button)findViewById(R.id.setting_button);
		bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(et.getText().toString());
//				getIntent().putExtra("mine_number", Integer.parseInt(et.getText().toString()));
				Intent intent=new Intent().putExtra("mine_number", Integer.parseInt(et.getText().toString()));
				SetDialog.this.setResult(0,intent);
//				System.out.println("¸¸´°¿Ú£¿"+SetDialog.this.getParent());
				SetDialog.this.finish();
			}
			
		});
	}
	
	
}
