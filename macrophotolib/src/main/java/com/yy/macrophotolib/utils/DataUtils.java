package com.yy.macrophotolib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.yy.macrophotolib.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    public static ArrayList<ImageInfo> getUrls() {
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        imageInfos.add(new ImageInfo("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1593749011&di=466378d5d474f65662a7f846aeb1a60d&src=http://wx3.sinaimg.cn/orj360/005OzLj9ly1g5pnw725vrj30g01hce5k.jpg"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_d2c3310015484b68e11aa96d43f21498.jpg"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_775472d6046aa31457c8c6237d5ec51c.jpg"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_fc26e410efed1bb75cd985197496e839.jpg"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_d09749c885ea941a254e8c3f557189d0.png"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_367232bab8030850ff5e413b4af132d4.jpg"));
        imageInfos.add(new ImageInfo("https://cdn3.kouling.cn/media/pic/oa_d72b767ab6466a13b47da4fc81ce2675.jpg"));
        imageInfos.add(new ImageInfo("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=244985024,1557281236&fm=15&gp=0.jpg"));
        return imageInfos;
    }

    public static ArrayList<String> getSystemPhotoList(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return null; // 没有图片
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                result.add(path);
                Log.e("test", path);
            }
        }

        return result;
    }
}
