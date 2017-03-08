package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.common.TimelineType;
import com.xn.pento.model.Board;
import com.xn.pento.service.BoardService;
import com.xn.pento.service.SubscriptionService;
import com.xn.pento.service.TimelineService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.common.exception.ArgumentErrorException;
import net.csdn.modules.http.RestRequest;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;
import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: PM4:56
 * To change this template use File | Settings | File Templates.
 */
public class TimelineController extends BaseController {
    @BeforeFilter
    private final static Map $checkAuth = map(except, list("pins", "boards"));

    @BeforeFilter
    private final static Map $checkGuestAuth = map(only, list("pins", "boards"));

    /**
     *
     * 品文时间线接口
     *
     * @param "type" 时间线类型
     * @param "start_id" 起始记录ID
     * @param "count" 返回记录条数
     *
     * @return 品文列表
     *
     * @throws Exception
     */
    @At(path = "/api/timeline/{type}/pins.json", types = RestRequest.Method.GET)
    public void pins() throws Exception {
        checkParams("type");

        TimelineType timelineType = TimelineType.valueOf(param("type"));
        switch (timelineType) {
            case subscription:
                guestUserError();
                render(Result.ok(findService(TimelineService.class).subscriptionPins(getUser(), startId(), count())));
                break;
            case user:
                guestUserError();
                render(Result.ok(findService(TimelineService.class).userPins(getUser(), startId(), count())));
                break;
            case friend:
                guestUserError();
                render(Result.ok(findService(TimelineService.class).friendPins(getUser(), startId(), count())));
                break;
            case category:
                checkParams("category_id");
                render(Result.ok(findService(TimelineService.class).categoryPins(paramAsInt("category_id"), startId(), count())));
                break;
            case board:
                checkParams("board_id");
                render(Result.ok(findService(TimelineService.class).boardPins(paramAsInt("board_id"), startId(), count())));
                break;
            case latest:
            default:
                render(Result.ok(findService(TimelineService.class).latestPins(startId(), count())));
                break;
        }
    }

    /**
     *
     * 笔记本时间线接口
     *
     * @param "type" 时间线类型
     * @param "categoty_id" 类别ID，类别时间线时需要提供
     * @param "offset" 起始位置
     * @param "count" 返回的记录条数
     *
     * @return 笔记本列表
     *
     * @throws Exception
     */
    @At(path = "/api/timeline/{type}/boards.json", types = RestRequest.Method.GET)
    public void boards() throws Exception {
        checkParams("type");

        TimelineType timelineType = TimelineType.valueOf(param("type"));
        switch (timelineType) {
            case user:
                guestUserError();
                render(Result.ok(findService(BoardService.class).userBoards(getUser(), offset(), count())));
                break;
            case friend:
                guestUserError();
                // TODO
                break;
            case category:
                checkParams("category_id");
                render(Result.ok(findService(BoardService.class).categoryBoards(paramAsInt("category_id"), offset(), count())));
                break;
            case latest:
            default:
                render(Result.ok(findService(BoardService.class).boards(offset(), count())));
        }
    }

}
