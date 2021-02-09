package com.tomi.ohl.szakdoga.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

// Egy RecyclerView, ami csak felül jelenít meg fading edge-t.
// Kell hozzá az xml-ben az android:requiresFadingEdge attribútum.
public class TopFadingEdgeRecyclerView extends RecyclerView {
    public TopFadingEdgeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TopFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0.0f;
    }
}
