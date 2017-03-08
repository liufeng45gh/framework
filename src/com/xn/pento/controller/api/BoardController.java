package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.model.Board;
import com.xn.pento.service.BoardService;
import com.xn.pento.service.FileStoreService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-20
 * Time: PM7:28
 * To change this template use File | Settings | File Templates.
 */
public class BoardController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list(""));

    /**
     * 获取用户的笔记本列表
     *
     * @param "offset" 结果集起始位置
     * @param "count" 返回的记录条数
     * @return 笔记本列表
     */
    @At(path = "/api/board/show.json", types = RestRequest.Method.GET)
    public void show() throws Exception {
        List<Board> boards = findService(BoardService.class).userBoards(getUser(), offset(), count());
        render(Result.ok(boards));
    }

    /**
     * 获取指定类别下推荐的笔记本列表，按照推荐指数排序
     *
     * @param "category_id" 需要查询的类别ID
     * @return 笔记本列表
     */
    @At(path = "/api/board/recommend.json", types = RestRequest.Method.GET)
    public void recommend() throws Exception {
        checkParams("category_id");

        List<Board> boards = findService(BoardService.class).recommends(paramAsInt("category_id"));
        findService(FileStoreService.class).filterFileUrl(boards, "cover");
        render(Result.ok(boards));
    }

    /**
     * 添加笔记本
     *
     * @param "name" 笔记本名称
     * @param "category_id" 笔记本类别ID
     * @param "cover" 封面图片ID
     * @return 处理结果
     */
    @At(path = "/api/board/create.json", types = RestRequest.Method.POST)
    public void create() throws Exception {
        checkParams("name", "category_id");

        Board board = findService(BoardService.class).create(
                getUser(),
                param("name"),
                paramAsInt("category_id"),
                param("cover"));
        if (board != null) {
            findService(FileStoreService.class).filterFileUrl(board, "cover");
            render(Result.ok(board));
        } else {
            render(Result.fail("failed to create board"));
        }
    }

    /**
     * 删除笔记本
     *
     * @param "board_id" 需要删除的笔记本ID
     * @return 处理结果
     */
    @At(path = "/api/board/destroy.json", types = RestRequest.Method.POST)
    public void destroy() throws Exception {
        checkParams("board_id");

        if (findService(BoardService.class).destroy(getUser(), paramAsInt("board_id"))) {
            render(Result.ok());
        } else {
            render(Result.fail("failed to unsubscribe board"));
        }
    }

    @At(path = "/api/board/update.json", types = RestRequest.Method.POST)
    public void update() throws Exception {
        checkParams("board_id");

        if (findService(BoardService.class).update(
                getUser(),
                paramAsInt("board_id"),
                param("name"),
                paramAsInt("category_id"),
                param("cover"))) {
            render(Result.ok());
        } else {
            render(Result.fail("failed to unsubscribe board"));
        }
    }
}
