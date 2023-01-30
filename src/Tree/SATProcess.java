package Tree;

import java.util.Map;

public class SATProcess {
    public Map<Integer,PreAndTags> map;
    public int success;

    public SATProcess() {
    }

    public SATProcess(Map<Integer, PreAndTags> map, int success) {
        this.map = map;
        this.success = success;
    }

    public Map<Integer, PreAndTags> getMap() {
        return map;
    }

    public void setMap(Map<Integer, PreAndTags> map) {
        this.map = map;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
