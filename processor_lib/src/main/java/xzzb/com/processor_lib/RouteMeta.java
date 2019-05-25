package xzzb.com.processor_lib;

import javax.lang.model.element.Element;

/**
 * Created by LL130386 on 2019/5/23.
 */

public class RouteMeta {
    String path;
    String group;
    private Class<?> destination;   // Destination
    private Element rawType;        // Raw type of route


    public RouteMeta() {

    }

    public RouteMeta(String p, String g, Element e) {
        this.path = p;
        this.group = g;
        this.rawType = e;
    }

    public RouteMeta(String p, String g, Element e, Class<?> clazz) {
        this.path = p;
        this.group = g;
        this.rawType = e;
        this.destination = clazz;
    }

    public static RouteMeta build(String path, String group, Class<?> destination) {
        return new RouteMeta(path, group, null, destination);
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

    public Class<?> getDestination() {
        return destination;
    }

    public RouteMeta setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }
}
