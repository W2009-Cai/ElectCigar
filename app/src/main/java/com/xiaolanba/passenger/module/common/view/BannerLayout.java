package com.xiaolanba.passenger.module.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framework.common.base.IBasePagerAdapter;
import com.framework.common.utils.IDisplayUtil;
import com.framework.common.utils.ILog;
import com.framework.common.view.autoscrollviewpager.AutoScrollViewPager;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.logic.control.LBController;

import java.util.List;


/**
 * 把banner广告抽离出来
 *
 * @author xutingz
 * @company xiaolanba.com
 */

public class BannerLayout extends LinearLayout {
    private static final String TAG = "BannerLayout";
    private AutoScrollViewPager mViewPager;
    private View mBannerLayout;
    private LinearLayout mPointLayout;
    private Context mContext;
    private BannerListAdapter mBannerPageAdapter;
    private View mTopSpace;
    private boolean needShowSpaceWhenHasAd = false;

    private String bannerFromWhere = null;

    public void setBannerFromWhere(String where) {
        bannerFromWhere = where;
    }

    /**
     * 有广告时，是否要显示顶部10dp间距
     *
     * @param needShow
     */
    public void setNeedShowSpaceWhenHasAd(boolean needShow) {
        needShowSpaceWhenHasAd = needShow;
    }

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View banner = mInflater.inflate(R.layout.banner_home_header_layout, null);
        mBannerLayout = banner.findViewById(R.id.ad_layout);
        mBannerLayout.getLayoutParams().height = IDisplayUtil.getScreenWidth(context) / 2;
        mViewPager = (AutoScrollViewPager) banner.findViewById(R.id.viewpager);
        mViewPager.setScrollFactgor(5);
        mPointLayout = (LinearLayout) banner.findViewById(R.id.point_layout);
        mTopSpace = banner.findViewById(R.id.top_space_view);
        mBannerLayout.setVisibility(View.GONE);
        mTopSpace.setVisibility(View.GONE);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(banner, params);
    }

    /**
     * 资讯和圈子的banner广告比例不一样
     *
     * @param scale
     */
    public void adjustHeight(float scale) {
        mBannerLayout.getLayoutParams().height = (int) (IDisplayUtil.getScreenWidth(mContext) * scale);
    }

    /**
     * 返回数据时，判断banner数据与设置数据 相同 就不进行重复设置
     *
     * @param list
     * @return
     */
    public boolean checkBanner(List<Object> list) {
        if (mBannerPageAdapter != null && mBannerPageAdapter.getList() != null) {
            String data = list == null ? "" : list.toString();
            String oldData = mBannerPageAdapter.getList().toString();
            if (data.equals(oldData)) {
                ILog.w(TAG, "----checkBanner return true");
                return true;
            }
        }
        ILog.w(TAG, "----checkBanner return false");
        return false;
    }

    public void setBannerPagerAdapter(final List<Object> list, boolean isFirst) {
        if (mViewPager == null) {
            return;
        }
        mPointLayout.removeAllViews();
        if (list != null && !list.isEmpty()) {
            mBannerLayout.setVisibility(View.VISIBLE);
            mTopSpace.setVisibility(needShowSpaceWhenHasAd ? View.VISIBLE : View.GONE);
            final int size = list.size();
            mPointLayout.setVisibility(size == 1 ? View.GONE : View.VISIBLE);
            for (int i = 0; i < size; i++) {
                ImageView view = new ImageView(mContext);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setTag(i);
                int pointWidth = IDisplayUtil.dip2px(mContext, 4);
                int distance = IDisplayUtil.dip2px(mContext, 4);
                LayoutParams param = new LayoutParams(pointWidth, pointWidth);
                param.setMargins(distance, 0, distance, 0);
                view.setLayoutParams(param);
                view.setBackgroundResource(i == 0 ? R.drawable.banner_point2 : R.drawable.banner_point1);
                mPointLayout.addView(view);
            }
            mViewPager.stopAutoScroll();
            if (mBannerPageAdapter == null) {
                mBannerPageAdapter = new BannerListAdapter(mContext);
            }
            mBannerPageAdapter.setList(list);
            mViewPager.setAdapter(mBannerPageAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < size; i++) {
                        ImageView dianImg = (ImageView) mPointLayout.findViewWithTag(i);
                        if (dianImg != null) {
                            dianImg.setBackgroundResource(position == i ? R.drawable.banner_point2 : R.drawable.banner_point1);
                        }
                    }
                    if (mBannerPageAdapter != null) {
                        mBannerPageAdapter.showStatis(position);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            if (isFirst) {
                mViewPager.setVisibility(View.INVISIBLE);
                LBController.MainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mViewPager != null) {
                            mViewPager.setVisibility(View.VISIBLE);
                        }
                    }
                }, 35);
            }
            mViewPager.startAutoScroll(5000);

            if (mBannerPageAdapter != null) {
                mBannerPageAdapter.showStatis(0);
            }
        } else {
            mBannerLayout.setVisibility(View.GONE);
            mTopSpace.setVisibility(View.GONE);
            mViewPager.stopAutoScroll();
        }
    }

    /**
     * 所在的activity执行了onResume
     */
    public void onResume() {
        if (mViewPager != null && mViewPager.isHasRemoved() && !mViewPager.isDetachedFromWindow()) {
            mViewPager.startAutoScroll();
        }
    }

    /**
     * 所在的activity执行了onPause
     */
    public void onPause() {
        if (mViewPager != null) {
            mViewPager.removeMessages();
        }
    }

    /**
     * 所在的Fragment执行了hide
     */
    public void hide() {
        if (mViewPager != null) {
            mViewPager.removeMessages();
        }
    }

    /**
     * 所在的Fragment执行了show
     */
    public void show() {
        if (mViewPager != null && mViewPager.isHasRemoved() && !mViewPager.isDetachedFromWindow()) {
            mViewPager.startAutoScroll();
        }
    }

    public void onDestory() {
        if (mViewPager != null) {
            mViewPager.stopAutoScroll();
            mViewPager.removeHandler();
            mViewPager.setAdapter(null);
        }
        mViewPager = null;
        mBannerPageAdapter = null;
    }

    public boolean getBannerVisible() {
        return mBannerLayout.getVisibility() == View.VISIBLE;
    }

    private class BannerListAdapter extends IBasePagerAdapter<Object> {

        public BannerListAdapter(Context context) {
            super(context);
        }

        public void showStatis(int position) {
            if (position >= mList.size()) {
                return;
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            final Object bean = mList.get(position); // 真实情况下使用真实的对象和布局
            ImageView img = new ImageView(getContext());
            ((ViewPager) container).addView(img, 0);
            return img;
        }
    }
}
