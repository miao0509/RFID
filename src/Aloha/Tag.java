package Aloha;

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
}
