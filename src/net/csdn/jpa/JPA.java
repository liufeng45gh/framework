package net.csdn.jpa;


import com.google.inject.Injector;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import net.csdn.common.collect.Tuple;
import net.csdn.common.env.Environment;
import net.csdn.common.io.Streams;
import net.csdn.common.logging.CSLogger;
import net.csdn.common.logging.Loggers;
import net.csdn.common.scan.DefaultScanService;
import net.csdn.common.scan.ScanService;
import net.csdn.common.settings.Settings;
import net.csdn.enhancer.Enhancer;
import net.csdn.jpa.context.JPAConfig;
import net.csdn.jpa.enhancer.JPAEnhancer;
import net.csdn.jpa.model.Model;
import net.csdn.jpa.type.DBInfo;
import net.csdn.jpa.type.DBType;
import net.csdn.jpa.type.impl.MysqlType;
import net.csdn.validate.ValidatorLoader;

import javax.persistence.DiscriminatorColumn;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.common.logging.support.MessageFormat.format;

/**
 * User: WilliamZhu
 * Date: 12-6-26
 * Time: 下午9:46
 * Since it is difficult to manager POJOS generated by JPA(Actually you can use AOP).Here we use this class
 * to create +EntityManagerFactory+,to get +EntityManager+ which do persistence and query staff.
 * It is also a holder of some Persistence Infos.
 * <p/>
 * <p/>
 * <code>
 * //find the config file
 * InputStream inputStream = Main.class.getResourceAsStream("application_for_test.yml");
 * Settings settings = InternalSettingsPreparer.simplePrepareSettings(ImmutableSettings.Builder.EMPTY_SETTINGS,
 * inputStream);
 * <p/>
 * //configure ORM
 * JPA.CSDNORMConfiguration csdnormConfiguration = new JPA.CSDNORMConfiguration("development", settings, Main.class.getClassLoader());
 * JPA.configure(csdnormConfiguration);
 * <p/>
 * //finally load all you model
 * JPA.loadModels();
 * <p/>
 * //then you can use you pojo now
 * Tag.findAll();
 * </code>
 */
public class JPA {

    private static CSLogger logger = Loggers.getLogger(JPA.class);


    private static JPAConfig jpaConfig;
    public final static Map<String, Class<Model>> models = new HashMap<String, Class<Model>>();

    private static CSDNORMConfiguration ormConfiguration;

    public static void configure(CSDNORMConfiguration csdnormConfiguration) {
        ormConfiguration = csdnormConfiguration;
        ormConfiguration.buildDefaultDBInfo();
        loadModels();
        try {
            new ValidatorLoader().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized JPAConfig getJPAConfig() {
        if (jpaConfig == null) {
            try {
                modifyPersistenceXml(new Tuple<Settings, Environment>(settings(), environment()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            jpaConfig = new JPAConfig(properties(), settings().get(JPA.mode() + ".datasources.mysql.database"));
        }
        return jpaConfig;
    }

    //自动同步application.xml文件的配置到persistence.xml
    private static void modifyPersistenceXml(Tuple<Settings, Environment> tuple) throws Exception {

        String fileContent = persistenceContent();
        Map<String, Settings> groups = tuple.v1().getGroups(mode() + ".datasources");
        Settings mysqlSetting = groups.get("mysql");
        //
        StringBuffer stringBuffer = new StringBuffer();
        for (Class clzz : models.values()) {
            stringBuffer.append(format("<class>{}</class>", clzz.getName()));
        }
        String path = classLoader().getResource(".").getPath();
        File persistDir = new File(path + "");
        if (!persistDir.exists()) {
            persistDir.mkdirs();
        }
        File persistFile = new File(path + "META-INF/persistence.xml");
        if (persistFile.exists()) {
            persistFile.delete();
        }
        Streams.copy(format(fileContent, mysqlSetting.get("database"), stringBuffer.toString()), new FileWriter(persistFile));
    }

    private static String persistenceContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\"\n" +
                "             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "             xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd\"\n" +
                "             version=\"2.0\">\n" +
                "    <persistence-unit name=\"{}\">\n" +
                "        {}\n" +
                "        <properties>\n" +
                "        </properties>\n" +
                "    </persistence-unit>\n" +
                "</persistence>";

    }

    public static void setJPAConfig(JPAConfig _jpaConfig) {
        jpaConfig = _jpaConfig;
    }

    public static ClassLoader classLoader() {
        return ormConfiguration.classLoader.getClassLoader();
    }

    public static String mode() {
        return ormConfiguration.mode;
    }

    public static ClassPool classPool() {
        return ormConfiguration.classPool;
    }

    public static Injector injector() {
        return ormConfiguration.injector;

    }

    public static Settings settings() {
        return ormConfiguration.settings;
    }

    public static Environment environment() {
        return ormConfiguration.environment;
    }

    public static DBType dbType() {
        return ormConfiguration.dbType;
    }

    public static DBInfo dbInfo() {
        return ormConfiguration.dbInfo;
    }

    private static Map<String, String> properties() {
        Map<String, String> properties = new HashMap<String, String>();

        Map<String, Settings> groups = settings().getGroups(mode() + ".datasources");

        Settings mysqlSetting = groups.get("mysql");

        properties.put("hibernate.show_sql", settings().get("orm.show_sql", "true"));
        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.connection.password", mysqlSetting.get("password"));
        properties.put("hibernate.connection.url", "jdbc:mysql://" + mysqlSetting.get("host") + "/" + mysqlSetting.get("database"));
        properties.put("hibernate.connection.username", mysqlSetting.get("username"));
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.c3p0.min_size", settings().get("orm.pool_min_size", "20"));
        properties.put("hibernate.c3p0.max_size", settings().get("orm.pool_max_size", "20"));
        properties.put("hibernate.c3p0.timeout", settings().get("orm.timeout", "300"));
        properties.put("hibernate.c3p0.max_statements", settings().get("orm.max_statements", "50"));
        properties.put("hibernate.c3p0.idle_test_period", settings().get("orm.idle_test_period", "3000"));
        properties.put("hibernate.connection.characterEncoding", "UTF-8");
        //    properties.put("hibernate.query.factory_class", "org.hibernate.hql.internal.classic.ClassicQueryTranslatorFactory");
        return properties;
    }

    public static void loadModels() {
        try {
            new JPAModelLoader().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void injector(Injector injector) {
        ormConfiguration.injector = injector;
    }

    public static class CSDNORMConfiguration {
        private Settings settings;
        private Environment environment;
        private Class classLoader;
        private String mode;
        private ClassPool classPool;
        private Injector injector;
        private DBType dbType;
        private DBInfo dbInfo;

        public Settings getSettings() {
            return settings;
        }

        public void setSettings(Settings settings) {
            this.settings = settings;
        }

        public Environment getEnvironment() {
            return environment;
        }

        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        public ClassLoader getClassLoader() {
            return classLoader.getClassLoader();
        }

        public void setClassLoader(Class classLoader) {
            this.classLoader = classLoader;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public ClassPool getClassPool() {
            return classPool;
        }

        public void setClassPool(ClassPool classPool) {
            this.classPool = classPool;
        }

        public Injector getInjector() {
            return injector;
        }

        public void setInjector(Injector injector) {
            this.injector = injector;
        }

        public DBType getDbType() {
            return dbType;
        }

        public void setDbType(DBType dbType) {
            this.dbType = dbType;
        }

        public DBInfo getDbInfo() {
            return dbInfo;
        }

        public void setDbInfo(DBInfo dbInfo) {
            this.dbInfo = dbInfo;
        }

        public CSDNORMConfiguration(String _mode, Settings _settings, Class _classLoader) {
            mode = _mode;
            settings = _settings;

            classLoader = _classLoader;
            buildDefaultClassPool();
            buildDefaultDbType();

        }

        public CSDNORMConfiguration(String _mode, Settings _settings, Class _classLoader, ClassPool classPool) {
            mode = _mode;
            settings = _settings;
            classLoader = _classLoader;
            this.classPool = classPool;
            buildDefaultDbType();

        }

        public void buildDefaultClassPool() {
            classPool = new ClassPool();
            classPool.appendSystemPath();
            classPool.appendClassPath(new LoaderClassPath(classLoader.getClassLoader()));
        }

        public void buildDefaultDbType() {
            dbType = new MysqlType();
        }

        public void buildDefaultDBInfo() {
            dbInfo = new DBInfo(settings);
        }


    }

    public static class JPAModelLoader {
        public void load() throws Exception {
            final Enhancer enhancer = new JPAEnhancer(JPA.settings());

            final List<CtClass> classList = new ArrayList<CtClass>();
            ScanService scanService = new DefaultScanService();
            scanService.setLoader(ormConfiguration.classLoader);
            scanService.scanArchives(settings().get("application.model"), new ScanService.LoadClassEnhanceCallBack() {
                @Override
                public Class loaded(DataInputStream classFile) {
                    try {
                        classList.add(enhancer.enhanceThisClass(classFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            enhancer.enhanceThisClass2(classList);


            for (CtClass ctClass : classList) {
                if (ctClass.hasAnnotation(DiscriminatorColumn.class)) {
                    loadClass(ctClass);
                }
            }

            for (CtClass ctClass : classList) {
                if (!ctClass.hasAnnotation(DiscriminatorColumn.class)) {
                    loadClass(ctClass);
                }
            }
        }

        private void loadClass(CtClass ctClass) {
            try {
                Class<Model> clzz = ctClass.toClass(JPA.classLoader(), JPA.class.getProtectionDomain());
                JPA.models.put(clzz.getSimpleName(), clzz);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }
}
