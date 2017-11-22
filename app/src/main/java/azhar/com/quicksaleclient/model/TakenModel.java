package azhar.com.quicksaleclient.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by azharuddin on 24/7/17.
 */

public class TakenModel implements Serializable {
    private String key;
    private HashMap<String, Object> takenMap;

    public TakenModel(String key, HashMap<String, Object> takenMap) {
        this.key = key;
        this.takenMap = takenMap;
    }

    public String getKey() {
        return key;
    }

    public HashMap<String, Object> getTakenMap() {
        return takenMap;
    }
}
