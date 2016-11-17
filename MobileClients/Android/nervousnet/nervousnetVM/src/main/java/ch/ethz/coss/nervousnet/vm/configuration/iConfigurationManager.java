package ch.ethz.coss.nervousnet.vm.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import ch.ethz.coss.nervousnet.vm.nervousnet.database.NoSuchElementInDBException;

/**
 * Created by ales on 17/11/16.
 */
public interface iConfigurationManager {

    public Collection<GeneralSensorConfiguration> getAllConfigurations();
    public GeneralSensorConfiguration getConfiguration(long sensorID) throws NoSuchElementException;
    public int getSensorState(long sensorID) throws NoSuchElementException;
    public void setSensorState(long sensorID, int state) throws NoSuchElementException;
    public int getNervousnetState();
    public void setNervousnetState(int state);
}
