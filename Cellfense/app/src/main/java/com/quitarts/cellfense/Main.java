package com.quitarts.cellfense;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {   
	private Typeface tf;
	private SharedPreferences sp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);          
        ContextContainer.setApplicationContext(getApplicationContext());        
        setContentView(R.layout.main);
    	sp = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        Display display = getWindowManager().getDefaultDisplay();         
        Utils.setCanvasSize(display.getWidth(), display.getHeight());
        tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/apexnew_medium.ttf");
        Button button_new_game = (Button)findViewById(R.id.button_new_game);
        button_new_game.setTypeface(tf);
        button_new_game.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button_menu));
        button_new_game.setOnClickListener(this);
        Button button_options = (Button)findViewById(R.id.button_options);
        button_options.setTypeface(tf);
        button_options.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button_menu));
        button_options.setOnClickListener(this);
        Button button_about= (Button)findViewById(R.id.button_about);
        button_about.setTypeface(tf);
        button_about.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button_menu));
        button_about.setOnClickListener(this); 
        checkUnLockArtButton();
    }

	private void checkUnLockArtButton() {
		String result = sp.getString(Utils.UNLOCKED_ART,"0");
		Button button_unlock = (Button)findViewById(R.id.button_unlocked);
		if(result != "0"){		
			button_unlock.setTypeface(tf);			
			button_unlock.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button_menu));
			button_unlock.setOnClickListener(this);
			button_unlock.setVisibility(View.VISIBLE);
		}
		else{
			button_unlock.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button_menu));
			button_unlock.setVisibility(View.GONE);
		}			
	}   
    
    @Override
    protected void onDestroy() {    
    	super.onDestroy();    			
    }

	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button_new_game:	 
				startActivity(new Intent(getApplicationContext(), LevelSelection.class));		
				break;		
			case R.id.button_options:
				startActivity(new Intent(Main.this, Options.class));			
				break;
			case R.id.button_about:
				startActivity(new Intent(Main.this, About.class));
				break;
			case R.id.button_unlocked:
				startActivity(new Intent(getApplicationContext(), Tutorial.class));
				break;
			default:
				break;
		}		
	}
	
	@Override
	public void onResume() {
	super.onResume();
		checkUnLockArtButton();
	}
		
	/*
	//SDK 2.0 en adelante. Reemplaza onKeyDown	
	@Override
	public void onBackPressed() {
		System.out.println("onBackPressed");		
	}		
	  
    @Override
    public void onStart() {
    	super.onStart();
    	System.out.println("onStart");
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	System.out.println("onResume");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	System.out.println("onPause");   	
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	System.out.println("onStop");
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	System.out.println("onRestart");
    }*/ 
}