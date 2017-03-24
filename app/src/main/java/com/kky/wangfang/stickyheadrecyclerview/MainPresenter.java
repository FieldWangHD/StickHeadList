package com.kky.wangfang.stickyheadrecyclerview;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainPresenter {
    private Activity mActivity;
    private LayoutInflater mInflater;
    private RecyclerView mRecyclerView;

    MainPresenter (Activity activity) {
        this.mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);

        initView();
        initListener();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.container);
    }

    private void initListener() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mRecyclerView.addItemDecoration(new StickHeadItemDecoration(mActivity,R.layout.item1,100));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.item, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.text);
        }

        public void onBind(int position) {
            tv.setText("item " + position);
        }
    }
}
