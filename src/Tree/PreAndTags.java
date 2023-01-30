package Tree;

import Aloha.Tag;

import java.util.List;

public class PreAndTags {
    public String prefix;
    public List<Tag> tags;

    public PreAndTags() {
    }

    public PreAndTags(String prefix, List<Tag> tags) {
        this.prefix = prefix;
        this.tags = tags;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
