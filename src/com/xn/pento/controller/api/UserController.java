package com.xn.pento.controller.api;

import com.xn.pento.common.Result;
import com.xn.pento.common.BaseController;
import com.xn.pento.model.User;
import com.xn.pento.model.UserToken;
import com.xn.pento.service.UserService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.common.exception.ArgumentErrorException;
import net.csdn.common.exception.AuthException;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.modules.http.RestRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;
import static net.csdn.filter.FilterHelper.BeforeFilter.only;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-2-20
 * Time: PM1:32
 * To change this template use File | Settings | File Templates.
 */
public class UserController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list("weiboLogin", "guestLogin"));

    @BeforeFilter
    private final static Map $checkGuestAuth = map(except, list("weiboLogin", "guestLogin"));

    /**
     *
     * 获取用户信息
     *
     * @param "uid" 需要查询的用户ID
     * @return 用户信息
     */
    @At(path = "/api/user/show.json", types = RestRequest.Method.GET)
    public void show() throws Exception {
        final User user = findService(UserService.class).findById(paramAsInt("uid"));
        if (user == null) {
            throw new RecordNotFoundException("user not found");
        }

        render(Result.ok(user));
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @At(path = "/api/user/profile.json", types = RestRequest.Method.GET)
    public void profile() throws Exception {
        render(Result.ok(getUser()));
    }

    /**
     * 用户登录
     *
     * @param "weibo_id" 微博用户ID
     * @param "access_token" 微博访问token
     * @return 访问token
     */
    @At(path = "/api/user/weibo_login.json", types = RestRequest.Method.POST)
    public void weiboLogin() throws Exception {
        String weiboId = param("weibo_id");
        if (weiboId == null)
            throw new ArgumentErrorException("weibo_id missing");

        String accessToken = param("access_token");
        if (accessToken == null)
            throw new ArgumentErrorException("access_token missing");

        UserToken userToken = findService(UserService.class).loginByWeibo(weiboId, accessToken, map(
                "ip", request.getRemoteAddr(),
                "user_agent", header("User-Agent"),
                "mac", header("mac")
        ));
        render(Result.ok(userToken.attr("token")));
    }

    /**
     * 用户登录
     *
     * @return 访问token
     */
    @At(path = "/api/user/guest_login.json", types = RestRequest.Method.POST)
    public void guestLogin() throws Exception {
        UserToken userToken = findService(UserService.class).loginByGuest(map(
                "ip", request.getRemoteAddr(),
                "user_agent", header("User-Agent"),
                "mac", header("mac")
        ));
        render(Result.ok(userToken.attr("token")));
    }

    /**
     * 新浪微博关系导入
     *
     * 微博好友列表用json格式上传，例如：
     * [
     *  {
     *      "id" : "12345",
     *      "nick" : "test",
     *      "profile_image" : "http://weobi.com/12345/12345"
     *  }
     * ]
     * @return 访问token
     */
    @At(path = "/api/user/weibo_import.json", types = RestRequest.Method.POST)
    public void weiboImport() throws Exception {
        JSONArray weiboUsers = JSONArray.fromObject(request.contentAsString());
        for (int i = 0; i < weiboUsers.size(); i++) {
            JSONObject weiboUser = weiboUsers.getJSONObject(i);

        }
    }

    /**
     * 用户登出
     *
     * @param "token" 需要登出的用户token
     * @return 处理结果
     */
    @At(path = "/api/user/logout.json", types = RestRequest.Method.POST)
    public void logout() throws Exception {
        UserToken userToken = findService(UserService.class).findUserToken(param("token"));
        if (userToken != null) {
            userToken.attr("valid", false);
            userToken.save();
        }

        render(Result.ok());
    }

//    @BeforeFilter
//    private final static Map $checkManageAuth = map(only, list("create"));
//
//    @At(path = "/api/user/create.json", types = RestRequest.Method.POST)
//    public void create() throws Exception {
//        if (getManageUser() == null) {
//            throw new AuthException("no permission");
//        }
//
//        User user = findService(UserService.class).create(params());
//        render(Result.ok(user));
//    }
//
//    @At(path = "/api/user/update.json/{id}", types = RestRequest.Method.POST)
//    public void update() throws Exception {
//        User loginUser = getUser();
//        int userId = paramAsInt("id");
//
//        if (userId != loginUser.id() && getManageUser() == null) {
//            throw new AuthException("no permission");
//        }
//
//        findService(UserService.class).update(userId, params());
//        render(Result.ok());
//    }

}
