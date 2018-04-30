package br.ciceromoura.loaderrecycler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * @param <T>  Entity model to be  loaded
 * @param <VH> View Holder to bind T properties
 */
public abstract class BaseLoaderRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements
        LoaderManager.LoaderCallbacks<List<T>> {


    private LoaderManager mLoaderManager;
    private int mLoaderId;
    private List<T> mItems;
    private OnLoaderListener mOnLoaderListener;
    private Context mContext;

    /**
     * Used to call loader listener methods
     */
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * It has to be passed this parameters
     *
     * @param context       The context to be used by the loader
     * @param loaderManager Activity/Fragment support loader manager
     * @param loaderId      Unique id to the loader
     */
    public BaseLoaderRecyclerViewAdapter(@NonNull Context context, @NonNull LoaderManager loaderManager, int loaderId) {
        this.mContext = context;
        this.mLoaderManager = loaderManager;
        this.mLoaderId = loaderId;
    }

    //region GETTERS

    public LoaderManager getLoaderManager() {
        return mLoaderManager;
    }

    public int getLoaderId() {
        return mLoaderId;
    }

    public List<T> getItems() {
        return mItems;
    }

    public Context getContext() {
        return mContext;
    }

    public OnLoaderListener getOnLoaderListener() {
        return mOnLoaderListener;
    }

    public void setOnLoaderListener(OnLoaderListener onLoaderListener) {
        this.mOnLoaderListener = onLoaderListener;
    }

    //endregion

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        onBindViewHolder(holder, mItems == null ? null : mItems.get(position), position);
    }

    public abstract void onBindViewHolder(@NonNull VH holder, T item, int position);

    @Override
    public final int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    //region LOADER

    public final void startLoader() {
        startLoader(null);
    }

    public final void startLoader(Bundle args) {
        mLoaderManager.initLoader(mLoaderId, args, this);
    }

    public final void restartLoader() {
        restartLoader(null);
    }

    public final void restartLoader(Bundle args) {

        if (mLoaderManager.getLoader(mLoaderId) == null) {
            startLoader(args);
        } else {
            mLoaderManager.restartLoader(mLoaderId, args, this);
        }
    }

    public final void destroyLoader() {
        mLoaderManager.destroyLoader(mLoaderId);
    }


    @NonNull
    @Override
    public final Loader<List<T>> onCreateLoader(int id, final @Nullable Bundle args) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mOnLoaderListener != null) {
                    mOnLoaderListener.onLoaderLoading();
                }
            }
        });

        return new InnerAsyncTaskLoader<>(mContext, new InnerAsyncTaskLoader.onLoadDataBackgroundCallback<T>() {
            @Override
            public List<T> onLoadDataInBackground() {
                return BaseLoaderRecyclerViewAdapter.this.onLoadDataInBackground(args);
            }
        });
    }

    @Override
    public final void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> data) {

        mItems = data;
        notifyDataSetChanged();

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mOnLoaderListener != null) {
                    mOnLoaderListener.onLoaderFinished();
                }
            }
        });
    }

    @Override
    public final void onLoaderReset(@NonNull Loader<List<T>> loader) {
    }

    /**
     * @return list of T items to be shown on the RecyclerView
     */
    @NonNull
    protected abstract List<T> onLoadDataInBackground(@Nullable Bundle args);

    //endregion

    /**
     * Loader callback
     */
    public interface OnLoaderListener {

        /**
         * Tells that a loading has started
         * It is useful to change the UI like showing a progressbar horizontal
         */
        void onLoaderLoading();

        /**
         * Tells that the load has been done
         * It is useful to change the UI like showing a progressbar horizontal
         */
        void onLoaderFinished();
    }

    /**
     * Inner loader to be used if any loader is returned by the onCreateLoader method
     *
     * @param <T> Entity model to be  loaded
     */
    static class InnerAsyncTaskLoader<T> extends BaseAsyncTaskLoader<List<T>> {

        private onLoadDataBackgroundCallback<T> onLoadDataBackgroundCallback;

        InnerAsyncTaskLoader(@NonNull Context context, @NonNull InnerAsyncTaskLoader.onLoadDataBackgroundCallback<T> onLoadDataBackgroundCallback) {
            super(context);
            this.onLoadDataBackgroundCallback = onLoadDataBackgroundCallback;
        }


        @Override
        public List<T> loadInBackground() {
            return onLoadDataBackgroundCallback.onLoadDataInBackground();
        }

        public interface onLoadDataBackgroundCallback<T> {
            List<T> onLoadDataInBackground();
        }
    }
}
