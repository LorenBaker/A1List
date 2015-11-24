package com.lbconsulting.a1list.database;

import android.graphics.drawable.GradientDrawable;

import com.parse.ParseClassName;

/**
 * A class to hold local A1List Attributes.
 */

@ParseClassName("ListAttributes")
public class LocalListAttributes {

    private boolean isAttributesDirty;
    private boolean isBold;
    private boolean isChecked;
    private boolean isDefaultAttributes;
    private boolean isMarkedForDeletion;
    private boolean isTransparent;
    private float textSize;
    private int endColor;
    private int horizontalPaddingInDp;
    private int startColor;
    private int textColor;
    private int verticalPaddingInDp;
    private String name;

    public LocalListAttributes() {

    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public int getHorizontalPaddingInDp() {
        return horizontalPaddingInDp;
    }

    public void setHorizontalPaddingInDp(int horizontalPaddingInDp) {
        this.horizontalPaddingInDp = horizontalPaddingInDp;
    }

    public boolean isAttributesDirty() {
        return isAttributesDirty;
    }

    public void setIsAttributesDirty(boolean isAttributesDirty) {
        this.isAttributesDirty = isAttributesDirty;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setIsBold(boolean isBold) {
        this.isBold = isBold;
    }

    public void toggleTextStyle() {
        setIsBold(!isBold());
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isDefaultAttributes() {
        return isDefaultAttributes;
    }

    public void setIsDefaultAttributes(boolean isDefaultAttributes) {
        this.isDefaultAttributes = isDefaultAttributes;
    }

    public boolean isMarkedForDeletion() {
        return isMarkedForDeletion;
    }

    public void setIsMarkedForDeletion(boolean isMarkedForDeletion) {
        this.isMarkedForDeletion = isMarkedForDeletion;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setIsTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getVerticalPaddingInDp() {
        return verticalPaddingInDp;
    }

    public void setVerticalPaddingInDp(int verticalPaddingInDp) {
        this.verticalPaddingInDp = verticalPaddingInDp;
    }

    public GradientDrawable getBackgroundDrawable() {
        int colors[] = {getStartColor(), getEndColor()};
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
    }
}
