package com.example.bluetoothprinterdemo;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothService {
	private static final String BLUETOOTH_SERIAL_SERVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	protected static final int MESSAGE_READ = 1000;
	private static final String TAG = "BluetoothService";
	protected static final int READ_SIZE = 1024;
	private static BluetoothService bluetoothService = null;
	private static BluetoothAdapter bluetoothAdapter = null;
	private BluetoothSocket bluetoothSocket = null;
	private String remoteDeviceAddress = null;
	private String pin = null;
	private BluetoothDevice bluetoothDevice= null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private static Handler handler = null;
	
	/**
	 * 
	 * @param 返回值为空，该设备不支持蓝牙
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public static BluetoothService getInstance(Handler handler) {
		if (bluetoothService == null) {
			if (bluetoothAdapter == null) {
				return bluetoothService;
			}
			bluetoothService = new BluetoothService();
		}
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothService.handler = handler;
		return bluetoothService;
	}
	
	/**
	 * 获取系统BluetoothAdapter(自行判断是否启动蓝牙)
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}
	
	/**
	 * 设置远程蓝牙设备MAC地址
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public void setRemoteDeviceAddress(String address){
		remoteDeviceAddress = address;
	}
	
	/**
	 * 设置PIN
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public void setPin(String pin){
		this.pin = pin;
	}
	
	/**
	 * 设置自动配对PIN
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean setPin(Class btClass, BluetoothDevice btDevice,String str) throws Exception {
		Boolean returnValue = null;
        Method setPinMethod = btClass.getDeclaredMethod("setPin",new Class[]{byte[].class});
        returnValue = (Boolean) setPinMethod.invoke(btDevice,new Object[]{str.getBytes()});
        return returnValue.booleanValue();
    }
	
	/**
	 * 开始配对
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean createBond(Class btClass,BluetoothDevice device) throws Exception {   
        Method createBondMethod = btClass.getMethod("createBond");   
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);   
        return returnValue.booleanValue();   
    }
    
    /**
	 * 取消用户输入配对
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean cancelPairingUserInput(Class btClass,BluetoothDevice device) throws Exception {
        Method cancelPairingUserInputMethod = btClass.getMethod("cancelPairingUserInput");
        Boolean returnValue = (Boolean) cancelPairingUserInputMethod.invoke(device);
        return returnValue.booleanValue();
    }
    
    /**
	 * 移除配对
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }
    
    /**
	 * 取消配对进程
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean cancelBondProcess(Class btClass,BluetoothDevice device) throws Exception {
        Method cancelBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) cancelBondMethod.invoke(device);
        return returnValue.booleanValue();
    }
	
    /**
	 * 自动配对蓝牙设备
	 * @param
	 * @return 0:成功 -1:没有设置远程地址 或者 PIN 1:设置PIN失败 2:配对失败 3:取消用户输入PIN失败
	 * @date 2014-2-27
	 * @author zwz
	 * @throws Exception
	 */
	public int autoBondBluetooth() throws Exception{
		if (remoteDeviceAddress == null || pin == null) {
			return -1;
		}
		bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteDeviceAddress);	
		if(bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED){				//判断给定地址下的device是否已经配对  
			if (!setPin(bluetoothDevice.getClass(), bluetoothDevice, pin)) {
				Log.i(TAG,"设置PIN失败");
				return 1;
			}
			if (!createBond(bluetoothDevice.getClass(), bluetoothDevice)) {
				Log.i(TAG,"配对失败");
				return 2;
			}
			if (!cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice)) {
				Log.i(TAG,"取消用户输入PIN失败");
				return 3;
			}
		}
		Log.i(TAG,"配对成功");
		return 0;
	}
	
	/**
	 * 打开蓝牙socket
	 * @param
	 * @return -1:失败 0:成功 
	 * @date 2014-2-27
	 * @author zwz
	 * @throws Exception
	 */
	public void connectBluetoothSocket() throws Exception{
		UUID uuid = UUID.fromString(BLUETOOTH_SERIAL_SERVICE_UUID);	//UUID表示串口服务  
	  	//客户端建立一个BluetoothSocket类去连接远程蓝牙设备  
		bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
		bluetoothSocket.connect();	
		inputStream = bluetoothSocket.getInputStream();	
		outputStream = bluetoothSocket.getOutputStream();  
    	if(inputStream != null && outputStream != null){
    		new Thread(bluetoothListenRunnable).start(); //启动蓝牙数据坚挺线程							
    	}
    	Log.i(TAG,"建立socket成功");
	}
	
	private Runnable bluetoothListenRunnable = new Runnable() {
		
		@Override
		public void run() {
			byte[] buff = new byte[READ_SIZE];  
            int len = 0;  
            //读数据需不断监听，写不需要  
            while(true){  
                try {  
                    len = inputStream.read(buff);  
                    //把读取到的数据发送给UI进行处理  
                    Message msg = handler.obtainMessage(BluetoothService.MESSAGE_READ,  
                            len, -1, buff);  
                    msg.sendToTarget();  
                } catch (IOException e) {  
                	Log.e(TAG, e.toString());
                }  
            }  
			
		}
	};
	
	/**
	 * 关闭蓝牙socket
	 * @param
	 * @return 
	 * @date 2014-2-27
	 * @author zwz
	 * @throws Exception 
	 */
	public void closeBluetoothSocket() throws Exception{
		bluetoothSocket.close();
		removeBond(bluetoothDevice.getClass(), bluetoothDevice);
		cancelBondProcess(bluetoothDevice.getClass(), bluetoothDevice);
		Log.i(TAG,"关闭socket成功");
	}
	
	/**
	 * 蓝牙发送数据
	 * @param
	 * @return
	 * @date 2014-2-28
	 * @author zwz
	 * @throws Exception 
	 */
	public void sendData(byte[] buffer) throws Exception {  
        outputStream.write(buffer);
        Log.i(TAG,"蓝牙发送数据成功");
    }  
}
