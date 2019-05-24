package xzzb.com.processor;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import xzzb.com.processor_lib.LRoute;

import static xzzb.com.processor_lib.Constants.ANNOTATION_TYPE_ROUTE;
import static xzzb.com.processor_lib.Constants.KEY_GENERATE_DOC_NAME;
import static xzzb.com.processor_lib.Constants.KEY_MODULE_NAME;
import static xzzb.com.processor_lib.Constants.VALUE_ENABLE;

/**
 * Created by LL130386 on 2019/5/23.
 */

public abstract class BaseProcessor extends AbstractProcessor {
    private final String TAG = BaseProcessor.class.getSimpleName();

    public Filer filer;
    public Messager messager;
    public Elements elementUtils;
    String moduleName;
    public boolean generateDoc;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        Map<String, String> options = processingEnv.getOptions();
        System.out.println();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
            System.out.println(TAG + "--moduleName=" + moduleName);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> set = new HashSet<>();
        set.add(KEY_MODULE_NAME);
        set.add(KEY_GENERATE_DOC_NAME);
        System.out.println(TAG + "--" + "getSupportedOptions");
        return set;
    }

    @Override

    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
//        set.add(ANNOTATION_TYPE_ROUTE);
        set.add(LRoute.class.getCanonicalName());
        return set;
    }
}
