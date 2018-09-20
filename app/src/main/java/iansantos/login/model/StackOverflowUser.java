package iansantos.login.model;

import com.google.gson.annotations.SerializedName;

public class StackOverflowUser {
    @SerializedName("display_name")
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
