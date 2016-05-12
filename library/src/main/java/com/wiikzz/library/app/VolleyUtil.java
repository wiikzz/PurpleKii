package com.wiikzz.library.app;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by wiikii on 15/12/15.
 */
public class VolleyUtil {
    public static final String TAG = VolleyUtil.class.getSimpleName();

    private static VolleyUtil mInstance = new VolleyUtil();

    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleyUtil() {
        // private constructor.
    }

    private static synchronized VolleyUtil getInstance() {
        return mInstance;
    }

    private void initInternal(Context context) {
        mContext = context;
        initRequestQueue();
        initImageLoader();
    }

    private void destroyInternal() {
        if(mRequestQueue != null) {
            mRequestQueue.stop();
        }
    }

    private void checkComplete() {
        if(mContext == null) {
            throw new IllegalStateException("VolleyUtil init failed, have you called init method?");
        }
    }

    private void initRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
    }

    private void initImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue,
                    new LruBitmapCache(mContext.getApplicationContext()));
        }
    }

    private RequestQueue getRequestQueueInternal() {
        checkComplete();
        initRequestQueue();
        return mRequestQueue;
    }

    private ImageLoader getImageLoaderInternal() {
        checkComplete();
        getRequestQueue();
        initImageLoader();
        return mImageLoader;
    }

    private <T> void addToRequestQueueInternal(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    private <T> void addToRequestQueueInternal(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private void cancelPendingRequestsInternal(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    // interface
    public static void init(Context context) {
        getInstance().initInternal(context);
    }
    public static RequestQueue getRequestQueue() {
        return getInstance().getRequestQueueInternal();
    }

    public static ImageLoader getImageLoader() {
        return getInstance().getImageLoaderInternal();
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        getInstance().addToRequestQueueInternal(req, tag);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        getInstance().addToRequestQueueInternal(req);
    }

    public static void cancelPendingRequests(Object tag) {
        getInstance().cancelPendingRequestsInternal(tag);
    }

    public static void destroy() {
        getInstance().destroyInternal();
    }
}
