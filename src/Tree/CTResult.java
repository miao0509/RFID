package Tree;

import Aloha.Tag;

import java.util.List;

public class CTResult {
    public int result; //  0 为空闲 1为成功 2为失败
    public List<Tag> prefix0; // 公共前缀+0
    public List<Tag> prefix1; // 公共前缀+1

    public CTResult() {
    }

    public CTResult(int result) {
        this.result = result;
    }

    public CTResult(int result, List<Tag> prefix0, List<Tag> prefix1) {
        this.result = result;
        this.prefix0 = prefix0;
        this.prefix1 = prefix1;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Tag> getPrefix0() {
        return prefix0;
    }

    public void setPrefix0(List<Tag> prefix0) {
        this.prefix0 = prefix0;
    }

    public List<Tag> getPrefix1() {
        return prefix1;
    }

    public void setPrefix1(List<Tag> prefix1) {
        this.prefix1 = prefix1;
    }
}
