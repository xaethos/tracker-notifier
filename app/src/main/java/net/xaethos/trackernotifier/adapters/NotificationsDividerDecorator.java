package net.xaethos.trackernotifier.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.xaethos.trackernotifier.R;

public class NotificationsDividerDecorator extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mDividerHeight;

    public NotificationsDividerDecorator(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        mDividerHeight = mDivider == null ? 0 : mDivider.getIntrinsicHeight();
        a.recycle();
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childPos = parent.getChildAdapterPosition(view);
        switch (parent.getAdapter().getItemViewType(childPos)) {
            case R.layout.item_notification:
            case R.layout.item_story:
                outRect.set(0, mDividerHeight, 0, 0);
        }
    }

    @Override
    public void onDraw(
            Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDividerHeight == 0) return;

        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childCount = parent.getChildCount();
        final Resources resources = parent.getContext().getResources();
        final int leftMargin =
                resources.getDimensionPixelOffset(R.dimen.divider_notification_left_margin);
        final int rightMargin =
                resources.getDimensionPixelOffset(R.dimen.divider_notification_right_margin);

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            final int left;
            final int right;
            switch (parent.getAdapter().getItemViewType(parent.getChildAdapterPosition(child))) {
                case R.layout.item_project:
                    continue;
                case R.layout.item_notification:
                    left = manager.getDecoratedLeft(child) + leftMargin;
                    right = manager.getDecoratedRight(child) - rightMargin;
                    break;
                case R.layout.item_story:
                default:
                    left = manager.getDecoratedLeft(child);
                    right = manager.getDecoratedRight(child);
            }

            final int top = manager.getDecoratedTop(child);
            final int bottom = child.getTop();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
