package SAT;

import Aloha.Tag;

import java.util.List;

public class PreAndTag {
    public String prefix;
    public List<Tag> tags;

    public PreAndTag() {
    }

    public PreAndTag(String prefix, List<Tag> tags) {
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
