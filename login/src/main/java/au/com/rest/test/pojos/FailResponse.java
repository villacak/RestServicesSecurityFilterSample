package au.com.rest.test.pojos;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;

public class FailResponse {

    private Response.Status status;
    private String message;

    public FailResponse(Response.Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status.getStatusCode();
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("'status':").append(status.getStatusCode());
        sb.append(", 'message':'").append(message).append("'");
        sb.append('}');
        return sb.toString();
    }

    public String toJson() {
        final Gson gson = new Gson();
        final String jsonStr = gson.toJson(this);
        return jsonStr;
    }
}
