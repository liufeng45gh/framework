package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.model.Comment;
import com.xn.pento.model.Pin;
import com.xn.pento.model.User;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.common.exception.ValidateErrorException;

import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: PM11:34
 * To change this template use File | Settings | File Templates.
 */
public class CommentService extends BaseService {
    PinService pinService = findService(PinService.class);
    UserService userService = findService(UserService.class);

    private Map commentToMap(Comment comment) throws Exception {
        User user = userService.findById(comment.attrAsInt("user_id"));

        return map(
                "user", user,
                "text", comment.attr("text"),
                "comment_id", comment.id()
        );
    }

    public List<Map> show(int pinId, int offset, int count) throws Exception {
        List<Comment> comments = Comment.where("pin_id=:pin_id", map(
                "pin_id", pinId
        )).order("id DESC").offset(offset).limit(count).fetch();

        List<Map> commentMaps = list();
        for (Comment comment : comments) {
            commentMaps.add(commentToMap(comment));
        }

        return commentMaps;
    }

    public Comment create(User user, int pinId, String text) throws Exception {
        Pin pin = pinService.findById(pinId);
        if (pin == null) {
            throw new RecordNotFoundException("pin not found");
        }

        Comment comment = Comment.create(map(
                "user_id", user.id(),
                "pin_id", pin.id(),
                "text", text
        ));

        if (!comment.save()) {
            throw new ValidateErrorException(comment.validateResults);
        }

        return comment;
    }
}
