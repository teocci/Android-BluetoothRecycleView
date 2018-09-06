package com.github.teocci.android.bluetoothrecycleview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import com.github.teocci.android.bluetoothrecycleview.interfaces.StateDisplay;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-06
 */
public class DefaultEmptyState implements StateDisplay
{
    /* Immutable paint objects to draw the text */
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint subtitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    /* Immutable title text for empty state */
    private final String title;
    /* Immutable subtitle text for empty state */
    private final String subtitle;
    /* Immutable spacing for text */
    private final int spacing;


    public DefaultEmptyState(@NonNull Context c, @NonNull String title, @Nullable String subtitle)
    {
        this.title = title;
        this.subtitle = subtitle;

        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        this.spacing = (int) (4f * dm.density);

        // Setup default title paint
        this.titlePaint.setColor(Color.parseColor("#212121"));
        this.titlePaint.setTextSize(21f * dm.scaledDensity);
        this.titlePaint.setTextAlign(Paint.Align.CENTER);

        // Setup default subtitle paint
        this.subtitlePaint.setColor(Color.parseColor("#757575"));
        this.subtitlePaint.setTextSize(16f * dm.scaledDensity);
        this.subtitlePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDrawState(DeviceRecycleView rv, Canvas canvas)
    {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        float dx = (width >> 1);
        float dy = (height >> 1);

        // Draw the title text first, it will be in the exact center
        canvas.drawText(title, dx, dy, titlePaint);

        // Draw the subtitle text above the title text in the horizontal-center, if possible
        if (subtitle != null) {
            dy -= titlePaint.getTextSize() + spacing;
            canvas.drawText(subtitle, dx, dy, subtitlePaint);
        }
    }
}
