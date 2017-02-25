package me.yokeyword.sample.city;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangfh on 2017/2/24 0024.
 */

public class HotCityC implements Serializable{
    private long id;
    private List<String> city;

    public HotCityC(long id, List<String> city) {
        this.id = id;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public HotCityC setId(long id) {
        this.id = id;
        return this;
    }

    public List<String> getCity() {
        return city;
    }

    public HotCityC setCity(List<String> city) {
        this.city = city;
        return this;
    }
}
