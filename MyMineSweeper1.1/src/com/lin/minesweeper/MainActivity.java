package com.lin.minesweeper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class MainActivity extends Activity {

	private int width;
	private int height;
	private int numOfMine;
	private int numOfUnmine;
	private int currentNumOfMine;
	private boolean[][] isMineArr;
	private boolean isFirstClick;
	private boolean isGameOver;
	
	private LinearLayout main;
	private Button mineNumButton;
	private MineButton[][] buttons;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor pEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		main=(LinearLayout)this.findViewById(R.id.main);
		
		this.width=5;
		this.height=10;
		this.preferences=this.getSharedPreferences("mine_sweeper", MODE_PRIVATE);
		this.pEditor=this.preferences.edit();
		this.numOfMine=this.preferences.getInt("mine_number", 10);
		this.buttons=new MineButton[height][width];

		this.gameStart();
	}
	
	public void gameStart(){
		main.removeAllViews();
		this.numOfUnmine=width*height-numOfMine;
		this.isFirstClick=true;
		this.currentNumOfMine=numOfMine;
		this.isGameOver=false;
		
		LinearLayout top=new LinearLayout(this);
		top.setGravity(Gravity.CENTER);
		LayoutParams topLp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		topLp.weight=1;
		main.addView(top,topLp);
		mineNumButton=new Button(this);
		mineNumButton.setText(this.currentNumOfMine+"");
		mineNumButton.setGravity(Gravity.CENTER);
		mineNumButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.this.gameStart();
			}
		});
		LayoutParams mineNumButtonLp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		top.addView(mineNumButton,mineNumButtonLp);
		
		for(int i=0;i<height;i++){
			LinearLayout row=new LinearLayout(this);
			for(int j=0;j<width;j++){
				this.buttons[i][j]=new MineButton(i,j);
				LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				lp.weight=1;
				row.addView(buttons[i][j],lp);
			}
			LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			lp.weight=1;
			main.addView(row,lp);
		}
	}
	
	public void gameOver(){
		this.isGameOver=true;
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++)
				this.buttons[i][j].click();
		this.mineNumButton.setText("X");
	}
	
	public void gamePassed(){
		System.out.println("game passed");
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++)
				if(!this.buttons[i][j].isClicked){
					this.buttons[i][j].isClicked=true;
					this.buttons[i][j].setIsLongClicked();
				}
		this.mineNumButton.setText("V");
	}
	
	private void setIsMineArr(int numOfMine,int y,int x) {
		this.isMineArr = new boolean[height][width];
		int allButtonNumber = this.width * this.height;
		while (numOfMine > 0) {
			int temp = (int) (Math.random() * allButtonNumber);
			if(temp==y*this.width+x)
				continue;
			boolean flag = this.isMineArr[temp / this.width][temp % this.width];
			if (flag != true) {
				this.isMineArr[temp / this.width][temp % this.width] = true;
				numOfMine--;
//				System.out.println(temp);
//				System.out.println(this.isMineArr[temp / this.width][temp
//						% this.width]);
			}
		}
	}
	
	class MineButton extends Button implements OnClickListener,OnLongClickListener {
		private int x;
		private int y;
		private boolean isMine;
		private boolean isClicked;
		private boolean isLongClicked;

		MineButton(int y, int x) {
			super(MainActivity.this);
			this.x = x;
			this.y = y;
			this.isClicked = false;
			this.isLongClicked=false;

			this.setBackgroundResource(R.drawable.button_default);
			this.setOnClickListener(this);
			this.setOnLongClickListener(this);
		}

		public boolean getIsClicked() {
			return this.isClicked;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(MainActivity.this.isFirstClick){
				MainActivity.this.setIsMineArr(MainActivity.this.numOfMine,y,x);
				MainActivity.this.isFirstClick=false;
				for(int i=0;i<height;i++)
					for(int j=0;j<width;j++)
						MainActivity.this.buttons[i][j].setIsMine(MainActivity.this.isMineArr[i][j]);
			}
			if(this.isLongClicked){
				return;
			}
			this.click();
		}
		
		public void setIsMine(boolean isMine){
			this.isMine=isMine;
		}
		
		public void click() {
			if(this.isClicked){
				return;
			}
			this.isClicked = true;
			int countMine = 0;
			if (buttons[y][x].isMine) {
				this.setBackgroundResource(R.drawable.button_mine);
				if(!MainActivity.this.isGameOver)
					MainActivity.this.gameOver();
				return;
			} else {
				this.setBackgroundResource(R.drawable.button_unmine);
				if (y - 1 >= 0 && x - 1 >= 0 && buttons[y - 1][x - 1].isMine) {
					countMine++;
				}
				if (y - 1 >= 0 && buttons[y - 1][x].isMine) {
					countMine++;
				}
				if (y - 1 >= 0 && x + 1 < width && buttons[y - 1][x + 1].isMine) {
					countMine++;
				}
				if (x - 1 >= 0 && buttons[y][x - 1].isMine) {
					countMine++;
				}
				if (x + 1 < width && buttons[y][x + 1].isMine) {
					countMine++;
				}
				if (y + 1 < height && x - 1 >= 0
						&& buttons[y + 1][x - 1].isMine) {
					countMine++;
				}
				if (y + 1 < height && buttons[y + 1][x].isMine) {
					countMine++;
				}
				if (y + 1 < height && x + 1 < width
						&& buttons[y + 1][x + 1].isMine) {
					countMine++;
				}
				if (countMine == 0) {
					if (y - 1 >= 0 && x - 1 >= 0
							&& !buttons[y - 1][x - 1].isClicked) {
						buttons[y - 1][x - 1].click();
					}
					if (y - 1 >= 0 && !buttons[y - 1][x].isClicked) {
						buttons[y - 1][x].click();
					}
					if (y - 1 >= 0 && x + 1 < width
							&& !buttons[y - 1][x + 1].isClicked) {
						buttons[y - 1][x + 1].click();
					}
					if (x - 1 >= 0 && !buttons[y][x - 1].isClicked) {
						buttons[y][x - 1].click();
					}
					if (x + 1 < width && !buttons[y][x + 1].isClicked) {
						buttons[y][x + 1].click();
					}
					if (y + 1 < height && x - 1 >= 0
							&& !buttons[y + 1][x - 1].isClicked) {
						buttons[y + 1][x - 1].click();
					}
					if (y + 1 < height && !buttons[y + 1][x].isClicked) {
						buttons[y + 1][x].click();
					}
					if (y + 1 < height && x + 1 < width
							&& !buttons[y + 1][x + 1].isClicked) {
						buttons[y + 1][x + 1].click();
					}
				} else {
					this.setText(countMine + "");
				}
				MainActivity.this.numOfUnmine--;
				if(MainActivity.this.numOfUnmine==0&&!MainActivity.this.isGameOver){
					MainActivity.this.gamePassed();
					return;
				}
			}
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			if(this.isClicked){
				return false;
			}
			if(this.isLongClicked){
				this.setBackgroundResource(R.drawable.button_default);
				MainActivity.this.currentNumOfMine++;
				this.isLongClicked=false;
			}
			else{
				this.setBackgroundResource(R.drawable.button_longclick);
				MainActivity.this.currentNumOfMine--;
				this.isLongClicked=true;
			}
			MainActivity.this.mineNumButton.setText(MainActivity.this.currentNumOfMine+"");
			return true;
		}
		
		public void setIsLongClicked(){
			if(!this.isLongClicked){
				this.isLongClicked=true;
				this.setBackgroundResource(R.drawable.button_longclick);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		menu.add(0, 1, 1, R.string.set_mine_number);
		menu.add(0,2,2,R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			Intent intent=new Intent(this,SetDialog.class);
			intent.putExtra("mine_number", this.numOfMine);
//			this.startActivity(intent);
			this.startActivityForResult(intent, 0);
		}
		
		if(item.getItemId()==2){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0 && resultCode==0){
			this.numOfMine=data.getIntExtra("mine_number", 10);
			this.pEditor.putInt("mine_number", this.numOfMine);
			this.pEditor.commit();
			this.gameStart();
		}
	}
	

}
