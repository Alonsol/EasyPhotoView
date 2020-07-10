package com.yy.macrophotolib;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 图片信息
 */
public class ImageInfo implements Parcelable {

    private String thumbnailUrl;// 缩略图，质量很差
    private String originUrl;// 原图或者高清图
    private String remoteUrl;//远程地址
    private String msgLocalId;
    private long fileLen;

    public ImageInfo(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }


    private boolean isCheckQrCode;  //是否检测了二维码
    private String qrCodeContent;  //二维码内容

    protected ImageInfo(Parcel in) {
        thumbnailUrl = in.readString();
        originUrl = in.readString();
        remoteUrl = in.readString();
        msgLocalId = in.readString();
        fileLen = in.readLong();
        isCheckQrCode = in.readByte() != 0;
        qrCodeContent = in.readString();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    public long getFileLen() {
        return fileLen;
    }

    public void setFileLen(long fileLen) {
        this.fileLen = fileLen;
    }

    public String getMsgLocalId() {
        return msgLocalId;
    }

    public void setMsgLocalId(String msgLocalId) {
        this.msgLocalId = msgLocalId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public boolean isCheckQrCode() {
        return isCheckQrCode;
    }

    public void setCheckQrCode(boolean checkQrCode) {
        isCheckQrCode = checkQrCode;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "thumbnailUrl='" + thumbnailUrl + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", msgLocalId='" + msgLocalId + '\'' +
                ", fileLen=" + fileLen +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnailUrl);
        dest.writeString(originUrl);
        dest.writeString(remoteUrl);
        dest.writeString(msgLocalId);
        dest.writeLong(fileLen);
        dest.writeByte((byte) (isCheckQrCode ? 1 : 0));
        dest.writeString(qrCodeContent);
    }
}