package sk.upjs.ics.android.presentr;


import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Loads an object that does not need to be closed. 
 * <p>
 * Modelled after CursorLoader from Android API, according to
 * instructions from <a href="https://groups.google.com/d/msg/android-developers/J-Uql3Mn73Y/3haYPQ-pR7sJ">
 * Google Mailing list</a> 
 *
 */
public abstract class AbstractObjectLoader<T> extends AsyncTaskLoader<T> {
	public AbstractObjectLoader(Context context) {
		super(context);
	}

	private T cachedResult;

    /**
     * Retrieves the result from worker thread and caches it into an instance variable,
     * but only when the loader is started.
     * <p>
     * Handles resetting (by doing nothing).
     * <p>
     * Runs on the UI thread. 
    */
    @Override
    public void deliverResult(T result) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }
        cachedResult = result;

        if (isStarted()) {
            super.deliverResult(result);
        }
    }


    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread.
     */
    @Override
    protected void onStartLoading() {
        if (this.cachedResult != null) {
            deliverResult(this.cachedResult);
        }
        if (takeContentChanged() || this.cachedResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        this.cachedResult = null;
    } 			
}

 