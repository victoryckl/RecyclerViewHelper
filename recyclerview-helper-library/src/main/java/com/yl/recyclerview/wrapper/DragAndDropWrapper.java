package com.yl.recyclerview.wrapper;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.yl.recyclerview.helper.ItemMoveCallback;
import com.yl.recyclerview.listener.OnCustomClickListener;
import com.yl.recyclerview.listener.OnItemClickListener;

import java.util.Collections;
import java.util.List;

/**
 * An adapter can make {@link RecyclerView} basic drag & drop.
 * <p>
 * Created by yangle on 2017/11/17.
 * Website：http://www.yangle.tech
 */
public class DragAndDropWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemMoveCallback.ItemMoveListener {

    // Origin adapter
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    // Data list
    private List<?> mDataList;
    // ItemTouchHelper
    private ItemTouchHelper mItemTouchHelper;
    // Default long click delay 200ms.
    private long mDelay = 200;
    // Item click listener.
    private OnItemClickListener mItemClickListener;

    public DragAndDropWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, List<?> dataList) {
        this.mAdapter = adapter;
        this.mDataList = dataList;
    }

    public DragAndDropWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, List<?> dataList,
                              long delay) {
        this.mAdapter = adapter;
        this.mDataList = dataList;
        this.mDelay = delay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        mAdapter.onBindViewHolder(holder, position);

        // Custom delay long click listener.
        // The item root view must be clickable.
        holder.itemView.setOnTouchListener(new OnCustomClickListener(mDelay) {
            @Override
            public void onLongClickListener(View view) {
                if (mItemTouchHelper != null) {
                    mItemTouchHelper.startDrag(holder);
                }
            }

            @Override
            public void onClickListener(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public boolean onItemMove(final RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target,
                              final int fromPosition, final int toPosition) {
        Collections.swap(mDataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        // Swap click listener.
        viewHolder.itemView.setOnTouchListener(new OnCustomClickListener(mDelay) {
            @Override
            public void onLongClickListener(View view) {
                if (mItemTouchHelper != null) {
                    mItemTouchHelper.startDrag(viewHolder);
                }
            }

            @Override
            public void onClickListener(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, toPosition);
                }
            }
        });
        target.itemView.setOnTouchListener(new OnCustomClickListener(mDelay) {
            @Override
            public void onLongClickListener(View view) {
                if (mItemTouchHelper != null) {
                    mItemTouchHelper.startDrag(viewHolder);
                }
            }

            @Override
            public void onClickListener(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, fromPosition);
                }
            }
        });
        return true;
    }

    /**
     * Attach to RecyclerView for drag & drop.
     *
     * @param recyclerView RecyclerView
     */
    public void attachToRecyclerView(RecyclerView recyclerView) {
        attachToRecyclerView(recyclerView, false);
    }

    /**
     * Attach to RecyclerView for drag & drop.
     *
     * @param recyclerView RecyclerView
     * @param isFreedom    true: item can move up|down|left|right
     *                     false: item can move up|down
     */
    public void attachToRecyclerView(RecyclerView recyclerView, boolean isFreedom) {
        ItemTouchHelper.Callback callback = new ItemMoveCallback(this, isFreedom);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Register a callback to be invoked when this view is clicked. If this view is not
     * clickable, it becomes clickable.
     *
     * @param onItemClickListener The callback that will run
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }
}
