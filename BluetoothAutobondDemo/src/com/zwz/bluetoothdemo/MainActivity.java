package com.zwz.bluetoothdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1000;
	
	private Context context = null;
	private Button startButton = null;
	private Button stopButton = null;
	private Button sendButton = null;
	private EditText sendEditText = null;
	private EditText readEditText = null;
	private Handler handler = null;
	
	private BluetoothService bluetoothService = null;
	private BluetoothAdapter bluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initCtrl();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
    	case 0:
    		
    		break;
		case 1:
			
			break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode == REQUEST_ENABLE_BT){
    		if(resultCode == RESULT_OK){
    			new Thread(startRunnable).start();
    		}
    	}
    }

	private void initCtrl() {
		context = this;
		startButton = (Button) findViewById(R.id.btn_start);
		stopButton = (Button) findViewById(R.id.btn_stop);
		sendButton = (Button) findViewById(R.id.btn_send);
		sendEditText = (EditText) findViewById(R.id.et_send_message);
		readEditText = (EditText) findViewById(R.id.et_read_message);
		
		startButton.setOnClickListener(myOnClickListener);
		stopButton.setOnClickListener(myOnClickListener);
		sendButton.setOnClickListener(myOnClickListener);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case BluetoothService.MESSAGE_READ:
					readEditText.append(msg.obj.toString() + "\n");
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		bluetoothService = BluetoothService.getInstance(handler);
		bluetoothAdapter = bluetoothService.getBluetoothAdapter();
	}
	
	private OnClickListener myOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start:
				OpenBluetoothDevice();
				break;
			case R.id.btn_stop:
				try {
					bluetoothService.closeBluetoothSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.btn_send:
				try {
					bluetoothService.sendData(sendEditText.getText().toString().trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			
		}
	};
	
	 private void OpenBluetoothDevice() {
			if(!bluetoothAdapter.isEnabled()){	//蓝牙未开启，则开启蓝牙
				new AlertDialog.Builder(context)
				.setTitle("警告") 
				.setMessage("蓝牙功能没有打开，可能导致部分功能无法使用。\n是否打开蓝牙？")
			 	.setPositiveButton("确定", new OpenBluetoothOnClickListener())
			 	.setNegativeButton("取消", null)
			 	.show();
			} else {
				new Thread(startRunnable).start();
			}
		}
	 
	class OpenBluetoothOnClickListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT); 
		}
	}
	
	private Runnable startRunnable = new Runnable() {
		
		@Override
		public void run() {
			bluetoothService.setRemoteDeviceAddress("BE:03:5D:C2:65:3A");
			bluetoothService.setPin("1234");
			int res;
			try {
				res = bluetoothService.autoBondBluetooth();
				if (res == 0) {
					res = bluetoothService.connectBluetoothSocket();
					if (res != 0) {
						bluetoothService.closeBluetoothSocket();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
