package xzzb.com.processor_lib;

/**
 * Created by LL130386 on 2019/5/22.
 */

public class Constants {
    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "LRouter";


    private static final String FACADE_PACKAGE = "xzzb.com.processor_lib";
    private static final String TEMPLATE_PACKAGE = ".template";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root";


    public static final String IROUTE_GROUP = FACADE_PACKAGE + ".IMap";//IMap full url
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;

    public static final String PACKAGE_OF_GENERATE_FILE = "com.processor_lib.lrouter.routes";
    public static final String PACKAGE_OF_GENERATE_DOCS = "com.processor_lib.lrouter.docs";


    public static final String VALUE_ENABLE = "enable";
    public static final String METHOD_REGISTER = "register";


    public static final String KEY_MODULE_NAME = "LROUTER_MODULE_NAME";
    public static final String KEY_GENERATE_DOC_NAME = "LROUTER_GENERATE_DOC";

    public static final String ITROUTE_ROOT = FACADE_PACKAGE + ".IRouteRoot";//IRouteRoot full url
    public static final String ANNOTATION_TYPE_ROUTE = FACADE_PACKAGE + ".LRoute";


}
