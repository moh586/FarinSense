package farin.code.air;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import farin.code.air.R;
import farin.code.air.player.Utilities;

public class Player implements SeekBar.OnSeekBarChangeListener
{
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private SeekBar timeline;
	Context context;
	// Media Player
	//public static  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	//public static SongsManager songManager;
	private Utilities utils;
	//private static int currentSongIndex = 0;
	//private static long duration=0;
	//private long currentpostion=0;


	public Player(Context context,ImageButton play,ImageButton next,ImageButton back,SeekBar timeline) {

		this.context=context;
		// All player buttons
		btnPlay = play;;
		btnNext = next;
		btnPrevious = back;
		this.timeline=timeline;
		// Mediaplayer
		//loadMusicPlayer();
		utils = new Utilities();

		// Getting all songs list

		//

		// Listeners
		timeline.setOnSeekBarChangeListener(this); // Important

		// By default play first song
		if(PlaybackService.isPlaying()){
			btnPlay.setBackgroundResource(R.drawable.pause);
		}else{
			btnPlay.setBackgroundResource(R.drawable.play);
		}
		updateProgressBar();
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//loadMusicPlayer();
				// check for already playing
				PlayPause();
				Status.postpone();

			}
		});




		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				PlaybackService mp=PlaybackService.getInstance();
				if(mp==null)
				{
					Intent i=new Intent(Player.this.context, PlaybackService.class);
					i.setAction(PlaybackService.ACTION_NEXT_SONG);
					Player.this.context.startActivity(i);
				}
				else{
					mp.NextMusic();
				}
				btnPlay.setBackgroundResource(R.drawable.pause);
				Status.postpone();

			}
		});

		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PlaybackService mp=PlaybackService.getInstance();
				if(mp==null)
				{
					Intent i=new Intent(Player.this.context, PlaybackService.class);
					i.setAction(PlaybackService.ACTION_PREVIOUS_SONG);
					Player.this.context.startActivity(i);
				}
				else{
					mp.BackMusic();
				}
				btnPlay.setBackgroundResource(R.drawable.pause);
				Status.postpone();

			}
		}); 
	}

	public void PlayPause()
	{
		PlaybackService mp=PlaybackService.getInstance();
		if(mp==null)
		{
			Intent i=new Intent(Player.this.context, PlaybackService.class);
			i.setAction(PlaybackService.ACTION_TOGGLE_PLAYBACK);
			Player.this.context.startActivity(i);
			btnPlay.setBackgroundResource(R.drawable.play);
		}
		else{
			if(PlaybackService.isPlaying()){
				// Changing button image to play button
				btnPlay.setBackgroundResource(R.drawable.play);
				mp.pauseMusic();
			}else{
				// Resume song
				btnPlay.setBackgroundResource(R.drawable.pause);
				mp.startMusic();
				updateProgressBar();
			}
		}
	}


	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}   

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			PlaybackService mp=PlaybackService.getInstance();	
			//duration = mp.getDuration();
			//currentpostion = mp.getCurrentPosition();

			// Displaying Total Duration time
			//songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			//songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress;
			if(mp==null)
				progress=0;
			else if(mp.getSongPath()==null)
				progress=0;
			else
				progress = (int)(utils.getProgressPercentage(mp.getCurrentPosition(), mp.getMusicDuration()));
			//Log.d("Progress", ""+progress);
			timeline.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	/**
	 *
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		PlaybackService mp=PlaybackService.getInstance();
		int currentPosition;
		if(mp==null)
			currentPosition=0;
		else
			currentPosition = utils.progressToTimer(seekBar.getProgress(),(int)mp.getMusicDuration());
		// forward or backward to certain seconds
		mp.seekMusicTo(currentPosition);
		// update timer progress again
		updateProgressBar();
	}

}