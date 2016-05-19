package com.quitarts.cellfense;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class LevelSelection extends Activity implements OnClickListener {
	private static final boolean TESTMODE = false;
	private HashMap<Integer, ArrayList<String>> levels;
	private SharedPreferences sp;
	private int levelLimit;
	private GameSurfaceView gameSurfaceView;
	private boolean readXmlFromNet = false;
	private String serverUrl = "http://192.168.1.103:3000/level/get?key=lucho";
	private LinearLayout linearLO;
	private int newLevelLimit;
	int numberOfUnlock;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * El hilo que dibuja necesita el contexto de la actividad que esta mostrando la surfaceview, en esta caso 
		 * levelSelection.java, por esta razon el contexto se setea en esta linea y no en el main.java
		 */
		ContextContainer.setApplicationContext(this);
		createDynamicLevelTable();
		Utils.setLevelUnlockedValues();
        super.setContentView(linearLO);     
	}
	
	public void onClick(View v) {
		if(v.getId() <= levelLimit){
			gameSurfaceView = new GameSurfaceView(LevelSelection.this, this, v.getId());
			setContentView(gameSurfaceView);
		}						
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	if(gameSurfaceView != null && !gameSurfaceView.getGameControl().isGamePaused()){
	    		gameSurfaceView.getGameControl().pause();
	    		gameSurfaceView.showExitConfirmDialog(LevelSelection.this);
	    		return true;
	    	}	    		
	    }	
	    return super.onKeyDown(keyCode, event);
	}
	
	public void setLevelSelectionContentView(){
		createDynamicLevelTable(); 
		super.setContentView(linearLO);
	}

	private void createDynamicLevelTable() throws FactoryConfigurationError {		
		parseXmlLevels();
		levels = LevelDataSet.getLevels();
		Typeface tfButton = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/tseries_c.ttf");
		Typeface tfText = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/apexnew_medium.ttf");
		sp = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		
		TableLayout table = new TableLayout (this);
        table.setLayoutParams( new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT) );
        
        linearLO = new LinearLayout(this);
        linearLO.setPadding(0, 19*Utils.getCanvasHeight()/100, 0, 0);
        linearLO.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        linearLO.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.menu));
                
        ScrollView scroolView = new ScrollView(this);
        scroolView.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
              
        linearLO.addView(scroolView);
        
        int colCount = 0;      
        TableRow tr = new TableRow(this);  
        TableRow tr2 = new TableRow(this);  
        newLevelLimit = 0; 
               
        for (int f=1; f<=levels.size(); f++) {
        	 colCount++;
        	 
      		if(f == 1){
      			String value = sp.getString(String.valueOf(f),"");
      			if(value == "")
      				numberOfUnlock = 1;
      			else
      				numberOfUnlock = 2;
      		}
        	 Button b = new Button (this);
        	 b.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button1));
        	 b.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
             b.setTypeface(tfButton);                      
             b.setTextSize(40.0f);
             b.setId(f);
             b.setTextColor(Color.WHITE);
             b.setGravity(Gravity.BOTTOM|Gravity.CENTER);          
             b.setOnClickListener(this);             
             
             TextView scoreLabel = new TextView(this);
             scoreLabel.setTypeface(tfText);
           	 scoreLabel.setTextSize(20);
           	 scoreLabel.setTextColor(Color.WHITE);
           	 scoreLabel.setGravity(Gravity.TOP|Gravity.CENTER); 
           	 scoreLabel.setId(f);
           	 scoreLabel.setOnClickListener(this);
           	            	 
            
             if(isUnlocked(f)){
            	 b.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button1));
            	 scoreLabel.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.button1_bottom));
               	 if(f == 1)
               		 scoreLabel.setText("Tutorial");
               	 else
               		scoreLabel.setText(sp.getString(String.valueOf(f),"Score:-"));
               	 b.setText(String.valueOf(f-1)); 
             }
             else       {     
            	 b.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.lock));
            	 scoreLabel.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.lock_bottom));
               	 scoreLabel.setText(sp.getString(String.valueOf(f),""));
             }        
             
             if (colCount <= 2 && f == levels.size()){
            	 tr.addView(b, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);            	 
            	 tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                 
            	 tr2.addView(scoreLabel, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);            	 
            	 tr2.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));    
            	 
            	 table.addView(tr);
            	 table.addView(tr2);
            	 
            	 tr = new TableRow(this);
            	 tr2 = new TableRow(this);
            	 colCount= 0;
             }
             else if(colCount <= 2){
            	 tr.addView(b, b.getBackground().getMinimumWidth(),b.getBackground().getMinimumHeight());            	 
            	 tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            	 
            	 tr2.addView(scoreLabel, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);            	 
            	 tr2.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)); 
        	 }           
             else
             {
            	 tr.addView(b, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);            	 
            	 tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                 
            	 tr2.addView(scoreLabel,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);            	 
            	 tr2.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));            	 
            	 
            	 table.addView(tr);
            	 table.addView(tr2);
            	 
            	 tr = new TableRow(this);
            	 tr2 = new TableRow(this);
            	 colCount= 0;
             }
         } //end for

        if(TESTMODE)
        	activateAllStages();
        scroolView.addView(table);
	}
	
	private boolean isUnlocked(int f) {		
		String value = sp.getString(String.valueOf(f),"");		
		
		if (value != ""){
        	levelLimit = f; 
        	return true;
        }
		else if (value == "" && newLevelLimit < numberOfUnlock){
			newLevelLimit++;
			levelLimit = f;
			return true;
		}
		return false;
	}

	private void activateAllStages() {
        for (int f=1; f<=levels.size(); f++) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(String.valueOf(f),String.valueOf(f));
			editor.commit();
        }
        levelLimit = levels.size();
	}
	
	private void parseXmlLevels() throws FactoryConfigurationError {
		try {		          
			LevelDataSet.reset();
		    URL url = new URL(serverUrl);
		    SAXParserFactory spf = SAXParserFactory.newInstance();
		    SAXParser sp = spf.newSAXParser();
		    XMLReader xr = sp.getXMLReader();	            		            
		    LevelXmlHandler lxh = new LevelXmlHandler();
		    xr.setContentHandler(lxh);		    
		    
		    if(readXmlFromNet) {
		    	xr.parse(new InputSource(url.openStream()));	            	
		    }
		    else {
		    	InputStream is = getApplicationContext().getResources().openRawResource(R.raw.levels);		            
		        xr.parse(new InputSource(is));	            	
		    }          
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void postScore(String subject, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, ContextContainer.getApplicationContext().getText(R.string.post_score)));
		gameSurfaceView.destroyGameThread();
		setLevelSelectionContentView();
	}	
}
