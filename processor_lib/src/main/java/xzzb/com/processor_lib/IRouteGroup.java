package xzzb.com.processor_lib;

import java.util.Map;

/**
 * Created by LL130386 on 2019/5/22.
 */

public interface IRouteGroup {
    void register(Map<String, RouteMeta> atlas);
}
