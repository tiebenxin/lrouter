package xzzb.com.processor_lib;

import java.util.Map;

/**
 * Created by LL130386 on 2019/5/24.
 */

public interface IRouteRoot {
    /**
     * Load routes to input
     *
     * @param routes input
     */
    void register(Map<String, Class<? extends IMap>> routes);
}
