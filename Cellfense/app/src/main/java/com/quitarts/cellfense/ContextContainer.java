package com.quitarts.cellfense;

import android.content.Context;

public class ContextContainer { 
    private static Context context;

    private ContextContainer() {
    }

    public static synchronized void setApplicationContext(Context context) {
    	ContextContainer.context = context;   
    }

    public static synchronized Context getApplicationContext() {
        return context;
    }
}