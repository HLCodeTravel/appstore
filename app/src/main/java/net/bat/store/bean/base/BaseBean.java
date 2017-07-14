package net.bat.store.bean.base;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class BaseBean<T>{
    private String code;
    private T resultMap;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getResultMap() {
        return resultMap;
    }

    public void setResultMap(T resultMap) {
        this.resultMap = resultMap;
    }
}
