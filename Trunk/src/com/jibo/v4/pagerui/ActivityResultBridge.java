package com.jibo.v4.pagerui;

import android.content.Intent;

public abstract interface ActivityResultBridge
{
  public abstract void startActivityForResult(ActivityResultReceiver paramActivityResultReceiver, Intent paramIntent, int paramInt);

  public static abstract interface ActivityResultReceiver
  {
    public abstract void registerActivityResultBridge(ActivityResultBridge paramActivityResultBridge);

    public abstract void handleActivityResult(ActivityResultReceiver paramActivityResultReceiver, int paramInt1, int paramInt2, Intent paramIntent);
  }
}

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     com.lewa.os.ui.ActivityResultBridge
 * JD-Core Version:    0.6.0
 */