package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.common.VisibleType;
import com.xn.pento.model.*;
import net.csdn.common.exception.ValidateErrorException;

import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: PM5:24
 * To change this template use File | Settings | File Templates.
 */
public class TimelineService extends BaseService {
    PinService pinService = findService(PinService.class);
    BoardService boardService = findService(BoardService.class);
    UserService userService = findService(UserService.class);

    public void distributePin(User user, Pin pin, Board board) throws Exception {
        // to friends
        List<Friendship> friendships = user.friends(0, -1);
        for (Friendship friendship : friendships) {
            FriendTimeline timeline = FriendTimeline.create(map(
                    "user_id", friendship.attrAsInt("user_id"),
                    "pin_id", pin.id()
            ));
            if (!timeline.save()) {
                throw new ValidateErrorException(timeline.validateResults);
            }
        }

        // to subscriptions
        List<Subscription> subscriptions = Subscription.where("board_id=:board_id", map(
                "board_id", pin.attrAsInt("board_id")
        )).fetch();

        for (Subscription subscription : subscriptions) {
            UserTimeline timeline = UserTimeline.create(map(
                    "user_id", subscription.attrAsInt("user_id"),
                    "pin_id", pin.id()
            ));
            if (!timeline.save()) {
                throw new ValidateErrorException(timeline.validateResults);
            }
        }

        // to category
        CategoryTimeline categoryTimeline = CategoryTimeline.create(map(
                "category_id", board.attrAsInt("category_id"),
                "board_id", board.id(),
                "pin_id", pin.id()
        ));
        if (!categoryTimeline.save()) {
            throw new ValidateErrorException(categoryTimeline.validateResults);
        }

    }

    private Map pinToMap(Pin pin) throws Exception {
        Map pinMap = map();
        pinMap.put("id", pin.id());
        pinMap.put("text", pin.attr("text"));
        pinMap.put("repin_count", pin.attrAsInt("repin_count"));
        pinMap.put("comment_count", pin.attrAsInt("comment_count"));

        if (pin.attr("image") != null) {
            pinMap.put("image", pin.attr("image"));
            pinMap.put("image_height", pin.attrAsInt("image_height"));
        }

        Board board = boardService.findById(pin.attrAsInt("board_id"));
        pinMap.put("board", board);

        User pinUser = userService.findById(pin.attrAsInt("user_id"));
        pinMap.put("user", pinUser);

        if (pin.attr("pin_id", Integer.class) != null) {
            // 转发的Pin
            Pin repostPin = pinService.findById(pin.attrAsInt("pin_id"));
            if (repostPin != null) {
                pinMap.put("pin", repostPin);
                findService(FileStoreService.class).filterFileUrl(repostPin, "image");
            }
        } else {
            // 原始的Pin
            pinMap.put("content", pin.content());
        }

        findService(FileStoreService.class).filterFileUrl(pinMap, "image");
        return pinMap;
    }

    public List<Map> latestPins(int startId, int count) throws Exception {
        List<Pin> latestPins = Pin.where("id<:id", map(
                "id", startId
        )).order("id desc").limit(count).fetch();

        List<Map> pins = list();
        for (Pin pin : latestPins) {
            pins.add(pinToMap(pin));
        }

        return pins;
    }

    public List<Map> userPins(User user, int startId, int count) throws Exception {
        List<Pin> boardPins = Pin.where("user_id=:user_id and id<:id", map(
                "user_id", user.id(),
                "id", startId
        )).order("id desc").limit(count).fetch();

        List<Map> pins = list();
        for (Pin pin : boardPins) {
            pins.add(pinToMap(pin));
        }

        return pins;
    }

    public List<Map> subscriptionPins(User user, int startId, int count) throws Exception {
        List<UserTimeline> userTimelines = UserTimeline.where("user_id=:user_id and id<:id", map(
                "user_id", user.id(),
                "id", startId
        )).order("id DESC").limit(count).fetch();

        List<Map> pins = list();
        PinService pinService = findService(PinService.class);
        BoardService boardService = findService(BoardService.class);
        UserService userService = findService(UserService.class);
        for (UserTimeline userTimeline : userTimelines) {
            Pin pin = pinService.findById(userTimeline.attrAsInt("pin_id"));
            if (pin == null || pin.attrAsInt("visible") != VisibleType.visible.ordinal()) {
                // ignore removed pin
                continue;
            }

            pins.add(pinToMap(pin));
        }

        return pins;
    }

    public List<Map> friendPins(User user, int startId, int count) throws Exception {
        List<FriendTimeline> friendTimelines = FriendTimeline.where("user_id=:user_id and id<:id", map(
                "user_id", user.id(),
                "id", startId
        )).order("id DESC").limit(count).fetch();

        List<Map> pins = list();
        for (FriendTimeline friendTimeline : friendTimelines) {
            Pin pin = pinService.findById(friendTimeline.attrAsInt("pin_id"));
            if (pin == null || pin.attrAsInt("visible") != VisibleType.visible.ordinal()) {
                // ignore removed pin
                continue;
            }

            pins.add(pinToMap(pin));
        }

        return pins;
    }

    public List<Map> categoryPins(int categoryId, int startId, int count) throws Exception {
        List<CategoryTimeline> categoryTimelines = CategoryTimeline.where("category_id=:category_id and id<:id", map(
                "category_id", categoryId,
                "id", startId
        )).order("id DESC").limit(count).fetch();

        List<Map> pins = list();
        for (CategoryTimeline categoryTimeline : categoryTimelines) {
            Pin pin = pinService.findById(categoryTimeline.attrAsInt("pin_id"));
            if (pin == null || pin.attrAsInt("visible") != VisibleType.visible.ordinal()) {
                // ignore removed pin
                continue;
            }

            pins.add(pinToMap(pin));
        }

        return pins;
    }

    public List<Map> boardPins(int boardId, int startId, int count) throws Exception {
        List<Pin> boardPins = Pin.where("board_id=:board_id and id<:id", map(
                "board_id", boardId,
                "id", startId
        )).order("id desc").limit(count).fetch();

        List<Map> pins = list();
        for (Pin pin : boardPins) {
            pins.add(pinToMap(pin));
        }

        return pins;
    }

}
