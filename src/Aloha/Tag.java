package Aloha;

import java.util.Objects;

public class Tag {
    private String tag;
    private int num;

    public Tag() {
    }

    public Tag(String tag) {
        this.tag = tag;
    }

    public Tag(String tag, int num) {
        this.tag = tag;
        this.num = num;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tag='" + tag + '\'' +
                ", num=" + num +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(null == obj) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }
        Tag tag = (Tag) obj;
        return Objects.equals(this.getTag(),tag.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag,num);
    }
}
