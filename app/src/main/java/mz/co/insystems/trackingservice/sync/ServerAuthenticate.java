package mz.co.insystems.trackingservice.sync;

/**
 * Created by voloide on 9/19/16.
 */
public interface ServerAuthenticate {
    public boolean userSignIn(final String contact, final String password, boolean isFirstSync);
}
