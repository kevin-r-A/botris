package com.csl.cs108ademoapp.TransicionSplash;

import android.graphics.RectF;

/**
 * Created by Mauricio on 10/11/2016.
 */
public interface TransitionGenerator {
    /**
     * Generates the next transition to be played by the {@link KenBurnsView}.
     * @param drawableBounds the bounds of the drawable to be shown in the {@link KenBurnsView}.
     * @param viewport the rect that represents the viewport where
     * the transition will be played in. This is usually the bounds of the
     * {@link KenBurnsView}.
     * @return a {@link Transition} object to be played by the {@link KenBurnsView}.
     */
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport);
}
