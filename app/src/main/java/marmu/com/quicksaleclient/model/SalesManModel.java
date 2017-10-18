package marmu.com.quicksaleclient.model;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManModel {
    private String name, phone;

    public SalesManModel(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
