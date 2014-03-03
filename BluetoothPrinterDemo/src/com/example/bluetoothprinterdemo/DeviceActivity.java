package com.example.bluetoothprinterdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceActivity extends Activity {
	private ListView mListView = null;
	private ArrayList<String> mDeviceList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_device);
		super.onCreate(savedInstanceState);
		
		InitCtrl();
	}

	private void InitCtrl() {
		mListView = (ListView)findViewById(R.id.lv);
		mDeviceList = (ArrayList<String>) getIntent().getStringArrayListExtra("DEVICE");
		MyAdapter myAdapter = new MyAdapter(this);
//		SimpleAdapter adapter = new SimpleAdapter(this,mDeviceHashMap,R.layout.item_device,new String[] { "ItemTitle" },new int[] { R.id.tv_address });  
//		SimpleAdapter adapter = new SimpleAdapter(null, mDeviceHashMap, 0, null, null);
		mListView.setAdapter(myAdapter);  
	}
	
	class MyAdapter extends BaseAdapter{
		private LayoutInflater inflater = null;
		
		MyAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mDeviceList.size();
		}

		@Override
		public Object getItem(int position) {
			return mDeviceList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_device, null);
				holder = new ViewHolder();
				holder.addressTextView = (TextView) convertView.findViewById(R.id.tv_address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.addressTextView.setText(mDeviceList.get(position));
			return convertView;
		}
		
		class ViewHolder{
			TextView addressTextView = null;
		}
	}
}
