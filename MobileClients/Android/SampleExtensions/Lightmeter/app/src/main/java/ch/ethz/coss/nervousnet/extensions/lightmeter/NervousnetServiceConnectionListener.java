package ch.ethz.coss.nervousnet.extensions.lightmeter;

import ch.ethz.coss.nervousnet.lib.NervousnetRemote;

/**
 * Created by prasad on 20/07/16.
 */
public interface NervousnetServiceConnectionListener {

    public void ServiceConnected(NervousnetRemote mService);

    public void ServiceDisconnected();
}
