package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.model.Pin;
import com.xn.pento.model.Subscription;
import com.xn.pento.service.PinService;
import com.xn.pento.service.SubscriptionService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: AM9:26
 * To change this template use File | Settings | File Templates.
 */
public class PinController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list(""));

    /**
     * 发布新品文
     *
     * @param "text" 品文标题和描述
     * @param "content" 品文内容
     * @param "board_id" 笔记本ID
     * @param "image" 品文图片ID，可选
     * @return 处理结果
     */
    @At(path = "/api/pin/create.json", types = RestRequest.Method.POST)
    public void create() throws Exception {
        checkParams("text", "content", "board_id");

        Pin pin = findService(PinService.class).create(getUser(), param("text"), param("content"), paramAsInt("board_id"), param("image"));
        if (pin != null) {
            render(Result.ok(pin));
        } else {
            render(Result.fail("failed to create pin"));
        }
    }

    /**
     * 转发品文
     *
     * @param "text" 品文标题和描述
     * @param "board_id" 笔记本ID
     * @param "pin_id"
     * @return 处理结果
     */
    @At(path = "/api/pin/repin.json", types = RestRequest.Method.POST)
    public void repin() throws Exception {
        checkParams("pin_id", "text", "board_id");

        Pin pin = findService(PinService.class).repin(
                getUser(),
                paramAsInt("pin_id"),
                param("text"),
                paramAsInt("board_id")
        );
        if (pin != null) {
            render(Result.ok(pin));
        } else {
            render(Result.fail("failed to create pin"));
        }
    }

    /**
     * 删除品文
     *
     * @param "pin_id" 笔记本ID
     * @return 处理结果
     */
    @At(path = "/api/pin/destroy.json", types = RestRequest.Method.POST)
    public void destroy() throws Exception {
        checkParams("pin_id");

        if (findService(PinService.class).destroy(getUser(), paramAsInt("pin_id"))) {
            render(Result.ok());
        } else {
            render(Result.fail("failed to delete board"));
        }
    }

}
