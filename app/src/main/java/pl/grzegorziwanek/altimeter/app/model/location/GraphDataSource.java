package pl.grzegorziwanek.altimeter.app.model.location;

/**
 * Created by Grzegorz Iwanek on 01.02.2017.
 */

public interface GraphDataSource {

    interface LoadGraphDrawCallback {
        void onGraphLoaded();
    }

    interface GetGraphDrawCallback {

    }
}
