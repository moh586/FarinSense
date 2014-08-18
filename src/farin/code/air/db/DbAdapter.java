package farin.code.air.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class DbAdapter {

	public DataBaseHelper mDb;
	private String TableName="tbl_probs";
	private String ID="_id";
	private String DATA="data";
	//
	public static int FIRST=1;
	public static int CALL=2;
	public static int METHOD=3;
	public static int ON=4;
	public static int ACCESS=5;
	public static int GPS=6;
	public static int WIFI=7;
	public static int FLASH=8;
	public static int BT=9;
	public static int RESHAPE=10;
	public static int STATUS=11;
	public static int VIBRATE=12;
	public static int SOUND=13;
	public static int LANG=14;
	public static int Gyroscop=15;
	public static int SCROFF=16;
	public static int Date=17;
	
	    /**
	     * Database creation SQL statement
	     */

	    private final Context mCtx;
	    public static final String TABLE_CREATE =
	            "CREATE TABLE tbl_probs (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	            "data INTEGER, " + "name TEXT" +
	            ");"
	    ;

	    public DbAdapter(Context ctx){
	    	this.mCtx=ctx;
	    	mDb =new DataBaseHelper(ctx);
	    	try {  
		        mDb.createDataBase();
		        } catch (Exception ioe) {
		        	Toast.makeText(ctx, ioe.getMessage(), Toast.LENGTH_SHORT).show();
		        }
	    	}

	    public int Select(int id) {
	    	openDB();
	        Cursor cursor = mDb.myDataBase.query(TableName, new String[] {ID, DATA}
	        , ID + "=?",
            new String[] { String.valueOf(id) }, null, null, null, null);
	        if (cursor != null)
	            cursor.moveToFirst();
	        
	        int data=cursor.getInt(1);
	        
	        if (cursor != null && !cursor.isClosed()) {
	             cursor.close();
	          }
	        closeDB();
	        return data;
	    }
	   
	    public void Update(int id,int value)
	    {
	    	openDB();
	    	ContentValues cv = new ContentValues();
	    	cv.put(DATA,value); //These Fields should be your String values of actual column names
	    	
	    	mDb.myDataBase.update(TableName, cv, "_id "+"="+id, null);
	    	closeDB();
	    }
	    
	    public void openDB()
	    {
	    	try {        
 	        	mDb.openDataBase();
 	        }catch(Exception sqle){
 	        	Toast.makeText(mCtx, sqle.getMessage(), Toast.LENGTH_SHORT).show();
 	        }
	    }

	    public void closeDB()
	    {
	    	mDb.close();
	    }
}
