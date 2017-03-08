package net.csdn.modules.http;

import net.csdn.ServiceFramework;
import net.csdn.annotation.filter.AroundFilter;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.common.collect.Tuple;
import net.csdn.common.logging.CSLogger;
import net.csdn.common.logging.Loggers;
import net.csdn.common.path.PathTrie;
import net.csdn.common.exception.ArgumentErrorException;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.filter.FilterHelper;
import net.csdn.common.reflect.ReflectHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.csdn.common.collections.WowCollections.list;
import static net.csdn.common.logging.support.MessageFormat.format;

/**
 * BlogInfo: william
 * Date: 11-9-5
 * Time: 下午4:24
 */
public class RestController {

    private CSLogger logger = Loggers.getLogger(getClass());

    private final PathTrie<Tuple<Class<ApplicationController>, Method>> getHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();
    private final PathTrie<Tuple<Class<ApplicationController>, Method>> postHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();
    private final PathTrie<Tuple<Class<ApplicationController>, Method>> putHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();
    private final PathTrie<Tuple<Class<ApplicationController>, Method>> deleteHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();
    private final PathTrie<Tuple<Class<ApplicationController>, Method>> headHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();
    private final PathTrie<Tuple<Class<ApplicationController>, Method>> optionsHandlers = new PathTrie<Tuple<Class<ApplicationController>, Method>>();

    public void registerHandler(RestRequest.Method method, String path, Tuple<Class<ApplicationController>, Method> handler) {
        switch (method) {
            case GET:
                getHandlers.insert(path, handler);
                break;
            case DELETE:
                deleteHandlers.insert(path, handler);
                break;
            case POST:
                postHandlers.insert(path, handler);
                break;
            case PUT:
                putHandlers.insert(path, handler);
                break;
            case OPTIONS:
                optionsHandlers.insert(path, handler);
                break;
            case HEAD:
                headHandlers.insert(path, handler);
                break;
            default:
                throw new ArgumentErrorException("Can't handle [" + method + "] for path [" + path + "]");
        }
    }


    public void dispatchRequest(final RestRequest request, RestResponse restResponse) throws Exception {
        final Tuple<Class<ApplicationController>, Method> handlerKey = getHandler(request);
        if (handlerKey == null) {
            throw new RecordNotFoundException(format("你请求的URL地址[{}]不存在", request.rawPath().toString()));
        }
        ApplicationController applicationController = ServiceFramework.injector.getInstance(handlerKey.v1());

        enhanceApplicationController(applicationController, request, restResponse);

        filter(handlerKey, applicationController);
    }

    private void enhanceApplicationController(ApplicationController applicationController, final RestRequest request, RestResponse restResponse) throws Exception {
        ReflectHelper.field(applicationController, "request", request);
        ReflectHelper.field(applicationController, "restResponse", restResponse);
    }

    private List<Method> whoFilterThisMethod(Class clzz, List<Field> filters, Method method) throws Exception {

        List<Method> result = list();
        for (Field filter : filters) {
            filter.setAccessible(true);
            Map filterInfo = (Map) filter.get(null);
            String filterMethod = filter.getName().substring(1, filter.getName().length());
            if (filterInfo.containsKey(FilterHelper.BeforeFilter.only)) {
                List<String> actions = (List<String>) filterInfo.get(FilterHelper.BeforeFilter.only);
                if (actions.contains(method.getName())) {
                    result.add(ReflectHelper.findMethodByName(clzz, filterMethod));
                }
            } else {
                if (filterInfo.containsKey(FilterHelper.BeforeFilter.except)) {
                    List<String> actions = (List<String>) filterInfo.get(FilterHelper.BeforeFilter.except);
                    if (!actions.contains(method.getName())) {
                        result.add(ReflectHelper.findMethodByName(clzz, filterMethod));
                    }
                } else {
                    result.add(ReflectHelper.findMethodByName(clzz, filterMethod));
                }
            }
        }
        return result;
    }

    private void filter(Tuple<Class<ApplicationController>, Method> handlerKey, ApplicationController applicationController) throws Exception {
        //check beforeFilter

        List<Field> globalBeforeFilters = ReflectHelper.fields(handlerKey.v1().getSuperclass(), BeforeFilter.class);
        List<Field> globalAroundFilters = ReflectHelper.fields(handlerKey.v1().getSuperclass(), AroundFilter.class);


        List<Field> beforeFilters = ReflectHelper.fields(handlerKey.v1(), BeforeFilter.class);
        List<Field> aroundFilters = ReflectHelper.fields(handlerKey.v1(), AroundFilter.class);

        globalBeforeFilters.addAll(beforeFilters);
        globalAroundFilters.addAll(aroundFilters);

        beforeFilters.clear();
        aroundFilters.clear();

        beforeFilters.addAll(globalBeforeFilters);
        aroundFilters.addAll(globalAroundFilters);


        Method action = handlerKey.v2();

        List<Method> beforeFilterFilterThisAction = whoFilterThisMethod(handlerKey.v1(), beforeFilters, action);

        List<Method> aroundFilterFilterThisAction = whoFilterThisMethod(handlerKey.v1(), aroundFilters, action);

        for (Method filter : beforeFilterFilterThisAction) {
            filter.setAccessible(true);
            filter.invoke(applicationController);
        }

        Iterator<Method> iterator = aroundFilterFilterThisAction.iterator();

        WowAroundFilter wowAroundFilter = null;
        WowAroundFilter first = null;
        if (iterator.hasNext()) {
            Method currentFilter = iterator.next();
            wowAroundFilter = new WowAroundFilter(currentFilter, handlerKey.v2(), applicationController);
            first = wowAroundFilter;
        }
        while (iterator.hasNext()) {
            Method currentFilter = iterator.next();
            wowAroundFilter.setNext(new WowAroundFilter(currentFilter, handlerKey.v2(), applicationController));
            wowAroundFilter = wowAroundFilter.getNext();
        }

        if (first != null) {
            first.invoke();
        } else {
            handlerKey.v2().invoke(applicationController);
        }

    }


    private Tuple<Class<ApplicationController>, Method> getHandler(RestRequest request) {
        String path = getPath(request);
        RestRequest.Method method = request.method();
        if (method == RestRequest.Method.GET) {
            return getHandlers.retrieve(path, request.params());
        } else if (method == RestRequest.Method.POST) {
            return postHandlers.retrieve(path, request.params());
        } else if (method == RestRequest.Method.PUT) {
            return putHandlers.retrieve(path, request.params());
        } else if (method == RestRequest.Method.DELETE) {
            return deleteHandlers.retrieve(path, request.params());
        } else if (method == RestRequest.Method.HEAD) {
            return headHandlers.retrieve(path, request.params());
        } else if (method == RestRequest.Method.OPTIONS) {
            return optionsHandlers.retrieve(path, request.params());
        } else {
            return null;
        }
    }

    private String getPath(RestRequest request) {
        return request.rawPath();
    }


}
