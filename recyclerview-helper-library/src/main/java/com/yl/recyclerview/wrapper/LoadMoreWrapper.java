package com.yl.recyclerview.wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yl.recyclerview.R;

/**
 * Pull up to load more
 * Created by yangle on 2017/10/26.
 * <p>
 * Website：http://www.yangle.tech
 * <p>
 * GitHub：https://github.com/alidili
 * <p>
 * CSDN：http://blog.csdn.net/kong_gu_you_lan
 * <p>
 * JianShu：http://www.jianshu.com/u/34ece31cd6eb
 */

public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Origin adapter
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    // General view
    private final int TYPE_ITEM = 1;
    // Footer view
    private final int TYPE_FOOTER = 2;
    // The current load state, the default is loaded to complete
    private int loadState = 2;
    // Loading
    public final int LOADING = 1;
    // Load done
    public final int LOADING_COMPLETE = 2;
    // Load end
    public final int LOADING_END = 3;

    public LoadMoreWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        // The last item is set to footer view
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a different view by judging the display type
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_refresh_footer, parent, false);
            return new FooterViewHolder(view);
        } else {
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (loadState) {
                case LOADING: // Loading
                    footerViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footerViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footerViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // Load done
                    footerViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footerViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footerViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // Load end
                    footerViewHolder.pbLoading.setVisibility(View.GONE);
                    footerViewHolder.tvLoading.setVisibility(View.GONE);
                    footerViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        } else {
            adapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        // GridLayoutManager
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // If the current position is footer view, then the item occupy two cells
                    // Normal item occupy a cell
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FooterViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    /**
     * Set the pull-up state
     *
     * @param loadState 0.Loading 1.Load done 2.Load end
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }
}
