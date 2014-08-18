package farin.code.air.player;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String(Environment.getExternalStorageDirectory().getPath());
	Context context;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	// Constructor
	public SongsManager(Context context){
			this.context=context;
	}

	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
		listfiles(MEDIA_PATH);
		return songsList;

	}
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	public void listfiles(String path )
	{
		File home = new File(path);
		if(home==null)
			return;
		try{
			if (home.listFiles(new FileExtensionFilter()).length> 0) {
				for (File file : home.listFiles(new FileExtensionFilter())) {
					HashMap<String, String> song = new HashMap<String, String>();
					song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
					song.put("songPath", file.getPath());

					// Adding each song to SongList
					songsList.add(song);
				}
				//

				for(File file:home.listFiles())
				{
					if(file.isDirectory())
						listfiles(file.getAbsolutePath());
				}
			}
		}catch(Exception e)
		{
			System.out.print(e.getMessage());
		}
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}

	String[] STAR = { "*" };
	int totalSongs;
	public ArrayList<HashMap<String, String>> ListAllSongs() 
	{
		Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		if (true) {
			Cursor cursor = context.getContentResolver().query(allsongsuri, STAR, selection, null, null);

			totalSongs = cursor.getCount();

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String songname = cursor
								.getString(cursor
										.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
						//int song_id = cursor.getInt(cursor
							//	.getColumnIndex(MediaStore.Audio.Media._ID));

						String fullpath = cursor.getString(cursor
								.getColumnIndex(MediaStore.Audio.Media.DATA));
						//fullsongpath.add(fullpath);

						//String albumname = cursor.getString(cursor
							//	.getColumnIndex(MediaStore.Audio.Media.ALBUM));
						//int album_id = cursor
						//		.getInt(cursor
							//			.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

						//String artistname = cursor.getString(cursor
						//		.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						//int artist_id = cursor
						//		.getInt(cursor
						//				.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
						//
						HashMap<String, String> song = new HashMap<String, String>();
						song.put("songTitle", songname.substring(0, (songname.length() - 4)));
						song.put("songPath", fullpath);
						songsList.add(song);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		}
		return songsList;
	}
}
