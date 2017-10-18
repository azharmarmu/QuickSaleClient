package marmu.com.quicksaleclient.model;

/**
 * Created by azharuddin on 24/7/17.
 */

public class CustomerModel {
    private String key, name, phone, gst;

    public CustomerModel(String key, String name, String phone, String gst) {
        this.key = key;
        this.name = name;
        this.phone = phone;
        this.gst = gst;
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

    public String getGst() {
        return gst;
    }
}
