package com.quitarts.cellfense;

import com.quitarts.cellfense.SoundManager.MusicType;
import com.quitarts.cellfense.ui.GameActivity;
import com.quitarts.cellfense.ui.LevelActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private GameActivity levelSelection;
	private ProgressDialog loadingDialog;
	private GameControl gameControl;
	private Thread gameThread;	
	
	private final Handler gameSurfaceViewHandler = new Handler() {
        public void handleMessage(Message msg) {
        	if(msg.what == Utils.DIALOG_PLAYAGAIN_ID){
        		/**
        		 * Misma observacion que en DIALOG_LEVEL_PASS_ID
        		 */
        		gameControl.showGameOverDialog();
        	}
        	else if(msg.what == Utils.DIALOG_LEVEL_PASS_ID){
        		gameControl.showLevelWinDialog();
        		/**
        		 * Podria ser la siguiente linea (pasando el metodo a esta clase) pero 
        		 * debido a que hace modificaciones al gameControl y utiliza informacion del gameWorld
        		 * por comodidad se lo deja asi.
        		 * showLevelWinDialog(ContextContainer.getApplicationContext());
        		 */
        	}
        	else if(msg.what == Utils.DIALOG_GAMEOVER_ID){
        		stopThreadGameAndShowLevels();
        	}
        	else if(msg.what == Utils.DIALOG_LOADING_OFF_ID){
        		loadingDialog.cancel();
        	}    	
        	else if(msg.what == Utils.POST_SCORE_ID) {        		
        		levelSelection.postScore("Cellfense", "Cellfense: Level: " + msg.arg1 + " - Score: " + msg.arg2);
        	}
        	else if(msg.what == Utils.TUTORIAL_S5_L1_POPUP_WIN){
        		gameControl.showTutorialLevel1Win();
        	}
        	else if(msg.what == Utils.TUTORIAL_S11_L2_POPUP_LOSE){
        		gameControl.showTutorialLevel2Lose();
        	}
        	else if(msg.what == Utils.TUTORIAL_S11_L2_POPUP_WIN){
        		gameControl.showTutorialLevel2Win();
        	}
        	else if(msg.what == Utils.TUTORIAL_S19_L3_POPUP_LOSE){
        		gameControl.showTutorialLevel3Lose();
        	}
        	else if(msg.what == Utils.TUTORIAL_S19_L3_POPUP_WIN){
        		gameControl.showTutorialLevel3Win();
        	}
        	else if(msg.what == Utils.TUTORIAL_S26_L4_POPUP_LOSE){
        		gameControl.showTutorialLevel4Lose();
        	}
        	else if(msg.what == Utils.TUTORIAL_S26_L4_POPUP_WIN){
        		gameControl.showTutorialLevel4Win();
        	}
        	else if(msg.what == Utils.TUTORIAL_END){
        		gameControl.showTutorialEndPopUp();
        	}
        	else if(msg.what == Utils.NEW_UNLOCKED_ART){
        		gameControl.showNewUnlockedArtMessage();
        	}
        }	
    };
    
    public GameSurfaceView(Context context, GameActivity ls, int startLevel) {
		super(context);
		levelSelection = ls;
		showLoadingDialog(context);		
		SurfaceHolder surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		gameControl = new GameControl(surfaceHolder, gameSurfaceViewHandler, startLevel);
	}

    @Override    
    public boolean onTouchEvent(MotionEvent ev) {    	
    	switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				gameControl.eventActionDown(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				gameControl.eventActionMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				gameControl.eventActionUp(ev);
				break;	
			default:
				break;
		}    	
    	return true;
    }

	public void surfaceCreated(SurfaceHolder holder) {			
		if(gameThread == null){
			gameThread = new Thread(new Runnable() {
				  public void run() {
					  SoundManager.loadMusic();
					  SoundManager.loadSounds();
					  SoundManager.playMusic(MusicType.STRATEGY, true, true, true);
					  SoundManager.playMusic(MusicType.ACTION, true, true, true);					  					  
					  gameControl.play();
				  }
			});			
			this.gameThread.start();
		}
		else {
			gameControl.resumeFull();
			SoundManager.resumeMusics();
		}			
	}	

	public void surfaceDestroyed(SurfaceHolder holder) {
		gameControl.pauseFull();
		if(gameThread != null)
			SoundManager.pauseMusics();					
	}
	
    public GameControl getGameControl(){
    	return gameControl;
    }
	
	public Handler getGameSurfaceViewHandler(){
		return gameSurfaceViewHandler;
	}
	
	public void destroyGameThread(){
		gameControl.stop();
		try {
			 	gameThread.join();
			 	gameThread = null;
			 	SoundManager.cleanup();			 				 	
			} 
		catch (InterruptedException e)
			{
				e.printStackTrace();
			}	
	}	

	public void showLoadingDialog(Context context) {
		loadingDialog  = ProgressDialog.show(context, "", 
                 getResources().getText(R.string.loading_message), true);
	}	
	
	public boolean showExitConfirmDialog(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
    	builder.setMessage(ContextContainer.getApplicationContext().getResources().getText(R.string.exit_game_dialog_message));
	    builder.setPositiveButton(
	    		ContextContainer.getApplicationContext().getResources().getText(R.string.yes_dialog_message),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	        		stopThreadGameAndShowLevels();	        	  
	    			}
	    		});
	    builder.setNegativeButton(
	    		ContextContainer.getApplicationContext().getResources().getText(R.string.no_dialog_message),
	    		new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	        	    gameControl.resume();
	    			}
	    		});
	    	    
    	AlertDialog alert = builder.create();   
    	alert.show();
    	return true;
	}
	
    private void stopThreadGameAndShowLevels() {
    	gameControl.stop();
    	destroyGameThread();    	
    	//levelSelection.setLevelSelectionContentView();
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub		
	}
}