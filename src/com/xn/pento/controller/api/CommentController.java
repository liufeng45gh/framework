package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.model.Comment;
import com.xn.pento.service.CommentService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: PM11:18
 * To change this template use File | Settings | File Templates.
 */
public class CommentController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list(""));

    /**
     * 获取品文的评论
     *
     * @param "pin_id" 需要查询的品文ID
     * @param "offset" 结果集起始位置
     * @param "count" 返回的记录条数
     * @return 评论列表
     */
    @At(path = "/api/comment/show.json", types = RestRequest.Method.GET)
    public void show() throws Exception {
        checkParams("pin_id");

        List<Map> comments = findService(CommentService.class).show(paramAsInt("pin_id"), offset(), count());
        render(Result.ok(comments));
    }

    /**
     * 创建评论
     *
     * @param "pin_id" 需要创建评论的品文ID
     * @param "text" 需要创建的评论内容
     * @return 处理结果
     */
    @At(path = "/api/comment/create.json", types = RestRequest.Method.GET)
    public void create() throws Exception {
        checkParams("pin_id", "text");

        Comment comment = findService(CommentService.class).create(getUser(), paramAsInt("pin_id"), param("text"));
        if (comment != null) {
            render(Result.ok(comment));
        } else {
            render(Result.fail("failed to create comment"));
        }
    }
}
