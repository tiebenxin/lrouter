package xzzb.com.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;

import xzzb.com.processor.entity.RouteDoc;
import xzzb.com.processor_lib.LRoute;
import xzzb.com.processor_lib.RouteMeta;

import static javax.lang.model.element.Modifier.PUBLIC;
import static xzzb.com.processor_lib.Constants.ANNOTATION_TYPE_ROUTE;
import static xzzb.com.processor_lib.Constants.IROUTE_GROUP;
import static xzzb.com.processor_lib.Constants.ITROUTE_ROOT;
import static xzzb.com.processor_lib.Constants.METHOD_REGISTER;
import static xzzb.com.processor_lib.Constants.NAME_OF_GROUP;
import static xzzb.com.processor_lib.Constants.NAME_OF_ROOT;
import static xzzb.com.processor_lib.Constants.PACKAGE_OF_GENERATE_DOCS;
import static xzzb.com.processor_lib.Constants.PACKAGE_OF_GENERATE_FILE;
import static xzzb.com.processor_lib.Constants.SEPARATOR;

@AutoService(Processor.class)
public class RouterPorcessor extends BaseProcessor {
    private final String TAG = RouterPorcessor.class.getSimpleName();
    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    private Map<String, String> rootMap = new TreeMap<>();  // Map of root metas, used for generate class file in order.


    private Writer docWriter;       // Writer used for write doc

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println(TAG + "--" + "process ");
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(LRoute.class);
            try {
                System.out.println(TAG + "--" + "Found routes, start...");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                System.out.println("come out fuck" + e);
                e.printStackTrace();
            }
            return true;
        }

        return false;


    }

    private void parseRoutes(Set<? extends Element> elements) throws IOException {
        if (!CollectionUtils.isNotEmpty(elements)) {
            System.out.println(TAG + "--" + " Found routes, size is " + elements.size());
            return;
        }
        System.out.println(TAG + "--" + " Found routes, size is " + elements.size());
        rootMap.clear();

        TypeElement type_IMap = elementUtils.getTypeElement(IROUTE_GROUP);
        ClassName routeMetaCn = ClassName.get(RouteMeta.class);
        /*
               Build input type, format as :

               ```Map<String, Class<? extends IMap>>```
             */
        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(type_IMap))
                )
        );

           /*

              ```Map<String, RouteMeta>```
             */
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );

                /*
              Build input param name.
             */
        ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();
        ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();

        /*
              Build method : 'register'
              register
             */
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_REGISTER)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);


        Map<String, List<RouteDoc>> docSource = new HashMap<>();

        for (Element element : elements) {
            LRoute route = element.getAnnotation(LRoute.class);
            RouteMeta routeMeta = new RouteMeta(route.path(), route.name());
            categories(routeMeta);
        }

        for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_REGISTER)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(groupParamSpec);

            List<RouteDoc> routeDocList = new ArrayList<>();

            // Build group method body
            Set<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                RouteDoc routeDoc = extractDocInfo(routeMeta);
                loadIntoMethodOfGroupBuilder.addStatement(
                        "atlas.put($S, $T.build(" + "$S, $S" + "))",
                        routeMeta.getPath(),
                        routeMetaCn,
                        routeMeta.getPath().toLowerCase(),
                        routeMeta.getGroup().toLowerCase());

                routeDoc.setClassName("");
                routeDocList.add(routeDoc);
            }

            // Generate groups
            String groupFileName = NAME_OF_GROUP + groupName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupFileName)
//                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IMap))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfGroupBuilder.build())
                            .build()
            ).build().writeTo(filer);

            System.out.println(">>> Generated group: " + groupName + "<<<");
            rootMap.put(groupName, groupFileName);
            docSource.put(groupName, routeDocList);
        }

        if (MapUtils.isNotEmpty(rootMap)) {
            // Generate root meta by group name, it must be generated before root, then I can find out the class of group.
            for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
                System.out.println(TAG + "--loadInto--" + ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
            }
        }

        // Output route doc
        if (generateDoc) {
            docWriter.append(JSON.toJSONString(docSource, SerializerFeature.PrettyFormat));
            docWriter.flush();
            docWriter.close();
        }

        // Write root meta into disk.
        String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
        JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootFileName)
//                        .addJavadoc(WARNING_TIPS)
                        .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filer);


    }

    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            System.out.println(TAG + ">>> Start categories, group = " + routeMeta.getGroup() + ", path = " + routeMeta.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta r1, RouteMeta r2) {
                        try {
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
//                            logger.error(npe.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
//            logger.warning(">>> Route meta verify error, group is " + routeMete.getGroup() + " <<<");
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        if (generateDoc) {
            try {
                docWriter = filer.createResource(StandardLocation.SOURCE_OUTPUT, PACKAGE_OF_GENERATE_DOCS, "arouter-map-of-" + moduleName + ".json").openWriter();
            } catch (IOException e) {
                System.out.println(TAG + "--" + "Create doc writer failed, because " + e.getMessage());
            }
        }
        System.out.println(TAG + "--" + "init ");

    }


    /**
     * Verify the route meta
     *
     * @param meta raw meta
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();

        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup())) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                System.out.println(TAG + "--" + "Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Extra doc info from route meta
     *
     * @param routeMeta meta
     * @return doc
     */
    private RouteDoc extractDocInfo(RouteMeta routeMeta) {
        RouteDoc routeDoc = new RouteDoc();
        routeDoc.setGroup(routeMeta.getGroup());
        routeDoc.setPath(routeMeta.getPath());

        return routeDoc;
    }
}
