package global.store;

import global.GalilelRate;

public interface RateDbDao<T> extends AbstractDbDao<T>{

    GalilelRate getRate(String coin);


    void insertOrUpdateIfExist(GalilelRate galilelRate);

}
