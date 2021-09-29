package com.oosl.colorostool.util;

public final class PrStackTrace {
    /**
     * print StackTrace in XposedBridge-log
     */
    private void PrStackTrace(){
        String tag = "ST";
        Log.d(tag,"Dump Stack:---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        for (int i =0; i< stackElements.length; i++){
            Log.d(tag,
                    i + ":"+ stackElements[i].getMethodName()
                            +" in "+stackElements[i].getFileName()
                            +":"+stackElements[i].getLineNumber()
                            +" -> "+stackElements[i].getClassName());
        }
        Log.d(tag,"Dump Stack:---------------over----------------");
    }
}
