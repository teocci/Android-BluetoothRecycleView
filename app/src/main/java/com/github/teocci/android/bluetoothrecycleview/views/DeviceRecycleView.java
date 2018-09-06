package com.github.teocci.android.bluetoothrecycleview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.github.teocci.android.bluetoothrecycleview.interfaces.StateChangedListener;
import com.github.teocci.android.bluetoothrecycleview.interfaces.StateDisplay;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-06
 */
public class DeviceRecycleView extends RecyclerView
{
    /* Constants representing all the available possible states */
    public static final byte STATE_LOADING = 0;
    public static final byte STATE_EMPTY = 1;
    public static final byte STATE_ERROR = 2;
    public static final byte STATE_OK = 3;

    /* Stores the state the recycler is in */
    private byte state = STATE_OK;

    /* Stores all the available states that can be displayed */
    @NonNull
    private SparseArray<StateDisplay> stateDisplays = new SparseArray<>();

    /* Stores observer for changes to our state value */
    private StateChangedListener onStateChangedListener;

    public DeviceRecycleView(Context context)
    {
        this(context, null);
    }

    public DeviceRecycleView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DeviceRecycleView(Context c, @Nullable AttributeSet attrs, int defStyle)
    {
        super(c, attrs, defStyle);

        // Setup default states
        this.stateDisplays.put(STATE_LOADING, new DefaultLoadingState(c, "Loading..."));
        this.stateDisplays.put(STATE_EMPTY, new DefaultEmptyState(c, "Please swipe down to reload", "No Content"));
        this.stateDisplays.put(STATE_ERROR, new DefaultEmptyState(c, "Something Went Wrong", "Error"));
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);

        final StateDisplay display;
        synchronized (this) {
            display = stateDisplays.get(state);
        }

        if (display != null) {
            display.onDrawState(this, canvas);
        }
    }

    /**
     * Sets the state display for the given state.
     *
     * @param state   {@link #STATE_LOADING}, {@link #STATE_EMPTY},
     *                {@link #STATE_ERROR}, or {@link #STATE_OK}
     * @param display {@link StateDisplay}
     */
    public void setStateDisplay(byte state, StateDisplay display)
    {
        if (display == null) {
            throw new NullPointerException("State display cannot be null!");
        }
        this.stateDisplays.put(state, display);
        requestLayout();
        invalidate();
    }

    /**
     * Sets multiple state displays for the given states.
     *
     * @param states   Array of {@link #STATE_LOADING}, {@link #STATE_EMPTY},
     *                 {@link #STATE_ERROR}, or {@link #STATE_OK}
     * @param displays Array of {@link StateDisplay}
     */
    public void setStateDisplays(byte[] states, StateDisplay[] displays)
    {
        if (states == null || displays == null) {
            throw new NullPointerException("States or displays cannot be null!");
        }
        if (states.length != displays.length) {
            throw new IllegalArgumentException("The amount of given states do " +
                    "not correspond to the amount of given displays!");
        }
        for (int i = 0; i < states.length; i++) {
            this.stateDisplays.put(states[i], displays[i]);
        }
        requestLayout();
        invalidate();
    }

    /**
     * Removes the state display for a given state.
     *
     * @param state {@link #STATE_LOADING}, {@link #STATE_EMPTY},
     *              {@link #STATE_ERROR}, or {@link #STATE_OK}
     */
    public void removeStateDisplay(byte state)
    {
        final int index = stateDisplays.indexOfKey(state);
        if (index > -1) {
            this.stateDisplays.removeAt(index);
            requestLayout();
            invalidate();
        }
    }

    /**
     * Removes all state displays for all states.
     */
    public void clearStateDisplays()
    {
        if (stateDisplays.size() > 0) {
            this.stateDisplays.clear();
            requestLayout();
            invalidate();
        }
    }

    /**
     * Invokes the given state.
     *
     * @param state {@link #STATE_LOADING}, {@link #STATE_EMPTY},
     *              {@link #STATE_ERROR}, or {@link #STATE_OK}
     */
    public void invokeState(byte state)
    {
        if (this.state == state) { return; }
        this.state = state;
        invalidate();
        if (onStateChangedListener != null) {
            this.onStateChangedListener.onStateChanged(state);
        }
    }

    /**
     * Checks if the current state is the empty state.
     *
     * @return True if empty state
     */
    public boolean isEmptyState()
    {
        return (state == STATE_EMPTY);
    }

    /**
     * Checks if the current state is the error state.
     *
     * @return True if empty state
     */
    public boolean isErrorState()
    {
        return (state == STATE_ERROR);
    }

    /**
     * Checks if the current state is the loading state.
     *
     * @return True if empty state
     */
    public boolean isLoadingState()
    {
        return (state == STATE_LOADING);
    }

    /**
     * Checks if the current state is the ok state.
     *
     * @return True if empty state
     */
    public boolean isOkState()
    {
        return (state == STATE_OK);
    }

    public void setOnStateChangedListener(StateChangedListener listener)
    {
        this.onStateChangedListener = listener;
    }

    public StateChangedListener getOnStateChangedListener()
    {
        return onStateChangedListener;
    }
}
