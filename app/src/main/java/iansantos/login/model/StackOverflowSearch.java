package iansantos.login.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StackOverflowSearch {
    @SerializedName("items")
    public List<StackOverflowQuestion> items = new ArrayList<>();

    public List<StackOverflowQuestion> getItems() {
        return items;
    }
    public void setItems(List<StackOverflowQuestion> items) {
        this.items = items;
    }
}
