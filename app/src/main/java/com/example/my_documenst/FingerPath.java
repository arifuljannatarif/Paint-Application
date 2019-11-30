package com.example.my_documenst;

import android.graphics.Path;

class FingerPath {

    public int color;
    public boolean emboss;
    public boolean blur;
    public float strokeWidth;
    public Path path;

    public FingerPath(int color, boolean emboss, boolean blur, float strokeWidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEmboss(boolean emboss) {
        this.emboss = emboss;
    }

    public void setBlur(boolean blur) {
        this.blur = blur;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
