package farin.code.air.db;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	//private static String DB_PATH ="/data/data/farin.code.air/databases/";

	private static String DB_NAME = "sensor";



	public SQLiteDatabase myDataBase;

	private final Context myContext;



	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		//Log.d("same",context.getDatabasePath(DB_NAME).getPath());
	}



	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws Exception
	{
		boolean dbExist = checkDataBase();

		if(dbExist){
			//do nothing - database already exist
		}else{
			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase().close();
			try {
				copyDataBase();  
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){
		File dbFile = new File(myContext.getDatabasePath(DB_NAME).getPath());
		//long size=dbFile.length()/(1024);
		return dbFile.isFile();
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws Exception{
		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = myContext.getDatabasePath(DB_NAME).getPath();

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);

		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException{

		//Open the database
		String myPath = myContext.getDatabasePath(DB_NAME).getPath();
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);


	}

	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.

}