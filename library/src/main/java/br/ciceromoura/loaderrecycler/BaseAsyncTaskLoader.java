package br.ciceromoura.loaderrecycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;


public abstract class BaseAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    // We hold a reference to the Loader’s data here.
    private T mData;
    protected String TAG = "BaseAsyncTaskLoader";

    /**
     * Quem herdar dessa classe, tera acesso apenas para modificar o objeto
     * mas nao para informar um novo
     * Dessa forma, ele podera adaptar para o caso que estiver
     * trabalhando atualmente, mas sem mudar o funcionamento basico
     */

    public BaseAsyncTaskLoader(@NonNull Context context) {
        super(context);

    }

    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /**
     * ************************************************
     */

    @Override
    public abstract T loadInBackground();


    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /**
     * ****************************************************
     */

    @Override
    public final void deliverResult(T data) {

        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        T oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }


    private void releaseResources(T data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /*********************************************************/
    /** (3) Implement the Loader’s state-dependent behavior **/
    /**
     * *****************************************************
     */

    @Override
    protected final void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }


    @Override
    protected final void onStopLoading() {

        // O loader está parado, por isso, devemos tentar cancelar o
        // carregamento atual (se houver).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.

    }

    @Override
    public final void onCanceled(T data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    /**
     * Retorna os dados (podem estar nulos)
     *
     * @return
     */
    public final T getData() {
        return mData;
    }
}
