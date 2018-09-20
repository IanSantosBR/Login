package iansantos.login.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StackOverflowQuestion {
    @SerializedName("title")
    private String title;
    @SerializedName("link")
    private String link;
    @SerializedName("owner")
    private StackOverflowUser owner;
    @SerializedName("is_answered")
    private boolean isAnswered;
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("creation_date")
    private long creationDate;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public StackOverflowUser getOwner() {
        return owner;
    }
    public void setOwner(StackOverflowUser owner) {
        this.owner = owner;
    }
    public boolean isAnswered() {
        return isAnswered;
    }
    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public long getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
