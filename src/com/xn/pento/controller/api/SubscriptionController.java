package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.model.Board;
import com.xn.pento.model.Subscription;
import com.xn.pento.model.User;
import com.xn.pento.service.FriendshipService;
import com.xn.pento.service.SubscriptionService;
import com.xn.pento.service.UserService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-19
 * Time: PM7:24
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionController extends BaseController {
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
    @At(path = "/api/subscription/show.json", types = RestRequest.Method.GET)
    public void show() throws Exception {
        List<Board> boards = findService(SubscriptionService.class).subscriptions(getUser(), offset(), count());
        render(Result.ok(boards));
    }

    /**
     * 关注某笔记本
     *
     * @param "board_id" 需要关注的笔记本ID列表，如有多个用,隔开
     * @return 处理结果
     */
    @At(path = "/api/subscription/create.json", types = RestRequest.Method.POST)
    public void create() throws Exception {
        List<Integer> ids = list();
        if (param("board_id") != null) {
            StringTokenizer tokenizer = new StringTokenizer(param("board_id"), ",");
            while (tokenizer.hasMoreElements()) {
                ids.add(Integer.parseInt(tokenizer.nextToken()));
            }
        }

        SubscriptionService subscriptionService = findService(SubscriptionService.class);

        for (int id : ids) {
            Subscription subscription = subscriptionService.create(getUser(), id);
            if (subscription == null) {
                render(Result.fail("failed to subscribe board"));
                return;
            }
        }

        render(Result.ok());
    }

    /**
     * 取消关注某笔记本
     *
     * @param "board_id" 需要取消关注的笔记本ID
     * @return 处理结果
     */
    @At(path = "/api/subscription/destroy.json", types = RestRequest.Method.POST)
    public void destroy() throws Exception {
        checkParams("board_id");

        if (findService(SubscriptionService.class).destroy(getUser(), paramAsInt("board_id"))) {
            render(Result.ok());
        } else {
            render(Result.fail("failed to unsubscribe board"));
        }
    }
}
