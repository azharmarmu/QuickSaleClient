package azhar.com.quicksaleclient.model;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManModel {
    private String key, name, phone;

    public SalesManModel(String key, String phone, String name) {
        this.key = key;
        this.phone = phone;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
