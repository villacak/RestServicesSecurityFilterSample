package au.com.rest.test.pojos;

import com.google.gson.Gson;

import java.io.Serializable;

public class UserLogin implements Serializable {

    private int id;
    private String login;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toJson() {
        final Gson json = new Gson();
        final String jsonStr = json.toJson(this);
        return jsonStr;
    }
}
