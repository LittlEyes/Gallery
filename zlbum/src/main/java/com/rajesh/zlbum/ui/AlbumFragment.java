package com.rajesh.zlbum.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rajesh.zlbum.ui.adapter.AlbumAdapter;
import com.rajesh.zlbum.widget.AlbumViewPager;
import com.rajesh.zlbum.widget.pull.OnPullProgressListener;

import java.util.ArrayList;

/**
 * desc
 *
 * @author zhufeng on 2018/1/26
 */
public class AlbumFragment extends Fragment {
    private static final String INTENT_IMAGE = "image";
    private static final String INTENT_INDEX = "index";
    private static final int BLACK = 0xFF000000;
    private AlbumViewPager mAlbumView;
    private AlbumAdapter mAdapter;
    private ArrayList<Uri> mDataList = new ArrayList<>();
    private int mCurrIndex = 0;
    private OnAlbumEventListener mListener;

    public static AlbumFragment newInstance(ArrayList<String> data) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(INTENT_IMAGE, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AlbumFragment newInstance(ArrayList<String> data, int index) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(INTENT_IMAGE, data);
        bundle.putInt(INTENT_INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDataList.clear();
            ArrayList<String> mDataTemp = getArguments().getStringArrayList(INTENT_IMAGE);
            if (mDataTemp != null) {
                for (int i = 0; i < mDataTemp.size(); i++) {
                    mDataList.add(Uri.parse(mDataTemp.get(i)));
                }
            }
            mCurrIndex = getArguments().getInt(INTENT_INDEX, 0);
            mCurrIndex = mCurrIndex < 0 ? 0 : mCurrIndex;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAlbumView = new AlbumViewPager(getContext());
        mAlbumView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mAlbumView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAlbumView.setBackgroundColor(BLACK);
        mAlbumView.getBackground().setAlpha(255);

        mAdapter = new AlbumAdapter(getContext(), mDataList);
        mAlbumView.setPageMargin(30);
        mAlbumView.setAdapter(mAdapter);
        mAlbumView.setOffscreenPageLimit(1);
        if (mCurrIndex < mDataList.size()) {
            mAlbumView.setCurrentItem(mCurrIndex, false);
        }

        mAlbumView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrIndex = position;
                if (mListener != null) {
                    mListener.onPageChanged(mCurrIndex);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAlbumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        mAlbumView.setOnPullProgressListener(new OnPullProgressListener() {
            @Override
            public void startPull() {
                if (mListener != null) {
                    mListener.onStartPull();
                }
            }

            @Override
            public void onProgress(float progress) {
                mAlbumView.setBackgroundColor(BLACK);
                mAlbumView.getBackground().setAlpha((int) (progress * 255));
            }

            @Override
            public void stopPull(boolean isFinish) {
                if (isFinish) {
                    if (mListener != null) {
                        mListener.onPullFinished();
                    }
                } else {
                    mAlbumView.setBackgroundColor(BLACK);
                    mAlbumView.getBackground().setAlpha(255);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mListener = null;
    }

    public void setOnAlbumEventListener(OnAlbumEventListener l) {
        this.mListener = l;
    }

    public interface OnAlbumEventListener {
        void onClick();

        void onPageChanged(int page);

        void onStartPull();

        void onPullFinished();
    }
}
