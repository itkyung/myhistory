package com.bizwave.bamstory.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "bamstory";
	private static final int DATABASE_VERSION = 3;
	
	public static final String TABLE_NAME = "bam_callhistory";
	public static final int historyLimit = 50;
	public static final String CREATE_HISTORY = "Create Table " +  TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,contactid TEXT,title TEXT,phone TEXT,calldate DATE);";
	public static final String COUNT_QUERY = "Select count(*) from " + TABLE_NAME;
	public static final String MIN_QUERY = "Select min(_id) from " + TABLE_NAME;	
	
	private SQLiteDatabase readableDB = null;
	private SQLiteDatabase writableDB = null;	

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqls[] = new String[]{
				CREATE_HISTORY,
				"create table "+RevisionInfo.TABLE_NAME
				+" (cRevision varchar(10), pRevision varchar(10))",
				"insert into "+RevisionInfo.TABLE_NAME
				+" (cRevision, pRevision) values ('0','0')",
				"create table "+Category.TABLE_NAME
				+" (id varchar(36), name varchar(128), type varchar(10), " 
				+"parentId varchar(36), partner boolean)",
				"create table "+CategoryContactMapping.TABLE_NAME
				+" (categoryId varchar(36), contactId varchar(36))",
				"create table "+Partner.TABLE_NAME
				+" (_id TEXT,partnerName varchar(20), idx integer, contactId varchar(36)," 
				+" partnerTypeName varchar(10), evaluationImageName varchar(10)," 
				+" contactName varchar(20), partnerId varchar(36)," 
				+" partnerType varchar(10), evaluation integer, bamsCertFlag integer, haveGirl integer, evalCount integer)",
				"create table " + Event.TABLE_NAME
				+" (id TEXT,name TEXT)",
				"create table " + EventContactMapping.TABLE_NAME
				+" (eventId TEXT,contactId TEXT)"
		};
		try {
			this.execMultipleSQL(db, sqls);
		} catch(SQLException sqlException) {
			throw sqlException;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqls[] = new String[]{
				"drop table "+RevisionInfo.TABLE_NAME,
				"drop table "+Category.TABLE_NAME,
				"drop table "+CategoryContactMapping.TABLE_NAME,
				"drop table "+Partner.TABLE_NAME,
				"drop table "+Event.TABLE_NAME,
				"drop table "+EventContactMapping.TABLE_NAME,
				"create table "+RevisionInfo.TABLE_NAME
				+" (cRevision varchar(10), pRevision varchar(10))",
				"insert into "+RevisionInfo.TABLE_NAME
				+" (cRevision, pRevision) values ('0','0')",
				"create table "+Category.TABLE_NAME
				+" (id varchar(36), name varchar(128), type varchar(10), " 
				+"parentId varchar(36), partner boolean)",
				"create table "+CategoryContactMapping.TABLE_NAME
				+" (categoryId TEXT, contactId TEXT)",
				"create table "+Partner.TABLE_NAME
				+" (_id TEXT,partnerName varchar(20), idx integer, contactId varchar(36)," 
				+" partnerTypeName varchar(10), evaluationImageName varchar(10)," 
				+" contactName varchar(20), partnerId varchar(36)," 
				+" partnerType varchar(10), evaluation integer, bamsCertFlag integer, haveGirl integer, evalCount integer)",
				"create table " + Event.TABLE_NAME
				+" (id TEXT,name TEXT)",
				"create table " + EventContactMapping.TABLE_NAME
				+" (eventId TEXT,contactId TEXT)"				
		};
		try {
			this.execMultipleSQL(db, sqls);
		} catch(SQLException sqlException) {
			throw sqlException;
		}
	}
	
	public RevisionInfo getRevisionInfo() {
		this.openReadableDB();
		Cursor cursor = null;
		try {
			cursor = readableDB.query(
					RevisionInfo.TABLE_NAME, 
					new String[]{"cRevision","pRevision"}, 
					null, null, null, null, null);
			cursor.moveToFirst();
			return new RevisionInfo(
					cursor.getString(cursor.getColumnIndexOrThrow("cRevision")), 
					cursor.getString(cursor.getColumnIndexOrThrow("pRevision")));
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	public void updateCRevisionInfo(String cRevision) {
		openWritableDB();
		writableDB.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("cRevision", cRevision);
			writableDB.update(RevisionInfo.TABLE_NAME, contentValues, 
					null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	public void updatePRevisionInfo(String pRevision) {
		openWritableDB();
		writableDB.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("pRevision", pRevision);
			writableDB.update(RevisionInfo.TABLE_NAME, contentValues, 
					null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	public Category getCategory(String id) {
		openReadableDB();
		Cursor cursor = null;
		try {
			cursor = readableDB.query(Category.TABLE_NAME, 
					new String[]{"id", "name", "type", "parentId", "partner"}, 
					"id='"+id+"'", null, null, null, null);				

			int rowCount = cursor.getCount();
			Category category = null;
			if(rowCount > 0) {
				cursor.moveToPosition(0);
				category = new Category(
						cursor.getString(cursor.getColumnIndexOrThrow("id")), 
						cursor.getString(cursor.getColumnIndexOrThrow("name")),
						cursor.getString(cursor.getColumnIndexOrThrow("type")), 
						cursor.getString(cursor.getColumnIndexOrThrow("parentId")), 
						new Boolean(cursor.getString(cursor.getColumnIndexOrThrow("partner")))
				);
			}
			return category;
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			if(cursor != null) {
				cursor.close();
			}			
		}		
	}
	
	public ArrayList<Category> getCategories(String parentId) {
		openReadableDB();
		Cursor cursor = null;
		ArrayList<Category> categories = null;
		
		try {
			cursor = readableDB.query(Category.TABLE_NAME, 
					new String[]{"id", "name", "type", "parentId", "partner"}, 
					"parentId='"+parentId+"'", null, null, null, null);				

			int rowCount = cursor.getCount();
			if(rowCount > 0) {
				categories = new ArrayList<Category>();
				Category category = null;
				for(int i=0; i<rowCount; i++) {
					cursor.moveToPosition(i);
					category = new Category(
							cursor.getString(cursor.getColumnIndexOrThrow("id")), 
							cursor.getString(cursor.getColumnIndexOrThrow("name")),
							cursor.getString(cursor.getColumnIndexOrThrow("type")), 
							cursor.getString(cursor.getColumnIndexOrThrow("parentId")), 
							new Boolean(cursor.getString(cursor.getColumnIndexOrThrow("partner")))
					);
					categories.add(category);
				}
			}
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			if(cursor != null) {
				cursor.close();
			}			
		}
		
		return categories;
	}
	
	public boolean createCategories(ArrayList<Category> categories) {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			Category category = null;
			for(int i=0; i<categories.size(); i++) {
				category = categories.get(i);
				ContentValues map = new ContentValues();
				map.put("id", category.getId());
				map.put("name", category.getText());
				map.put("type", category.getType());
				map.put("parentId", category.getParentId());
				map.put("partner", category.isPartner());
				writableDB.insertOrThrow(Category.TABLE_NAME, null, map);		
			}
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			return false;
		} finally {
			writableDB.endTransaction();
		}
		return true;
	}
	
	public void deleteCategory() {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			writableDB.delete(Category.TABLE_NAME, null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	public void createCategoryContactMapppings(ArrayList<CategoryContactMapping> mappings) {
		if(mappings != null && mappings.size() > 0) {
			this.openWritableDB();
			writableDB.beginTransaction();			
			ContentValues map = null;
			try {
				for(CategoryContactMapping mapping : mappings) {
					map = new ContentValues();
					map.put("categoryId", mapping.getCategoryId());
					map.put("contactId", mapping.getContactId());
					readableDB.insertOrThrow(CategoryContactMapping.TABLE_NAME, null, map);
				}
				readableDB.setTransactionSuccessful();
			} catch(SQLException sqlException) {
				throw sqlException;
			} finally {
				readableDB.endTransaction();
			}
		}
	}

	public void deleteCategoryContactMappings() {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			writableDB.delete(CategoryContactMapping.TABLE_NAME, null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	public ArrayList<Partner> getPartners(String id,boolean isCategory) {
		openReadableDB();
		Cursor cursor = null;
		ArrayList<Partner> partners = null;
		String pTable = Partner.TABLE_NAME;
		String cTable = isCategory ? CategoryContactMapping.TABLE_NAME : EventContactMapping.TABLE_NAME;
		try {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(pTable + " AS a," + cTable + " AS b");
			
			if(isCategory)
				builder.appendWhere("a.contactId=b.contactId AND b.categoryId = '" + id + "'");
			else
				builder.appendWhere("a.contactId=b.contactId AND b.eventId = '" + id +"'");
			
			cursor = builder.query(readableDB,
					new String[]{"a._id","a.partnerName", "a.idx", "a.contactId", 
				"a.partnerTypeName", "a.evaluationImageName", "a.contactName", 
				"a.partnerId", "a.partnerType", "a.evaluation", "a.bamsCertFlag","a.haveGirl","a.evalCount"}, 
					null,null, null, null, "a.idx,a.partnerName,a.contactName ASC");
			int rowCount = cursor.getCount();
			if(rowCount > 0) {
				partners = new ArrayList<Partner>();
				Partner partner = null;
				for(int i=0; i<rowCount; i++) {
					cursor.moveToPosition(i);
					partner = new Partner(
							cursor.getString(cursor.getColumnIndexOrThrow("a._id")),
							cursor.getString(cursor.getColumnIndexOrThrow("a.partnerName")), 
							cursor.getInt(cursor.getColumnIndexOrThrow("a.idx")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.contactId")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.partnerTypeName")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.evaluationImageName")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.contactName")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.partnerId")), 
							cursor.getString(cursor.getColumnIndexOrThrow("a.partnerType")), 
							cursor.getInt(cursor.getColumnIndexOrThrow("a.evaluation")),
							cursor.getInt(cursor.getColumnIndexOrThrow("a.bamsCertFlag")),
							cursor.getInt(cursor.getColumnIndexOrThrow("a.haveGirl")),
							cursor.getInt(cursor.getColumnIndexOrThrow("a.evalCount"))
					);
					partners.add(partner);
				}
			}
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			if(cursor != null) {
				cursor.close();
			}			
		}
		
		return partners;
	}
	
	public void createPartners(ArrayList<Partner> partners) {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			ContentValues map = null;
			for(Partner partner : partners) {
				map = new ContentValues();
				map.put("_id", partner.get_id());
				map.put("partnerName", partner.getPartnerName());
				map.put("idx", partner.getIdx());
				map.put("contactId", partner.getContactId());
				map.put("partnerTypeName", partner.getPartnerTypeName());
				map.put("evaluationImageName", partner.getEvaluationImageName());
				map.put("contactName", partner.getContactName());
				map.put("partnerId", partner.getPartnerId());
				map.put("partnerType", partner.getPartnerType());
				map.put("evaluation", partner.getEvaluation());
				map.put("bamsCertFlag", partner.getBamsCertFlag());
				map.put("haveGirl", partner.getHaveGirl());
				map.put("evalCount", partner.getEvalCount());
				writableDB.insertOrThrow(Partner.TABLE_NAME, null, map);
			}
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	public void deletePartner() {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			writableDB.delete(Partner.TABLE_NAME, null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}
	
	
	public void createEventContactMappings(ArrayList<EventContactMapping> mappings){
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			ContentValues map = null;
			for(EventContactMapping mapping : mappings){
				map = new ContentValues();
				map.put("eventId", mapping.getEventId());
				map.put("contactId", mapping.getContactId());
				writableDB.insertOrThrow(EventContactMapping.TABLE_NAME, null, map);
			}
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}		
	}
	
	public void deleteEventContactMappings() {
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			writableDB.delete(EventContactMapping.TABLE_NAME, null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
	}	
	
	public void createEvents(ArrayList<Event> events){
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			ContentValues map = null;
			for(Event event : events){
				map = new ContentValues();
				map.put("id", event.getId());
				map.put("name", event.getName());
				writableDB.insertOrThrow(Event.TABLE_NAME, null, map);
			}
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}		
	}
	
	
	public void deleteEvent(){
		this.openWritableDB();
		writableDB.beginTransaction();
		try {
			writableDB.delete(Event.TABLE_NAME, null, null);
			writableDB.setTransactionSuccessful();
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			writableDB.endTransaction();
		}
		
	}
	
	public ArrayList<Event> getEvents(){
		openReadableDB();
		Cursor cursor = null;
		ArrayList<Event> events = null;
		
		try {
			cursor = readableDB.query(Event.TABLE_NAME, 
					new String[]{"id", "name"}, 
					null, null, null, null, null);				

			int rowCount = cursor.getCount();
			if(rowCount > 0) {
				events = new ArrayList<Event>();
				Event event = null;
				for(int i=0; i<rowCount; i++) {
					cursor.moveToPosition(i);
					event = new Event();
					event.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
					event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
					events.add(event);
				}
			}
		} catch(SQLException sqlException) {
			throw sqlException;
		} finally {
			if(cursor != null) {
				cursor.close();
			}			
		}
		
		return events;
	}
	
	
	
	private void execMultipleSQL(SQLiteDatabase db, String[] sqls) {
		for(String sql : sqls) {
			if(sql.trim().length() > 0) {
				db.execSQL(sql);
			}
		}
	}	
	
	
	public void openWritableDB() {
		if(writableDB == null || !writableDB.isOpen()) {
			writableDB = getReadableDatabase();
		}
	}
	
	public void openReadableDB() {
		if(readableDB == null || !readableDB.isOpen()) {
			readableDB = getReadableDatabase();
		}
	}
}

