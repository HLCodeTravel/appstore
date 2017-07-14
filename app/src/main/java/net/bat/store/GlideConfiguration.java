package net.bat.store;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

/**
 * glide setting
 * Created by libingbing
 */
public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 配置图片将缓存到SD卡
        ExternalCacheDiskCacheFactory externalCacheDiskCacheFactory = new ExternalCacheDiskCacheFactory(context);
        builder.setDiskCache(externalCacheDiskCacheFactory);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // 配置使用OKHttp来请求网络
    }
}
