package com.github.teocci.android.bluetoothrecycleview.views;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.github.teocci.android.bluetoothrecycleview.interfaces.StateDisplay;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-06
 */
public class DefaultLoadingState implements StateDisplay
{
    /* Used to paint the title text */
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /* Stores the title text for the loading state */
    private final String title;
    /* Used to animate the title text */
    private ValueAnimator anim;


    public DefaultLoadingState(@NonNull Context c, @NonNull String title)
    {
        this.title = title;

        // Setup the default title paint
        this.textPaint.setColor(Color.parseColor("#212121"));
        this.textPaint.setTextSize(21f * c.getResources().getDisplayMetrics().scaledDensity);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDrawState(final DeviceRecycleView rv, Canvas canvas)
    {
        canvas.drawText(title,
                (rv.getMeasuredWidth() >> 1),
                (rv.getMeasuredHeight() >> 1),
                textPaint);

        // Setup animator, if necessary
        if (anim == null) {
            this.anim = ObjectAnimator.ofObject(textPaint, "color", new ArgbEvaluator(),
                    Color.parseColor("#E0E0E0"), Color.parseColor("#BDBDBD"), Color.parseColor("#9E9E9E"));
            this.anim.setDuration(900);
            this.anim.setRepeatMode(ValueAnimator.REVERSE);
            this.anim.setRepeatCount(ValueAnimator.INFINITE);
            this.anim.addUpdateListener(animation -> rv.invalidate());
            this.anim.start();
        }
    }
}
