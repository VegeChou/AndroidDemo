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
	 * @param ����ֵΪ�գ����豸��֧������
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
	 * ��ȡϵͳBluetoothAdapter(�����ж��Ƿ���������)
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}
	
	/**
	 * ����Զ�������豸MAC��ַ
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public void setRemoteDeviceAddress(String address){
		remoteDeviceAddress = address;
	}
	
	/**
	 * ����PIN
	 * @param
	 * @return
	 * @date 2014-2-27
	 * @author zwz
	 */
	public void setPin(String pin){
		this.pin = pin;
	}
	
	/**
	 * �����Զ����PIN
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
	 * ��ʼ���
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
	 * ȡ���û��������
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
	 * �Ƴ����
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
	 * ȡ����Խ���
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
	 * �Զ���������豸
	 * @param
	 * @return 0:�ɹ� -1:û������Զ�̵�ַ ���� PIN 1:����PINʧ�� 2:���ʧ�� 3:ȡ���û�����PINʧ��
	 * @date 2014-2-27
	 * @author zwz
	 * @throws Exception
	 */
	public int autoBondBluetooth() throws Exception{
		if (remoteDeviceAddress == null || pin == null) {
			return -1;
		}
		bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteDeviceAddress);	
		if(bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED){				//�жϸ�����ַ�µ�device�Ƿ��Ѿ����  
			if (!setPin(bluetoothDevice.getClass(), bluetoothDevice, pin)) {
				Log.i(TAG,"����PINʧ��");
				return 1;
			}
			if (!createBond(bluetoothDevice.getClass(), bluetoothDevice)) {
				Log.i(TAG,"���ʧ��");
				return 2;
			}
			if (!cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice)) {
				Log.i(TAG,"ȡ���û�����PINʧ��");
				return 3;
			}
		}
		Log.i(TAG,"��Գɹ�");
		return 0;
	}
	
	/**
	 * ������socket
	 * @param
	 * @return -1:ʧ�� 0:�ɹ� 
	 * @date 2014-2-27
	 * @author zwz
	 * @throws Exception
	 */
	public void connectBluetoothSocket() throws Exception{
		UUID uuid = UUID.fromString(BLUETOOTH_SERIAL_SERVICE_UUID);	//UUID��ʾ���ڷ���  
	  	//�ͻ��˽���һ��BluetoothSocket��ȥ����Զ�������豸  
		bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
		bluetoothSocket.connect();	
		inputStream = bluetoothSocket.getInputStream();	
		outputStream = bluetoothSocket.getOutputStream();  
    	if(inputStream != null && outputStream != null){
    		new Thread(bluetoothListenRunnable).start(); //�����������ݼ�ͦ�߳�							
    	}
    	Log.i(TAG,"����socket�ɹ�");
	}
	
	private Runnable bluetoothListenRunnable = new Runnable() {
		
		@Override
		public void run() {
			byte[] buff = new byte[READ_SIZE];  
            int len = 0;  
            //�������費�ϼ�����д����Ҫ  
            while(true){  
                try {  
                    len = inputStream.read(buff);  
                    //�Ѷ�ȡ�������ݷ��͸�UI���д���  
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
	 * �ر�����socket
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
		Log.i(TAG,"�ر�socket�ɹ�");
	}
	
	/**
	 * ������������
	 * @param
	 * @return
	 * @date 2014-2-28
	 * @author zwz
	 * @throws Exception 
	 */
	public void sendData(byte[] buffer) throws Exception {  
        outputStream.write(buffer);
        Log.i(TAG,"�����������ݳɹ�");
    }  
}
