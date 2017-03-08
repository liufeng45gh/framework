package com.xn.pento.common;

import com.xn.pento.model.ManageUser;
import com.xn.pento.model.User;
import com.xn.pento.model.UserToken;
import com.xn.pento.service.UserService;
import net.csdn.annotation.filter.AroundFilter;
import net.csdn.common.exception.ArgumentErrorException;
import net.csdn.common.exception.AuthException;
import net.csdn.modules.http.ApplicationController;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-12
 * Time: PM3:54
 * To change this template use File | Settings | File Templates.
 */
public class BaseController extends ApplicationController {
    public BaseController() {
        super();
        config.setExcludes(new String[]{"password"});
    }

    public void checkAuth() throws Exception {
        if (getUser() == null && getManageUser() == null) {
            throw new AuthException("auth is invalid");
        }

        if (getUser() != null && getUser().isGuest()) {
            throw new AuthException("guest user");
        }
    }

    public void checkGuestAuth() throws Exception {
        if (getUser() == null && getManageUser() == null) {
            throw new AuthException("auth is invalid");
        }
    }

    public void guestUserError() throws Exception {
        if (getUser() != null && getUser().isGuest()) {
            throw new AuthException("guest user");
        }
    }

    public void checkManageAuth() throws Exception {
        if (getManageUser() == null) {
            throw new AuthException("manage auth is invalid");
        }
    }

    protected ManageUser getManageUser() throws Exception {
        Object user = request.getSession().getAttribute("manage_user");
        if (user instanceof ManageUser) {
            return (ManageUser)user;
        }

        return null;
    }

    protected User getUser() throws Exception {
        if (param("token") != null) {
            User user = findService(UserService.class).findByToken(param("token"));
            if (user instanceof User) {
                return (User)user;
            }
        }

        // disable login for normal user for now
//        Object user = request.getSession().getAttribute("user");
//        if (user instanceof User) {
//            return (User)user;
//        }

        return null;
    }

    protected int offset() {
        if (param("offset") == null) {
            // default set to start
            return 0;
        }

        return paramAsInt("offset");
    }

    private static final int MAX_COUNT = 100;
    protected int count() {
        if (param("count") == null) {
            // max limit set to 50
            return MAX_COUNT;
        }

        int count = paramAsInt("count");
        return Math.min(count, MAX_COUNT);
    }

    protected int startId() {
        int startId = Integer.MAX_VALUE;
        if (param("start_id") != null) {
            startId = paramAsInt("start_id");
        }
        return startId;
    }


    protected void checkParams(String... keys) throws Exception {
        List<String> missingKeys = list();
        for (String key : keys) {
            if (param(key) == null) {
                missingKeys.add(key);
            }
        }

        if (missingKeys.size() > 0) {
            throw new ArgumentErrorException("parameters missing: " + missingKeys);
        }
    }

    protected void ensure(Object object, String name) {
        if (object == null) {
            throw new ArgumentErrorException(name + " not found");
        }
    }
}
