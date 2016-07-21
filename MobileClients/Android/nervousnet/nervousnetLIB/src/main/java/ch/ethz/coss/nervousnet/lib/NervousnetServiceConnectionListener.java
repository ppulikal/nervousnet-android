package ch.ethz.coss.nervousnet.lib;

/**
 * Created by prasad on 21/07/16.
 */
public interface NervousnetServiceConnectionListener {

    public void onServiceConnected(NervousnetRemote mService);

    public void onServiceDisconnected();
}
