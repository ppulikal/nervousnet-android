package ch.ethz.coss.nervousnet.vm.nervousnet.database;

/**
 * Created by ales on 06/11/16.
 */
public class NoSuchElementInDBException extends Exception {
    public NoSuchElementInDBException(String msg){
        super(msg);
    }
}
