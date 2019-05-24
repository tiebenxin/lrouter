package xzzb.com.processor_lib;

/**
 * Created by LL130386 on 2019/5/23.
 */

public class RouteMeta {
    String path;
    String group;

    public RouteMeta() {

    }

    public RouteMeta(String p, String g) {
        this.path = p;
        this.group = g;
    }

    public static RouteMeta build(String path, String group) {
        return new RouteMeta(path, group);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
