/*
 * Copyright (C) 2012 Ferenc Nagy <nferenc@nferenc.com>
 * Copyright (C) 2010, 2011 Christopher Eby <kreed@kreed.org>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package farin.code.air;

import farin.code.air.PlaybackService.State;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * 1x4 widget that shows title, artist, album art, a play/pause button, and a
 * previous/next button.
 */
public class MainWidget extends AppWidgetProvider {
	private static boolean sEnabled;

	@Override
	public void onEnabled(Context context)
	{
		sEnabled = true;
	}

	@Override
	public void onDisabled(Context context)
	{
		sEnabled = false;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] ids)
	{
		String path="",title="";
		PlaybackService.State state = PlaybackService.State.Stopped;
		PlaybackService service;
		if (PlaybackService.hasInstance()) 
			service = PlaybackService.getInstance();
		else
			service = PlaybackService.get(context);
		path = service.getSongPath();
		title = service.getSongTitle();
		state = PlaybackService.getState();


		sEnabled = true;
		updateWidget(context, manager, path,title, state);
	}

	/**
	 * Check if there are any instances of this widget placed.
	 */
	public static void checkEnabled(Context context, AppWidgetManager manager)
	{
		sEnabled = manager.getAppWidgetIds(new ComponentName(context, MainWidget.class)).length != 0;
	}

	/**
	 * Populate the widgets with the given ids with the given info.
	 *
	 * @param context A Context to use.
	 * @param manager The AppWidgetManager that will be used to update the
	 * widget.
	 * @param song The current Song in PlaybackService.
	 * @param state The current PlaybackService state.
	 */
	public static void updateWidget(Context context, AppWidgetManager manager, String song,String title, State state)
	{
		if (!sEnabled)
			return;

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.four_white_widget);

		
			views.setViewVisibility(R.id.play_pause, View.VISIBLE);
			views.setViewVisibility(R.id.previous, View.VISIBLE);
			views.setViewVisibility(R.id.next, View.VISIBLE);
			

		boolean playing = state==State.Playing;
		views.setImageViewResource(R.id.play_pause, playing ? R.drawable.pause : R.drawable.play);

		Intent intent;
		PendingIntent pendingIntent;

		ComponentName service = new ComponentName(context, PlaybackService.class);



		intent = new Intent(PlaybackService.ACTION_TOGGLE_PLAYBACK).setComponent(service);
		pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.play_pause, pendingIntent);

		intent = new Intent(PlaybackService.ACTION_NEXT_SONG).setComponent(service);
		pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.next, pendingIntent);

		intent = new Intent(PlaybackService.ACTION_PREVIOUS_SONG).setComponent(service);
		pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.previous, pendingIntent);

		manager.updateAppWidget(new ComponentName(context, MainWidget.class), views);
	}
}
