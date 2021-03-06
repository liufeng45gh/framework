package net.csdn.bootstrap.loader.impl;

import com.google.inject.*;
import net.csdn.ServiceFramework;
import net.csdn.bootstrap.loader.Loader;
import net.csdn.common.scan.ScanModule;
import net.csdn.common.settings.Settings;
import net.csdn.jpa.type.DBInfo;
import net.csdn.jpa.type.DBType;
import net.csdn.modules.cache.CacheModule;
import net.csdn.modules.http.HttpModule;
import net.csdn.modules.settings.SettingsModule;
import net.csdn.modules.threadpool.ThreadPoolModule;
import net.csdn.modules.transport.TransportModule;

import java.util.ArrayList;
import java.util.List;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-7-2
 * Time: 上午11:32
 */
public class ModuelLoader implements Loader {
    @Override
    public void load(final Settings settings) {
        final List<Module> moduleList = new ArrayList<Module>();
        moduleList.add(new SettingsModule(settings));
        moduleList.add(new ThreadPoolModule());
        moduleList.add(new TransportModule());
        moduleList.add(new HttpModule());
        moduleList.add(new ScanModule());
        boolean disableRedis = settings.getAsBoolean(ServiceFramework.mode + ".datasources.redis.disable", false);
        if (!disableRedis) {
            moduleList.add(new CacheModule());
        }
        boolean disableMysql = settings.getAsBoolean(ServiceFramework.mode + ".datasources.mysql.disable", false);

        if(!disableMysql){
            moduleList.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DBInfo.class).in(Singleton.class);
                }
            });
            moduleList.add(new AbstractModule() {
                @Override
                protected void configure() {
                    String clzzName = settings.get("type_mapping", "net.csdn.jpa.type.impl.MysqlType");
                    final Class czz;
                    try {
                        czz = Class.forName(clzzName);
                        bind(DBType.class).to(czz).in(Singleton.class);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        ServiceFramework.injector = Guice.createInjector(Stage.PRODUCTION, moduleList);
    }
}
