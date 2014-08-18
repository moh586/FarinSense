package farin.code.air;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



import farin.code.air.R;
import farin.code.air.player.SongsManager;
import farin.code.air.shapeengin.PersianReshap;

/**
 * Handles music playback and pretty much all the other work.
 */
public final class PlaybackService extends Service implements
				MediaPlayer.OnPreparedListener, 
				MediaPlayer.OnErrorListener, 
				MediaPlayer.OnBufferingUpdateListener, 
				MediaPlayer.OnCompletionListener{

	//private static final String ACTION_PLAY = "PLAY";
	private static String mUrl;
	public static PlaybackService mInstance;
	private static MediaPlayer mMediaPlayer = null;    // The Media Player
	private int mBufferPosition;
	private static String mSongTitle;
	NotificationManager mNotificationManager;
	Notification mNotification = null;
	final int NOTIFICATION_ID = 1;
	private static final Object[] sWait = new Object[0];
	public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public static SongsManager songManager;
	public static int currentSongIndex = 0;
	
	public InCallListener mCallListener;
	private PowerManager.WakeLock mWakeLock;
	
	public static final String ACTION_TOGGLE_PLAYBACK = "farin.code.air.action.TOGGLE_PLAYBACK";
	
	public static final String ACTION_NEXT_SONG = "farin.code.air.action.NEXT_SONG";
	
	public static final String ACTION_PREVIOUS_SONG = "farin.code.air.action.PREVIOUS_SONG";
	public Context mContext;
	private static final int NOTIFY_ME_ID=1337;
	
	// indicates the state our service:
	enum State {
		Retrieving, // the MediaRetriever is retrieving music
		Stopped, // media player is stopped and not prepared to play
		Preparing, // media player is preparing...
		Playing, // playback active (media player ready!). (but the media player may actually be
		// paused in this state if we don't have audio focus. But we stay in this state
		// so that we know we have to resume playback once we get focus back)
		Ready,
		Paused
		// playback paused (media player ready!)
	};

	static State mState = State.Stopped;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext=this;
		songManager = new SongsManager(this);
		if(songsList.size()==0)
			songsList = songManager.ListAllSongs();
		mInstance = this;
		synchronized (sWait) {
			sWait.notifyAll();
		}
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mMediaPlayer = new MediaPlayer(); // initialize it here
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		initWidgets();
		
		PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VanillaMusicLock");
		
		try {
			mCallListener = new InCallListener();
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);
		} catch (SecurityException e) {
			// don't have READ_PHONE_STATE
		}

	}

	public static State getState()
	{
		return mState;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private void initWidgets()
	{
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		//OneCellWidget.checkEnabled(this, manager);
		//FourSquareWidget.checkEnabled(this, manager);
		//FourLongWidget.checkEnabled(this, manager);
		MainWidget.checkEnabled(this, manager);
		//WidgetD.checkEnabled(this, manager);
		//WidgetE.checkEnabled(this, manager);
	}
	
	/**
	 * Update the widgets with the current song and state.
	 */
	private void updateWidgets()
	{
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		//OneCellWidget.updateWidget(this, manager, song, state);
		//FourLongWidget.updateWidget(this, manager, song, state);
		//FourSquareWidget.updateWidget(this, manager, song, state);
		MainWidget.updateWidget(this, manager,getSongPath() ,getSongTitle(), mState);
		//WidgetD.updateWidget(this, manager, song, state);
		//WidgetE.updateWidget(this, manager, song, state);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//
		if (intent != null) {
			String action = intent.getAction();

			if (ACTION_TOGGLE_PLAYBACK.equals(action)) {
				if(isPlaying())
					pauseMusic();
				else
					startMusic();
			}else if(ACTION_NEXT_SONG.equals(action))
				NextMusic();
			else if(ACTION_PREVIOUS_SONG.equals(action))
				BackMusic();
			else
			{
				getMediaPlayer().stop();
			}
		}
		//startMusic();
		//initMediaPlayer();
		return START_STICKY;
	}
	
	public void  playSong(int songIndex){
		// Play song
		//loadMusicPlayer();
		mState=State.Retrieving;
		try {
			if(songsList.size()==0)
			{
				Toast.makeText(this, PersianReshap.reshape(getResources().getString(R.string.emptyfolder)),Toast.LENGTH_SHORT).show();
				return;
			}
			else if(songIndex>=songsList.size())
				songIndex=songsList.size()-1;
			//context.startService(new Intent(context, PlaybackService.class));
			//mp.reset();
			//mp.setDataSource(songsList.get(songIndex).get("songPath"));
			//mp.prepare();
			//mp.start();
			// Displaying Song title
			//String songTitle = songsList.get(songIndex).get("songTitle");
			currentSongIndex=songIndex;
			setSong(songsList.get(songIndex).get("songPath"), songsList.get(songIndex).get("songTitle"));
			initMediaPlayer();
			// Changing Button Image to pause image
			//btnPlay.setBackgroundResource(R.drawable.pause);

			// set Progress bar value
			//mp.setOnCompletionListener(this); // Important
			// Updating progress bar
			//updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * for playing next music
	 */
	public void NextMusic()
	{
		if(currentSongIndex < (songsList.size() - 1)){
			playSong(currentSongIndex + 1);
			//currentSongIndex = currentSongIndex + 1;
		}else{
			// play first song
			playSong(0);
			currentSongIndex = 0;
		}
	}
	
	public void BackMusic()
	{
		if(currentSongIndex > 0){
			playSong(currentSongIndex - 1);
			//currentSongIndex = currentSongIndex - 1;
		}else{
			// play last song
			playSong(songsList.size() - 1);
			currentSongIndex = songsList.size() - 1;
		}
	}
	

	private void initMediaPlayer() {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mUrl);
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// ...
		} catch (IllegalStateException e) {
			// ...
		} catch (IOException e) {
			NextMusic();
		}

		try {
			mMediaPlayer.prepareAsync(); // prepare async to not block main thread
		} catch (IllegalStateException e) {
			// ...
		}
		mState = State.Preparing;
	}

	public void restartMusic() {
		playSong(currentSongIndex);
	}

	protected void setBufferPosition(int progress) {
		mBufferPosition = progress;
	}

	/** Called when MediaPlayer is ready */
	@Override
	public void onPrepared(MediaPlayer player) {
		mState=State.Ready;
		startMusic();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
		mState = State.Retrieving;
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public void pauseMusic() {
		if (mState.equals(State.Playing)) {
			mMediaPlayer.pause();
			mState = State.Paused;
			updateNotification(mSongTitle + "(paused)");
			updateall();
			cancelnotifay();
		}
	}

	private void cancelnotifay() {
		NotificationManager mgr=
				(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				mgr.cancel(NOTIFY_ME_ID);
	}

	public void startMusic() {
		if (mState.equals(State.Ready)||mState.equals(State.Paused)) {
			
			if(mUrl!=null&&!mUrl.equals(""))
				mMediaPlayer.start();
			else
			{
				playSong(0);
			}
			mState = State.Playing;
			updateNotification(mSongTitle + "(playing)");
		}
		else if(mState.equals(State.Stopped))
			playSong(currentSongIndex);
		mState=State.Playing;
		updateall();
		//
		applaynotify();
		//
	}

	private void applaynotify() {
		NotificationManager mgr=
				(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE)
				;
				Notification note=new Notification(R.drawable.ic_launcher,
				PersianReshap.reshape(getResources().getString(R.string.notify_content)),
				System.currentTimeMillis());
				PendingIntent i=PendingIntent.getActivity(mContext, 0,
				new Intent(mContext, Status.class),
				0);
				note.setLatestEventInfo(mContext, PersianReshap.reshape(getResources().getString(R.string.app_name)),
						PersianReshap.reshape(getResources().getString(R.string.notify_content)),
				i);
				note.flags|=Notification.FLAG_NO_CLEAR;
				mgr.notify(NOTIFY_ME_ID, note);
		
	}

	private void updateall() {
		updateWidgets();
		
	}

	public static boolean isPlaying() {
		return mState.equals(State.Playing);

	}

	public int getMusicDuration() {
		if(getMediaPlayer()!=null)
			return getMediaPlayer().getDuration();
		else
			return(0);
	}

	public int getCurrentPosition() {
		if(getMediaPlayer()!=null)
			return getMediaPlayer().getCurrentPosition();
		else
			return(0);
	}

	public int getBufferPercentage() {
		return mBufferPosition;
	}

	public void seekMusicTo(int pos) {
		getMediaPlayer().seekTo(pos);
	}

	public static PlaybackService getInstance() {
		return mInstance;
	}

	public void setSong(String url, String title) {
		mUrl = url;
		mSongTitle = title;
	}

	public static boolean hasSong() {
		return (mUrl!=null&&!mUrl.equals(""));
	}

	public String getSongTitle() {
		return mSongTitle;
	}
	
	public String getSongPath() {
		return mUrl;
	}

	public static PlaybackService get(Context context)
	{
		if (mInstance == null) {
			context.startService(new Intent(context, PlaybackService.class));
			while (mInstance == null) {
				try {
					synchronized (sWait) {
						sWait.wait();
					}
				} catch (InterruptedException ignored) {
				}
			}
		}

		return mInstance;
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		setBufferPosition(percent * getMusicDuration() / 100);
	}

	/** Updates the notification. */
	void updateNotification(String text) {
		// Notify NotificationManager of new intent
	}

	/**
	 * Configures service as a foreground service. A foreground service is a service that's doing something the user is
	 * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
	 * the notification here.
	 */
	void setUpAsForeground(String text) {
		PendingIntent pi =
				PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), Status.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification = new Notification();
		mNotification.tickerText = text;
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), text, pi);
		startForeground(NOTIFICATION_ID, mNotification);
	}

	public static boolean hasInstance() {
		return mInstance!=null;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if(getSongPath()==null)
		{
			getMediaPlayer().stop();
		}
		else if(currentSongIndex < (songsList.size() - 1)){
			NextMusic();
		}else{
			// play first song
			playSong(0);
		}
		
	}
	
	public static boolean mPlayingBeforeCall;
	private class InCallListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
			case TelephonyManager.CALL_STATE_OFFHOOK: {
				//MediaButtonReceiver.setInCall(true);

				if (isPlaying()) {
					mPlayingBeforeCall=true;
					getMediaPlayer().pause();
				}
				break;
			}
			case TelephonyManager.CALL_STATE_IDLE: {
				//MediaButtonReceiver.setInCall(false);

				if (mPlayingBeforeCall) {
					//setFlag(FLAG_PLAYING);
					mPlayingBeforeCall = false;
					getMediaPlayer().start();
				}
				break;
			}
		}
		}
	}
}