package com.example.bluetoothprinterdemo;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	protected static final int REQUEST_ENABLE_BT = 1000;
	private Context mContext = null;
	private Button mButton = null;
	private Button mCancleButton = null;
	
	private BluetoothDevice device;
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket  bluetoothSocket = null;
	private ArrayList<String> mNewDevicesArrayAdapter = null;
	
	private BluetoothService mBluetoothService = null;
	private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        InitCtrl();
        
    }

    private void InitCtrl() {
    	mContext = this;
    	mHandler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			switch (msg.what) {
				case BluetoothService.MESSAGE_READ:
					//���յ���������
					break;

				default:
					break;
				}
    			super.handleMessage(msg);
    		}
    	};
    	mBluetoothService = BluetoothService.getInstance(mHandler);
    	mBluetoothAdapter = mBluetoothService.getBluetoothAdapter();
    	mButton = (Button) findViewById(R.id.btn);
    	mButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View arg0) {
    			OpenBluetoothDevice();
    		}
    	});
    	mCancleButton = (Button) findViewById(R.id.btn_cancle);
    	mCancleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					bluetoothSocket.close();
					boolean res1 = cancelBondProcess(device.getClass(), device);
					boolean res = removeBond(device.getClass(), device);
//					int i = -1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        mNewDevicesArrayAdapter = new ArrayList<String>();
        
        //ע�ᣬ��һ���豸������ʱ����onReceive  
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);  
                this.registerReceiver(mReceiver, filter);  
          
        //���������������onReceive  
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  
                this.registerReceiver(mReceiver, filter);
	}
    
    private void OpenBluetoothDevice() {
		if(!mBluetoothAdapter.isEnabled()){	//����δ��������������
			new AlertDialog.Builder(mContext)
			.setTitle("����") 
			.setMessage("��������û�д򿪣����ܵ��²��ֹ����޷�ʹ�á�\n�Ƿ��������")
		 	.setPositiveButton("ȷ��", new OpenBluetoothOnClickListener())
		 	.setNegativeButton("ȡ��", null)
		 	.show();
		} else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
//					mAdapter.setRemoteDeviceAddress("BE:03:5D:C2:65:3A");
//					mAdapter.setRemoteDeviceAddress("7C:61:93:43:1D:13");
//					mAdapter.setPin("1234");
//					int res = mAdapter.AutoBondBluetooth();
//					if (res == 0) {
//						mAdapter.ConnectBluetoothSocket();
//						mAdapter.CloseBluetoothSocket();
//					}
//					device = mBluetoothAdapter.getRemoteDevice("BE:03:5D:C2:65:3A");	
					device = mBluetoothAdapter.getRemoteDevice("7C:61:93:43:1D:13");	
	    			if(device.getBondState() != BluetoothDevice.BOND_BONDED){//�жϸ�����ַ�µ�device�Ƿ��Ѿ����  
	    				try{  
	    					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");         //UUID��ʾ���ڷ���
	    					boolean res2 = setPin(device.getClass(), device, "1234");//����pinֵ  
//	    					boolean res = autoBond(device.getClass(), device, "1234");//����pinֵ  
	    					boolean res1 = createBond(device.getClass(), device);  
	    					boolean res3= cancelPairingUserInput(device.getClass(), device);  
    		             	
	    					//�ͻ��˽���һ��BluetoothSocket��ȥ����Զ�������豸  
	    					bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);  
	    		            bluetoothSocket.connect();//��������  
	    		            InputStream inputStream = bluetoothSocket.getInputStream();//��IO��  
    		            	if(inputStream != null){  
    		            		System.out.println("----���ӳɹ�-----");  
    		            	}  
	    				} catch (Exception e) {  
	    					System.out.println("��Բ��ɹ�");  
	    					try {
	    						bluetoothSocket.close();
	    						boolean ress = removeBond(device.getClass(), device);
	    						boolean res1 = cancelBondProcess(device.getClass(), device);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}  
	    				}  
		    		} else {  
		    			try{
			    			//��Գɹ�
	    					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");         //UUID��ʾ���ڷ���  
			    			bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);  
	    		            bluetoothSocket.connect();//��������  
	    		            InputStream inputStream = bluetoothSocket.getInputStream();//��IO��  
			            	if(inputStream != null){  
			            		System.out.println("----���ӳɹ�-----");  
			            	}  if(inputStream != null){  
			            		System.out.println("----���ӳɹ�-----");  
			            	} 
		    			} catch (Exception e) {  
	    					System.out.println("��Բ��ɹ�");  
	    					try {
	    						bluetoothSocket.close();
	    						boolean resss = removeBond(device.getClass(), device);
	    						boolean res1 = cancelBondProcess(device.getClass(), device);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	    				} 
		    		}  
					
				}
			}).start();
		}
	}
    
    class OpenBluetoothOnClickListener implements android.content.DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT); 
		}
	}
    
    private boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
    
    private boolean cancelBondProcess(Class btClass,
    		            BluetoothDevice device)
    		 
    		    throws Exception
    		    {
    		        Method createBondMethod = btClass.getMethod("cancelBondProcess");
    		        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
    		        return returnValue.booleanValue();
    		    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode == REQUEST_ENABLE_BT){
    		if(resultCode == RESULT_OK){
//				mBluetoothAdapter.startDiscovery();
    			//�������ɹ�
    			new Thread(new Runnable() {
					
					@Override
					public void run() {
						BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("BE:03:5D:C2:65:3A");	
		    			if(device.getBondState() != BluetoothDevice.BOND_BONDED){//�жϸ�����ַ�µ�device�Ƿ��Ѿ����  
		    				try{  
		    					boolean res2 = setPin(device.getClass(), device, "1234");//����pinֵ  
//		    					boolean res = autoBond(device.getClass(), device, "1234");//����pinֵ  
		    					boolean res1= createBond(device.getClass(), device);  
		    					boolean res3= cancelPairingUserInput(device.getClass(), device);  
	    		             	//��Գɹ�
		    					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");         //UUID��ʾ���ڷ���  
		    					//�ͻ��˽���һ��BluetoothSocket��ȥ����Զ�������豸  
		    					BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);  
		    		            bluetoothSocket.connect();//��������  
		    		            InputStream inputStream = bluetoothSocket.getInputStream();//��IO��  
	    		            	if(inputStream != null){  
	    		            		System.out.println("----���ӳɹ�-----");  
	    		            	}  
		    				} catch (Exception e) {  
		    					System.out.println("��Բ��ɹ�");  
		    				}  
			    		 }  
			    		else {  
			    			try{
				    			//��Գɹ�
		    					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");         //UUID��ʾ���ڷ���  
				    			bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);  
		    		            bluetoothSocket.connect();//��������  
		    		            InputStream inputStream = bluetoothSocket.getInputStream();//��IO��  
				            	if(inputStream != null){  
				            		System.out.println("----���ӳɹ�-----");  
				            	}  if(inputStream != null){  
				            		System.out.println("----���ӳɹ�-----");  
				            	} 
			    			} catch (Exception e) {  
		    					System.out.println("��Բ��ɹ�");  
		    				} 
			    		}  
						
					}
				}).start();
    		} else {
    			Toast.makeText(mContext, "������ʧ��", Toast.LENGTH_LONG).show();
			}
    	}
	}
    
    private boolean cancelPairingUserInput(Class btClass,
    		            BluetoothDevice device)
    		 
    		    throws Exception
    		    {
    		        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
    		        // cancelBondProcess()
    		        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
    		        return returnValue.booleanValue();
    		    }

    
    //�Զ��������Pinֵ  
    private boolean autoBond(Class btClass,BluetoothDevice device,String strPin) throws Exception {   
        Method autoBondMethod = btClass.getMethod("setPin",new Class[]{byte[].class});  
        Boolean result = (Boolean)autoBondMethod.invoke(device,new Object[]{strPin.getBytes()});  
        return result;  
    }  
    
    private  boolean setPin(Class btClass, BluetoothDevice btDevice,
    		            String str) throws Exception
    		    {
    		        try
    		        {
    		            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
    		                    new Class[]
    		                    {byte[].class});
    		            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
    		                    new Object[]
    		                    {str.getBytes()});
    		        }
    		        catch (SecurityException e)
    		        {
    		            // throw new RuntimeException(e.getMessage());
    		            e.printStackTrace();
    		        }
    		        catch (IllegalArgumentException e)
    		        {
    		            // throw new RuntimeException(e.getMessage());
    		            e.printStackTrace();
    		        }
    		        catch (Exception e)
    		        {
    		            // TODO Auto-generated catch block
    		            e.printStackTrace();
    		        }
    		        return true;
    		 
    		    }

  
    //��ʼ���  
    private boolean createBond(Class btClass,BluetoothDevice device) throws Exception {   
        Method createBondMethod = btClass.getMethod("createBond");   
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);   
        return returnValue.booleanValue();   
    }  


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if(BluetoothDevice.ACTION_FOUND.equals(action)){  
                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                 if (device.getName() != null) {
                	 mNewDevicesArrayAdapter.add(device.getAddress());
                 } else {
                	 mNewDevicesArrayAdapter.add(device.getAddress());
                 }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {  //��������  
                if (mNewDevicesArrayAdapter.size() == 0) {
            		Toast.makeText(mContext, "û���������豸", Toast.LENGTH_LONG).show();
                } else {
					Intent intent2 = new Intent();
					intent2.putExtra("DEVICE", mNewDevicesArrayAdapter);
					intent2.setClass(MainActivity.this, DeviceActivity.class);
					startActivity(intent2);
				}
            }  
        }  
	};
    
}
