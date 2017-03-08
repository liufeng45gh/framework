package net.csdn;

import com.google.inject.Injector;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import net.csdn.common.scan.DefaultScanService;
import net.csdn.common.scan.ScanService;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-7-2
 * Time: 上午11:35
 */
public class ServiceFramework {
    public static Injector injector;
    public final static ScanService scanService = new DefaultScanService();
    public final static ClassPool classPool;
    public static Mode mode = null;

    public static enum Mode {
        development, production, test
    }

    static {
        classPool = new ClassPool();
        classPool.appendSystemPath();
        classPool.appendClassPath(new LoaderClassPath(ServiceFramework.class.getClassLoader()));
    }

    public static <T> T findService(Class<T> clz) {
        return injector.getInstance(clz);
    }

}
