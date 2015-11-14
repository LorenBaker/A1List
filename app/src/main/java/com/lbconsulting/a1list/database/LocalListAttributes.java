package com.lbconsulting.a1list.database;

import android.graphics.drawable.GradientDrawable;

import com.lbconsulting.a1list.classes.CommonMethods;
import com.lbconsulting.a1list.classes.MyEvents;
import com.lbconsulting.a1list.classes.MyLog;
import com.lbconsulting.a1list.classes.MySettings;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * A class to hold local A1List Attributes.
 */

@ParseClassName("ListAttributes")
public class LocalListAttributes  {

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
//    private String nameLowercase;


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
//        this.nameLowercase = name.toLowerCase();
    }

//    public String getNameLowercase() {
//        return nameLowercase;
//    }

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
}
