package au.com.rest.test.pojos;

import com.google.gson.Gson;

import java.io.Serializable;

public class Question  implements Serializable {
    private int id;
    private String question;
    private boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String toJson() {
        final Gson json = new Gson();
        final String jsonStr = json.toJson(this);
        return jsonStr;
    }
}
