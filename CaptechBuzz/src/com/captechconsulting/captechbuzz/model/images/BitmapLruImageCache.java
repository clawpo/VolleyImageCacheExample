package com.captechconsulting.captechbuzz.model.images;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Basic LRU Memory cache.
 * 
 * @author Trey Robinson
 *
 */
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache{
	
	private final String TAG = this.getClass().getSimpleName();
	
	public BitmapLruImageCache(int maxSize) {
		super(maxSize);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

    @Override
    public Bitmap getBitmap(String url) {
        Log.v(TAG, "Retrieved item from Mem Cache");
        Bitmap bitmap = get(url);
        //如果没有在内存中找到则去磁盘缓存查找
        if(bitmap==null){
            bitmap = ImageCacheManager.getInstance().getBitmap(url);
            //如果磁盘缓存找到，添加到内存缓存中
            if(bitmap!=null){
                putBitmap(url,bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //在getBitmap方法返回NULL时，Volley启动下载图片的任务下载图片成功后会调用此方法
        Log.v(TAG, "Added item to Mem Cache");
        //原生的内存缓存添加图片到内存中的方法
        put(url, bitmap);
        //将图片同时添加到磁盘缓存中
        ImageCacheManager.getInstance().putBitmap(url,bitmap);
    }
}
