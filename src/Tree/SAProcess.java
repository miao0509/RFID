package Tree;

import Aloha.Tag;

import java.util.List;
import java.util.Map;

public class SAProcess {
    public Map<Integer, List<Tag>> map;
    public int success;

    public SAProcess() {
    }

    public SAProcess(Map<Integer, List<Tag>> map, int success) {
        this.map = map;
        this.success = success;
    }

    public Map<Integer, List<Tag>> getMap() {
        return map;
    }

    public void setMap(Map<Integer, List<Tag>> map) {
        this.map = map;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
