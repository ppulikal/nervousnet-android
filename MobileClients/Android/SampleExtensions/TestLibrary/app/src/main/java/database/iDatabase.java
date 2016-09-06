package database;

import virtual.VirtualPoint;

/**
 * Created by ales on 01/09/16.
 */
public interface iDatabase {

    public void add(double timestampd,
                    double vnoised,
                    double vlightd,
                    double vbatteryd,
                    double vaccxd,
                    double vaccyd,
                    double vacczd,
                    double vgyroxd,
                    double vgyroyd,
                    double vgyrozd,
                    double vproxd,
                    double onoised,
                    double olightd,
                    double obatteryd,
                    double oaccxd,
                    double oaccyd,
                    double oacczd,
                    double ogyroxd,
                    double ogyroyd,
                    double ogyrozd,
                    double oproxd);

    public void update(double timestampd,
                    double vnoised,
                    double vlightd,
                    double vbatteryd,
                    double vaccxd,
                    double vaccyd,
                    double vacczd,
                    double vgyroxd,
                    double vgyroyd,
                    double vgyrozd,
                    double vproxd,
                    double onoised,
                    double olightd,
                    double obatteryd,
                    double oaccxd,
                    double oaccyd,
                    double oacczd,
                    double ogyroxd,
                    double ogyroyd,
                    double ogyrozd,
                    double oproxd);

    public VirtualPoint get(double timestamp);

    public void delete(double timestamp);

    public void deleteTable();

    public void initTable();
}
