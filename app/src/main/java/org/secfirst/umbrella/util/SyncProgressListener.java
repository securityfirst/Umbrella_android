package org.secfirst.umbrella.util;

public interface SyncProgressListener {
    void onProgressChange(int progress);
    void onStatusChange(String status);
    void onDone();
}
