package au.com.rest.test.pojos;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class Answer implements Serializable {

    private int id;
    private Question question;
    private String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String toJson() {
        final Gson json = new Gson();
        final String jsonStr = json.toJson(this);
        return jsonStr;
    }
}
