package Tree;

import java.util.Objects;

public class GNAQTSign {
    String prefix ;
    int group;

    public GNAQTSign() {
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public GNAQTSign(String prefix, int group) {
        this.prefix = prefix;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GNAQTSign gnaqtSign = (GNAQTSign) o;
        return  Objects.equals(prefix, gnaqtSign.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, group);
    }
}
