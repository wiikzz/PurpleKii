package com.wiikzz.ikz.ui.promotion;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by wiikii on 16/5/13.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class PromotionRequest extends Request<Promotion> {
    /** Default charset for WeatherEntity request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    private Response.Listener<Promotion> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public PromotionRequest(int method, String url, Response.Listener<Promotion> listener,
                                Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public PromotionRequest(String url, Response.Listener<Promotion> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        mListener = null;
    }

    @Override
    protected void deliverResponse(Promotion response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<Promotion> parseNetworkResponse(NetworkResponse response) {
        try {
            String dataString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Promotion promotion = parsePromotion(dataString);
            if(promotion == null) {
                return Response.error(new ParseError());
            }
            return Response.success(promotion, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private Promotion parsePromotion(String jsonString) {
        if(TextUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String promotionId = jsonObject.getString("aid");
            String promotionTitle = jsonObject.getString("title");
            String promotionImage = jsonObject.getString("picurl");
            String promotionLink = jsonObject.getString("linkurl");
            String startDate = jsonObject.getString("startdate");
            String endDate = jsonObject.getString("enddate");

            Promotion promotion = new Promotion();
            promotion.setPromotionId(promotionId);
            promotion.setPromotionTitle(promotionTitle);
            promotion.setPromotionImage(promotionImage);
            promotion.setPromotionLink(promotionLink);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);

            return promotion;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
