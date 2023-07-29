package com.csl.cs108ademoapp.Web;

/**
 * Created by rommel.torres on 22/12/2017.
 */

public interface JsonWebI<T> {
    void onComplete(final T result);
    void onError(final String msg);
    void onAcepted(String msg);
}
