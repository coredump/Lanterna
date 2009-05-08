package br.eti.core.lanterna;

/*
 * Lanterna - Copyright 2009 José de Paula E. Junior (jose.junior@gmail.com)
 * 
 * This file is part of Lanterna.

 * Lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Lanterna is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Lanterna.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;


public class Lanterna extends Activity {
	
	/* Some Constants */
	private static final int M_L_FLASHLIGHT = 10;
	private static final int M_L_STROBE = 14;
//	private static final int M_L_TOY = 16;
	
	/* Preferences */
	private String strobe_color;
	private String strobe_speed;
	private String fl_color;
	@SuppressWarnings("unused")
	private String toy_image;
	
	/* Power and Brightness stuff */
	private WakeLock wakelock;
	
	/* Miscelaneous states */
	private ImageView main_image_view;
//	private Boolean need_restart = false;
	private int last_mode = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Set this activity to full bright */
		WindowManager.LayoutParams lp = getWindow().getAttributes(); 
        lp.screenBrightness = 1.0f; 
        
        /* PowerManager interface to get and release the WakeLocks later */
        PowerManager pmanager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakelock = pmanager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
        			  					   "Lanterna Lock");
        
        if (savedInstanceState != null) {
	        if (savedInstanceState.containsKey("last_mode")) {
	        	last_mode = savedInstanceState.getInt("last_mode");
	        }
        }
        
        /* Setup the View */
        setContentView(R.layout.main);
        main_image_view = (ImageView) findViewById(R.id.main_image_view);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
        /* Preferences */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        strobe_color = prefs.getString("pref_strobe_color", "BW");
        strobe_speed = prefs.getString("pref_strobe_speed", "N");
        fl_color = prefs.getString("pref_flashlight_color", "WHITE");
        toy_image = prefs.getString("pref_toy_image", "HEART");
    	
    	/* Get the WakeLock */
        this.wakelock.acquire();
        
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	/* Releases the WakeLock */
    	this.wakelock.release();
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); 
		
		if (last_mode != 0) { 
			outState.putInt("last_mode", last_mode);
		}
		
	}
    
    public void onWindowFocusChanged(boolean hasFocus) {

        /* Restart the last mode */
        switch (last_mode) {
        case M_L_FLASHLIGHT:
        	setLightColor();
        	break;
        case M_L_STROBE:
        	strobeAnimation();
        	break;
        default:
        	setLightColor();
        	break;
        }   
    	
    }
    
	public void setLightColor() {
		last_mode = M_L_FLASHLIGHT;
		if (fl_color.equals("WHITE")) {
			main_image_view.setImageResource(R.drawable.white_light);
		} else if (fl_color.equals("RED")) {
			main_image_view.setImageResource(R.drawable.red_light);
		} else if (fl_color.equals("BLUE")) {
			main_image_view.setImageResource(R.drawable.blue_light);
		} else if (fl_color.equals("GREEN")) {
			main_image_view.setImageResource(R.drawable.green_light);
		} else if (fl_color.equals("YELLOW")) {
			main_image_view.setImageResource(R.drawable.yellow_light);
		}
		
	}
	
	/* Creates the basic Strobe Animation */
	public void strobeAnimation() {
		last_mode = M_L_STROBE;
		int s_speed = 150;
		String speed = strobe_speed;
		String style = strobe_color;
		
		if (speed.equals("F")) {
				s_speed = 75;
		} else if (speed.equals("N")){
				s_speed = 150;	
		} else if (speed.equals("S")) {
				s_speed = 250;
		}
		
		Resources res = this.getResources();
		Map<String, Drawable> frames = new HashMap<String, Drawable>();
		frames.put("white", res.getDrawable(R.drawable.white_light));
		frames.put("black", res.getDrawable(R.drawable.black_light));
		frames.put("red", res.getDrawable(R.drawable.red_light));
		frames.put("green", res.getDrawable(R.drawable.green_light));
		frames.put("blue", res.getDrawable(R.drawable.blue_light));
		frames.put("yellow", res.getDrawable(R.drawable.yellow_light));
		
		String[] to_be_added = null;
		
		if (style.equals("BW")) {
			to_be_added = new String[] { "black", "white" };
		} else if (style.equals("COLOR")) {
			to_be_added = new String[] { "black", "white", "red", "green", "yellow", "blue" };
		} else if (style.equals("POLICE")) {
			to_be_added = new String[] {"red", "blue"};
		}
		
		AnimationDrawable strobe = new AnimationDrawable();
		
		if (style.equals("SOS")) {
			Drawable white_frame = frames.get("white");
			Drawable black_frame = frames.get("black");
			strobe.addFrame(black_frame, 500);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 1000);
			strobe.addFrame(white_frame, 1200);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 1200);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 1200);
			strobe.addFrame(black_frame, 1000);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 300);
			strobe.addFrame(white_frame, 500);
			strobe.addFrame(black_frame, 500);
		} else if (style.equals("BEACON")){
			Drawable white_frame = frames.get("white");
			Drawable black_frame = frames.get("black");
			strobe.addFrame(white_frame, 100);
			strobe.addFrame(black_frame, 900);
		} else {
			for (int i = 0; i < to_be_added.length; i++) {
				Drawable t_frame = frames.get(to_be_added[i]);
				strobe.addFrame(t_frame, s_speed);
			}
		}

		strobe.setOneShot(false);
		
		main_image_view.setImageDrawable(strobe);
		strobe.start();
	}
	
	/* Creates the menu items */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflat = getMenuInflater();
		inflat.inflate(R.menu.options_menu, menu);
		return true;
	}
	
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_settings:
        	startActivity(new Intent(this, Preferences.class));
            return(true);
        case R.id.menu_quit:
        	finish();
            return(true);
        case R.id.mode_flashlight:
        	setLightColor();
        	return(true);
        case R.id.mode_strobe:
        	strobeAnimation();
        	return(true);
//        case R.id.mode_toy:
//        	last_mode = M_L_TOY;
//        	startActivity(new Intent(this, Toy.class));
//        	return(true);
        }
        return false;
    }
	
}