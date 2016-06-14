package com.wiikzz.ikz.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.wiikzz.ikz.R;
import com.wiikzz.library.ui.BaseActivity;
import com.wiikzz.library.widget.CirclePageIndicator;

/**
 * Created by wiikii on 16/6/14.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class GuideActivity extends BaseActivity {

    private GuidePositionImageIds [] sGuideImageIds = {
            new GuidePositionImageIds(R.drawable.guide_bg_image_1,
                    R.drawable.guide_content_word_1, R.drawable.guide_content_image_1),
            new GuidePositionImageIds(R.drawable.guide_bg_image_2,
                    R.drawable.guide_content_word_2, R.drawable.guide_content_image_2),
            new GuidePositionImageIds(R.drawable.guide_bg_image_3,
                    R.drawable.guide_content_word_3, R.drawable.guide_content_image_3),
            new GuidePositionImageIds(R.drawable.guide_bg_image_4,
                    R.drawable.guide_content_word_4, R.drawable.guide_content_image_4),
    };

    private ViewPager mViewPager;
    private GuidePagerAdapter mPagerAdapter;
    private CirclePageIndicator mCirclePageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initViews(savedInstanceState);
        loadViewData();
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide);
        mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.guide_view_pager_indicator);
        mViewPager = (ViewPager) findViewById(R.id.guide_view_pager);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void transformPage(View view, float position) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    View dummyImageView = view.findViewById(R.id.guide_view_item_bg);
                    if (dummyImageView == null) {
                        return;
                    }

                    int pageWidth = view.getWidth();
                    if (position < -1) { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        view.setAlpha(1);
                    } else if (position <= 1) { // [-1,1]
                        dummyImageView.setTranslationX(-position * (pageWidth/2)); //Half the normal speed
                    } else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.setAlpha(1);
                    }
                }
            }
        });
    }

    @Override
    protected void loadViewData() {
        mPagerAdapter = new GuidePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mCirclePageIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected class GuidePositionImageIds {
        private int bgImageId;
        private int contentWordId;
        private int contentImageId;

        public GuidePositionImageIds(int bgImageId, int contentWordId, int contentImageId) {
            this.bgImageId = bgImageId;
            this.contentWordId = contentWordId;
            this.contentImageId = contentImageId;
        }

        public int getBgImageId() {
            return bgImageId;
        }

        public int getContentWordId() {
            return contentWordId;
        }

        public int getContentImageId() {
            return contentImageId;
        }
    }

    protected class GuidePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return sGuideImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View pagerItemView = getLayoutInflater().inflate(R.layout.guide_view_item, null);
            ImageView bgView = (ImageView) pagerItemView.findViewById(R.id.guide_view_item_bg);
            ImageView wordView = (ImageView) pagerItemView.findViewById(R.id.guide_view_item_word);
            ImageView contentView = (ImageView) pagerItemView.findViewById(R.id.guide_view_item_content);
            Button buttonView = (Button) pagerItemView.findViewById(R.id.guide_view_item_button);
            if(position == sGuideImageIds.length - 1) {
                buttonView.setVisibility(View.VISIBLE);
            } else {
                buttonView.setVisibility(View.GONE);
            }

            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(GuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            GuidePositionImageIds imageIds = sGuideImageIds[position];
            bgView.setBackgroundResource(imageIds.getBgImageId());
            wordView.setBackgroundResource(imageIds.getContentWordId());
            contentView.setBackgroundResource(imageIds.getContentImageId());

            container.addView(pagerItemView);

            return pagerItemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(object != null && object instanceof View) {
                container.removeView((View)object);
            }
        }
    }
}
