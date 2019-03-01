package global.store;

import java.util.ArrayList;

/**
 *
 * @param <T> --> Object
 */
public interface AbstractDbDao<T> {

    long insert(T obj);

    ArrayList<T> list();

    T get(String whereColumn,Object whereObjValue);

    void updateFieldByKey(String whereColumn,String whereValue, String updateColumn, boolean updateValue);

    void updateByKey(String whereColumn,String whereValue, T t);

    int updateFieldByKey(String whereColumn,String whereValue, String updateColumn, String updateValue);

    int numberOfRows();

    Integer delete(String keyColumn,String columnValue);


}
