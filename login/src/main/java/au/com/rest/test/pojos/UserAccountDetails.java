package au.com.rest.test.pojos;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class UserAccountDetails implements Serializable {

    private int id;
    private UserLogin userLogin;
    private String fullName;
    private String email;
    private String phone;
    private List<Answer> answerList;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public String toJson() {
        final Gson json = new Gson();
        final String jsonStr = json.toJson(this);
        return jsonStr;
    }
}
