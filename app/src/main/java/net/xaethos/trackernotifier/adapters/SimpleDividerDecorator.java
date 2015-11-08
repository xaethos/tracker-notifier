package net.xaethos.trackernotifier.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleDividerDecorator extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mDividerHeight;

    public SimpleDividerDecorator(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        mDividerHeight = mDivider == null ? 0 : mDivider.getIntrinsicHeight();
        a.recycle();
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) > 0) {
            outRect.set(0, mDividerHeight, 0, 0);
        }
    }

    @Override
    public void onDraw(
            Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDividerHeight == 0) return;

        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) < 1) continue;

            final int left = manager.getDecoratedLeft(child);
            final int right = manager.getDecoratedRight(child);
            final int top = manager.getDecoratedTop(child);
            final int bottom = top + mDividerHeight;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
