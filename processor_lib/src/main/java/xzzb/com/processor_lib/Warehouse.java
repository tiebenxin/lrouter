package xzzb.com.processor_lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LL130386 on 2019/5/23.
 */

public class Warehouse {
    public static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    public static Map<String, RouteMeta> routes = new HashMap<>();

}

