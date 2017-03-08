package net.csdn.bootstrap.loader.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import javassist.CtClass;
import net.csdn.ServiceFramework;
import net.csdn.annotation.AnnotationException;
import net.csdn.annotation.Util;
import net.csdn.bootstrap.loader.Loader;
import net.csdn.common.scan.ScanService;
import net.csdn.common.settings.Settings;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import static net.csdn.common.logging.support.MessageFormat.format;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-7-2
 * Time: 上午11:32
 */
public class UtilLoader implements Loader {
    @Override
    public void load(Settings settings) throws Exception {
        final List<Module> moduleList = new ArrayList<Module>();

        ServiceFramework.scanService.scanArchives(settings.get("application.util"), new ScanService.LoadClassEnhanceCallBack() {
            @Override
            public Class loaded(DataInputStream classFile) {
                try {
                    CtClass ctClass = ServiceFramework.classPool.makeClass(classFile);
                    if (!ctClass.hasAnnotation(Util.class)) {
                        return null;
                    }
                    final Class clzz = ctClass.toClass();
                    final Util util = (Util) clzz.getAnnotation(Util.class);
                    if (clzz.isInterface())
                        throw new AnnotationException(format("{} util should not be interface", clzz.getName()));
                    moduleList.add(new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(clzz).in(util.value());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        });

        ServiceFramework.injector = ServiceFramework.injector.createChildInjector(moduleList);
    }
}
