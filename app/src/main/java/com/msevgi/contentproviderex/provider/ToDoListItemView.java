package com.msevgi.contentproviderex.provider;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.msevgi.contentproviderex.R;

// Sometimes none of the built-in controls meet your needs.
public class ToDoListItemView extends TextView {
    public ToDoListItemView(Context context) {
        super(context);
    }

    public ToDoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // We override onDraw method to modify the View's appearance
    @Override
    public void onDraw(Canvas canvas) {
        // Get a reference to resources table.
        final Resources resources = getResources();

        // Anti-aliasing is important when drawing text as anti-aliased
        // text is significantly easier to read.
        final Paint marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Margin paint brush
        marginPaint.setColor(resources.getColor(R.color.margin_color));

        final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Line paint brush
        linePaint.setColor(resources.getColor(R.color.line_color));

        // Get the paper background color
        final int paperColor = resources.getColor(R.color.paper_color);

        // Get the margin width.
        final int marginWidth = (int) resources.getDimension(R.dimen.margin);

        // Fill  entire canvas bitmap with paper color.
        canvas.drawColor(paperColor);

        // Draw ruled line
        final int lineX1 = 0;
        final int lineY1 = getMeasuredHeight();
        final int lineX2 = getMeasuredWidth();
        final int lineY2 = getMeasuredHeight();
        canvas.drawLine(lineX1, lineY1, lineX2, lineY2, linePaint);

        // Draw margin
        final int marginX1 = marginWidth;
        final int marginY1 = 0;
        final int marginX2 = marginWidth;
        final int marginY2 = getMeasuredHeight();
        canvas.drawLine(marginX1, marginY1, marginX2, marginY2, marginPaint);

        // Move the text across from the margin
        canvas.translate(marginWidth, 0);

        // Render the text as usual using the TextView base class.
        super.onDraw(canvas);
    }
}
