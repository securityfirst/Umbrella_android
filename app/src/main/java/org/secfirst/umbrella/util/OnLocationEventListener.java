package org.secfirst.umbrella.util;

/**
 * Created by HAL-9000 on 12/12/2017.
 */

public interface OnLocationEventListener {
    void locationEvent(String currentLocation, boolean sourceFeedEnable);

    void locationStartFetchData();

    void locationEndFetchData();
}
