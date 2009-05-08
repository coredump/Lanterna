package br.eti.core.lanterna;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ImageView;

public class Toy extends Activity {

	private String toy_image;
	private WakeLock wakelock;
	private ImageView main_image_view;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        /* Set this activity to full bright */
		WindowManager.LayoutParams lp = getWindow().getAttributes(); 
        lp.screenBrightness = 1.0f; 
        
        /* PowerManager interface to get and release the WakeLocks later */
        PowerManager pmanager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakelock = pmanager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
        			  					   "Lanterna Lock");
        
        setContentView(R.layout.toy);
        main_image_view = (ImageView) findViewById(R.id.toy_main_view);
        
    }
	
    public void onResume() {
    	super.onResume();
    	
        /* Preferences */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        toy_image = prefs.getString("pref_toy_image", "HEART");
        
    	/* Get the WakeLock */
        this.wakelock.acquire();

    }
    
    public void onPause() {
    	super.onPause();
    	
    	/* Releases the WakeLock */
    	this.wakelock.release();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        
        findViewById(R.id.toy_main_view).getBackground();
        if (hasFocus) {
            animatePanels();
        }
    }
    
    private void animatePanels() {
    	
    	Drawable[] panels = createPanels();
    	AnimationDrawable ledtoy = new AnimationDrawable();
    	
    	Resources res = this.getResources();
    	Drawable black = res.getDrawable(R.drawable.black_light);
    	
    	for(int i=0; i < panels.length; i++) {
    		ledtoy.addFrame(panels[i], 50);
    		ledtoy.addFrame(black, 20);
    	}
    	
    	ledtoy.addFrame(black, 5);
    	
    	ledtoy.setOneShot(false);

    	main_image_view.setImageDrawable(ledtoy);
    	ledtoy.start();
    }
    
    private Drawable[] createPanels() {
    	
    	Drawable[] result = new Drawable[24];
    	int[][] states = generateStates();
    	
    	for (int i=0; i < states.length;i++) {
    		Drawable tmp = new LedPanel(states[i], 20, 20, 0xffff0000);
    		result[i] = tmp;
    	}
    	
    	return result;
    }
    
    private int[][] generateStates() {

    	int[][] tmp_result = new int[24][24];
    	int[][] result = new int[24][24];
    	String my_image = null;
    	
    	if (toy_image.equals("HEART")) {
    		my_image = ImageStrings.HEART;
    	}
    	
    	int substr = 0;
    	for (int x=0; x < 24; x++) {
	    	for (int i=0; i < 24; i++) {
	    		String parts = my_image.substring(substr, substr+24);
	    		if (parts.charAt(i) == '1') {
	    			tmp_result[x][i] = 1;
	    		} else {
	    			tmp_result[x][i] = 0;
	    		}
	    	}
	    	substr = substr + 24;
    	}
    	
    	for(int k = 0; k < 24; k++) {
    		for(int m=0; m < 24; m++) {
    			result[m][k] = tmp_result[k][m];
    		}
    	}
    	
    	return result;
    	
    }
}