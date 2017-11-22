package azhar.com.quicksaleclient.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by azharuddin on 24/7/17.
 */

public class BillModel implements Serializable {

    private String key;
    private String name;
    private HashMap<String, Object> billMap;

    public BillModel(String key, String name, HashMap<String, Object> billMap) {
        this.key = key;
        this.name = name;
        this.billMap = billMap;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getBillMap() {
        return billMap;
    }
}
