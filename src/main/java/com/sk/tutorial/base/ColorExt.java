package com.sk.tutorial.base;

public class ColorExt {

    public static float calculateDeltaFunc(float val_from, float val_to, float percent) {
        float delta = val_from + (val_to - val_from) * percent;
        return delta;
    }

    public static Color4F linearGradient(Color4F from, Color4F to, int total, int idx) {
        float percent = idx * 1.0f / total;
        Color4F result = new Color4F();
        result.r = calculateDeltaFunc(from.r, to.r, percent);
        result.g = calculateDeltaFunc(from.g, to.g, percent);
        result.b = calculateDeltaFunc(from.b, to.b, percent);
        result.a = calculateDeltaFunc(from.a, to.a, percent);
        return result;
    }

}
