package virtual;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ales on 30/08/16.
 */
public class VirtualPoint {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int ID;
    private OriginalVirtualSensorPoint original;
    private ClusterVirtualSensorPoint cluster;

    public VirtualPoint( ClusterVirtualSensorPoint cluster, OriginalVirtualSensorPoint original ){
        this.original = original;
        this.cluster = cluster;
        ID = count.incrementAndGet();
    }

    public OriginalVirtualSensorPoint getOriginal(){
        return original;
    }

    public ClusterVirtualSensorPoint getCluster(){
        return cluster;
    }

    public void setCluster( ClusterVirtualSensorPoint cluster ){
        this.cluster = cluster;
    }
}
