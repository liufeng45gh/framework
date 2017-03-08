package net.csdn.bootstrap.loader.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import javassist.CtClass;
import net.csdn.ServiceFramework;
import net.csdn.annotation.rest.At;
import net.csdn.bootstrap.loader.Loader;
import net.csdn.common.collect.Tuple;
import net.csdn.common.scan.ScanService;
import net.csdn.common.settings.Settings;
import net.csdn.modules.http.ApplicationController;
import net.csdn.modules.http.RestController;
import net.csdn.modules.http.RestRequest;

import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-7-2
 * Time: 上午11:31
 */
public class ControllerLoader implements Loader {

    @Override
    public void load(Settings settings) throws Exception {
        final List<Module> moduleList = new ArrayList<Module>();
        //自动加载所有Action类
        ServiceFramework.scanService.scanArchives(settings.get("application.controller"), new ScanService.LoadClassEnhanceCallBack() {
            @Override
            public Class loaded(DataInputStream classFile) {
                try {
                    CtClass ctClass = ServiceFramework.classPool.makeClass(classFile);
                    if (Modifier.isAbstract(ctClass.getModifiers())) return null;
                    moduleList.add(bindAction(ctClass.toClass()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
        ServiceFramework.injector = ServiceFramework.injector.createChildInjector(moduleList);
    }

    private static Module bindAction(final Class clzz) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                if (clzz == null) return;
                try {
                    boolean isController = false;
                    Class wow = clzz;
                    while (wow.getSuperclass() != null) {
                        if (wow.getSuperclass() == ApplicationController.class) {
                            isController = true;
                            break;
                        }
                        wow = wow.getSuperclass();
                    }
                    if (!isController) return;
                    Method[] methods = clzz.getDeclaredMethods();

                    for (Method method : methods) {
                        if (method.getModifiers() == Modifier.PRIVATE) continue;
                        At at = method.getAnnotation(At.class);
                        if (at == null) continue;
                        String url = at.path()[0];
                        RestRequest.Method[] httpMethods = at.types();
                        RestController restController = ServiceFramework.injector.getInstance(RestController.class);
                        for (RestRequest.Method httpMethod : httpMethods) {
                            Tuple<Class<ApplicationController>, Method> tuple = new Tuple<Class<ApplicationController>, Method>(clzz, method);
                            restController.registerHandler(httpMethod, url, tuple);
                        }
                        bind(clzz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
