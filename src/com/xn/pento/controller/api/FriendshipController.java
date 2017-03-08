package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.model.User;
import com.xn.pento.service.FriendshipService;
import com.xn.pento.service.UserService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-18
 * Time: PM1:05
 *
 */
public class FriendshipController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list(""));

    /**
     * 获取用户的关注列表
     *
     * @param "uid" 需要查询的用户ID
     * @param "offset" 结果集起始位置
     * @param "count" 返回的记录条数
     * @return 关注列表
     */
    @At(path = "/api/friendship/interests.json", types = RestRequest.Method.GET)
    public void interests() throws Exception {
        checkParams("uid");

        User user = findService(UserService.class).findById(paramAsInt("uid"));
        ensure(user, "user");

        List<User> interests = findService(FriendshipService.class).interests(user, offset(), count());
        render(Result.ok(interests));
    }

    /**
     * 获取用户的粉丝列表
     *
     * @param "uid" 需要查询的用户ID
     * @return 粉丝列表
     */
    @At(path = "/api/friendship/followers.json", types = RestRequest.Method.GET)
    public void followers() throws Exception {
        checkParams("uid");

        User user = findService(UserService.class).findById(paramAsInt("uid"));
        ensure(user, "user");

        List<User> followers = findService(FriendshipService.class).followers(user, offset(), count());
        render(Result.ok(followers));
    }

    /**
     * 获取好友列表（双向关注）
     *
     * @return 好友列表
     */
    @At(path = "/api/friendship/friends.json", types = RestRequest.Method.GET)
    public void friends() throws Exception {
        render(Result.ok(findService(FriendshipService.class).friends(getUser(), offset(), count())));
    }

    /**
     * 关注某用户
     *
     * @param uid 需要关注的用户ID
     * @return 处理结果
     */
    @At(path = "/api/friendship/create.json", types = RestRequest.Method.POST)
    public void create() throws Exception {
        checkParams("uid");

        User interestUser = findService(UserService.class).findById(paramAsInt("uid"));
        ensure(interestUser, "user");

        if (findService(FriendshipService.class).create(getUser(), interestUser)) {
            render(Result.ok());
        } else {
            render(Result.fail("failed to add friend"));
        }
    }

    /**
     * 取消关注某用户
     *
     * @param "uid" 需要取消关注的用户ID
     * @return 处理结果
     */
    @At(path = "/api/friend/destroy.json", types = RestRequest.Method.POST)
    public void destroy() throws Exception {
        checkParams("uid");

        User interestUser = findService(UserService.class).findById(paramAsInt("uid"));
        ensure(interestUser, "user");

        findService(FriendshipService.class).destroy(getUser(), interestUser);
        render(Result.ok());
    }
}
