package xzzb.com.runningannotation;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.util.Set;

import xzzb.com.processor_lib.IRouteGroup;
import xzzb.com.processor_lib.IRouteRoot;
import xzzb.com.processor_lib.Warehouse;

import static xzzb.com.processor_lib.Constants.NAME_OF_GROUP;
import static xzzb.com.processor_lib.Constants.NAME_OF_ROOT;
import static xzzb.com.processor_lib.Constants.PACKAGE_OF_GENERATE_FILE;
import static xzzb.com.processor_lib.Constants.PROJECT;
import static xzzb.com.processor_lib.Constants.SEPARATOR;

public class LRouter {

//    public static final String ROUTE_ROOT_PAKCAGE = "com.processor_lib.lrouter.routes";
//    private static final String SUFFIX_ROOT = "Root";
//    private static final String SEPARATOR = "$$";


    private static LRouter lRouter;

    /**
     * @author Administrator
     * @time 2018/10/18  10:56
     * @describe 获取传入的Context
     */
    public static Context getContext() {
        return context;
    }


    private static Context context;

    /**
     * @author Administrator
     * @time 2018/10/18  10:56
     * @describe 私有化构造函数
     */
    private LRouter() {

    }

    /**
     * @author Administrator
     * @time 2018/10/18  10:57
     * @describe 初始化
     */
    public static void init(Application application) {
        context = application;
        initMap();
    }


    /**
     * @author Administrator
     * @time 2018/10/18  10:56
     * @describe 单例模式
     */
    public static LRouter getInstance() {
        if (lRouter == null) {
            lRouter = new LRouter();
        }

        return lRouter;
    }

    /**
     * @author Administrator
     * @time 2018/10/18  10:56
     * @describe 拿到生成的路由表 返回一个Postcard对象
     */
    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
//        LRouterMap lRouterMap = new LRouterMap();
//        HashMap<String, String> maps = lRouterMap.getMaps();

        return build(path, extractGroup(path));

    }

    /**
     * Build postcard by path and group
     */
    protected Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
//            throw new HandlerException(Consts.TAG + "Parameter is invalid!");
            return null;
        } else {
            return new Postcard(path, group);
        }
    }

    /**
     * Extract the default group from path.
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
//            throw new HandlerException(Consts.TAG + "Extract the default group failed, the path must be start with '/' and contain more than 2 '/'!");
        }

        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
//                throw new HandlerException(Consts.TAG + "Extract the default group failed! There's nothing between 2 '/'!");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
//            logger.warning(Consts.TAG, "Failed to extract default group! " + e.getMessage());
            return null;
        }
        return null;

    }

    /*
    * 初始化Map
    * 此处初始化一个map也可，一般是初始化groupsIndex, 然后routes通过取值的时候，在赋值
    * */
    private static void initMap() {
        try {
            Set<String> routerMap = ClassUtils.getFileNameByPackageName(context, PACKAGE_OF_GENERATE_FILE);
            for (String className : routerMap) {
                System.out.println("LRouter::" + "initMap--groupsIndex");
                if (className.startsWith(PACKAGE_OF_GENERATE_FILE + "." + NAME_OF_ROOT)) {
                    //root中注册的是分组信息 将分组信息加入仓库中
                    ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).register(Warehouse.groupsIndex);
                }
                if (className.startsWith(PACKAGE_OF_GENERATE_FILE + "." + NAME_OF_GROUP)) {
                    System.out.println("LRouter::" + "initMap--routes");
                    //root中注册的是分组信息 将分组信息加入仓库中
                    ((IRouteGroup) Class.forName(className).getConstructor().newInstance()).register(Warehouse.routes);
                }
//                ((IRouteGroup) (Class.forName(className).getConstructor().newInstance())).register(Warehouse.routes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
