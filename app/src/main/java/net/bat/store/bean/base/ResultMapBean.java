package net.bat.store.bean.base;

import java.util.List;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class ResultMapBean<T,E,F,G>{
    private List<T> pageGroupMapList;
    private List<E> webstoreFloorMapList;
    private List<F> categoryMap;
    private List<G> appMap;

    public List<T> getPageGroupMapList() {
        return pageGroupMapList;
    }

    public void setPageGroupMapList(List<T> pageGroupMapList) {
        this.pageGroupMapList = pageGroupMapList;
    }

    public List<E> getWebstoreFloorMapList() {
        return webstoreFloorMapList;
    }

    public void setWebstoreFloorMapList(List<E> webstoreFloorMapList) {
        this.webstoreFloorMapList = webstoreFloorMapList;
    }

    public List<F> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(List<F> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<G> getAppMap() {
        return appMap;
    }

    public void setAppMap(List<G> appMap) {
        this.appMap = appMap;
    }
}
