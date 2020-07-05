package com.yy.macrophotolib.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ImgOptionEntity implements Parcelable {
    private int left;
    private int top;
    private int width;
    private int height;
    private String imgUrl;

    public ImgOptionEntity() {

    }

    public ImgOptionEntity(int left,int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    protected ImgOptionEntity(Parcel in) {
        top = in.readInt();
        left = in.readInt();
        width = in.readInt();
        height = in.readInt();
        imgUrl = in.readString();
    }

    public static final Creator<ImgOptionEntity> CREATOR = new Creator<ImgOptionEntity>() {
        @Override
        public ImgOptionEntity createFromParcel(Parcel in) {
            return new ImgOptionEntity(in);
        }

        @Override
        public ImgOptionEntity[] newArray(int size) {
            return new ImgOptionEntity[size];
        }
    };

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(top);
        dest.writeInt(left);
        dest.writeInt(width);
        dest.writeInt(height);


        dest.writeString(imgUrl);
    }

}
