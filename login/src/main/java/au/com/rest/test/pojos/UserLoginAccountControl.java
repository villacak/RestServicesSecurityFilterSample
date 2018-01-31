package au.com.rest.test.pojos;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;

public class UserLoginAccountControl implements Serializable {

    private int id;
    private UserLogin userLogin;
    private String accountState;
    private Integer reason;
    private Date lastAccess;
    private Integer successTrys;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Integer getSuccessTrys() {
        return successTrys;
    }

    public void setSuccessTrys(Integer successTrys) {
        this.successTrys = successTrys;
    }

    public String toJson() {
        final Gson json = new Gson();
        final String jsonStr = json.toJson(this);
        return jsonStr;
    }
}
