package com.quitarts.cellfense;

import android.content.Context;

public class ContextContainer {
    private static Context context;

    public static void setContext(Context context) {
        ContextContainer.context = context;
    }

    public static synchronized Context getContext() {
        return ContextContainer.context;
    }
}