package xzzb.com.runningannotation;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import xzzb.com.processor_lib.IRouteGroup;
import xzzb.com.processor_lib.RouteMeta;
import xzzb.com.processor_lib.Warehouse;

public class Postcard extends RouteMeta {
    //path
    private String path;
    private String group;
    //要跳转的包名
//    private String packageName;

    public Postcard(String path, String group) {
        this.path = path;
        this.group = group;
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

    /**
     * @author Administrator
     * @time 2018/10/18  10:58
     * @describe 跳转
     */
    public void navigation() {
//判断路径是不是null
        if (!TextUtils.isEmpty(path)) {
            RouteMeta routeMeta = Warehouse.routes.get(path);
            if (routeMeta == null) {
                //TODO:如果本地无，重新初始化
                Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(subGroup(path));
                IRouteGroup iGroupInstance;
                try {
                    iGroupInstance = groupMeta.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("路由分组映射表记录失败.", e);
                }
                iGroupInstance.register(Warehouse.routes);
                //已经准备过了就可以移除了 (不会一直存在内存中)
                Warehouse.groupsIndex.remove(subGroup(path));
                //再次进入 else
                navigation(path);

                System.out.println("集合中无此activity");
            } else {
                //路径为null直接返回
                Intent intent = new Intent(LRouter.getContext(), routeMeta.getDestination());
                ActivityCompat.startActivity(LRouter.getContext(), intent, null);
                return;
            }
        }

    }

    private String subGroup(String path) {
        String defaultGroup = "";
        if (!TextUtils.isEmpty(path)) {
            defaultGroup = path.substring(1, path.indexOf("/", 1));
        }
        return defaultGroup;
    }
}
