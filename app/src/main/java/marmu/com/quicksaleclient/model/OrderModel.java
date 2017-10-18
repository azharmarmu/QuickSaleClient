package marmu.com.quicksaleclient.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by azharuddin on 24/7/17.
 */

public class OrderModel implements Serializable {
    private String key;
    private HashMap<String, Object> orderMap;

    public OrderModel(String key, HashMap<String, Object> orderMap) {
        this.key = key;
        this.orderMap = orderMap;
    }

    public String getKey() {
        return key;
    }

    public HashMap<String, Object> getOrderMap() {
        return orderMap;
    }
}
