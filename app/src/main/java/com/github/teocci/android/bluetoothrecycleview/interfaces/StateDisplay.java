package com.github.teocci.android.bluetoothrecycleview.interfaces;

import android.graphics.Canvas;

import com.github.teocci.android.bluetoothrecycleview.views.DeviceRecycleView;

/**
 * Defines methods for our states that will be drawn.
 * <p>
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-06
 */
public interface StateDisplay
{
    void onDrawState(DeviceRecycleView rv, Canvas canvas);
}
