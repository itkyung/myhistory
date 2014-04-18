package com.bizwave.bamstory.activity;





import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bizwave.bamstory.R;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.util.CallUtil;




public class CallHistoryActivity extends ListActivity {
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected Cursor selectCursor;
	private String deviceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = manager.getDeviceId();
		
		dbHelper = new DBHelper(this.getApplicationContext());
		db = dbHelper.getReadableDatabase();
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DBHelper.TABLE_NAME);
		
		selectCursor = qb.query(db, new String[]{DBHelper.TABLE_NAME + "._id",DBHelper.TABLE_NAME + ".title",
				DBHelper.TABLE_NAME + ".calldate",DBHelper.TABLE_NAME + ".contactid",DBHelper.TABLE_NAME + ".phone"}, null, null, null, null, "_id DESC");
		startManagingCursor(selectCursor);
		
		ListAdapter adapter = new SimpleCursorAdapter(this,R.layout.callhistoryrow, selectCursor,
				new String[]{"title","calldate"},
				new int[]{R.id.Name,R.id.Date});
		setListAdapter(adapter);
			
	}

	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final long selectedId = id;
		new AlertDialog.Builder(this).setMessage("전화연결하시겠습니까?")
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					selectCursor.moveToFirst();
					while(selectCursor.isAfterLast() == false){
						if(selectedId == selectCursor.getLong(0)){
							String title = selectCursor.getString(1);
							String contactId = selectCursor.getString(3);
							String phone = selectCursor.getString(4);
							CallUtil.getInstance().insertCallDb(db, phone, contactId, title, deviceId);
							
							Uri number = Uri.parse("tel:" + phone);
							Intent dial = new Intent(Intent.ACTION_CALL,number);
							startActivity(dial);
							break;
						}
					}
				}
			})
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setCancelable(true).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add("전체이력삭제").setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				deleteCallHistory();
				return false;
			}

		});
		
		
		return true;
	}
	
	
	public void deleteCallHistory(){
		db.delete(DBHelper.TABLE_NAME, null, null);
		selectCursor.requery();
	}
	
/*
	private class CallHistoryAdapter extends ArrayAdapter<CallHistoryInfo> {
		private ArrayList<CallHistoryInfo> items;
	
		
		public CallHistoryAdapter(Context context,int textViewResourceId,ArrayList<CallHistoryInfo> items){
			super(context,textViewResourceId,items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.callhistoryrow, null);
			}
			CallHistoryInfo info = items.get(position);
			if(info != null){
				TextView name = (TextView)v.findViewById(R.id.Name);
				TextView date = (TextView)v.findViewById(R.id.Date);
				name.setText(info.getTitle());
				date.setText(info.getCallDate());
				
			}
			return v;
		}

	}
	*/
	
	
	
}
