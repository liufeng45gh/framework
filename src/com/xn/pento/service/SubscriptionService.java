package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.model.Board;
import com.xn.pento.model.Subscription;
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
 * Date: 13-3-19
 * Time: PM7:43
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionService extends BaseService {
    public List<Board> subscriptions(User user, int offset, int count) throws Exception {
        List<Board> boards = list();

        List<Subscription> subscriptions = user.subscriptions(offset, count);
        for (Subscription subscription : subscriptions) {
            boards.add(subscription.board());
        }

        return boards;
    }

    public Subscription create(int userId, int boardId) throws Exception {
        User user = findService(UserService.class).findById(userId);
        if (user == null) {
            throw new RecordNotFoundException("user not found");
        }

        return create(user, boardId);
    }

    public Subscription create(User user, int boardId) {
        Board board = Board.find(boardId);
        if (board == null) {
            throw new RecordNotFoundException("board not found");
        }

        Subscription subscription = Subscription.create(map(
                "user_id", user.id(),
                "board_id", board.id(),
                "board_user_id", board.attrAsInt("user_id")
        ));

        if (!subscription.save()) {
            throw new ValidateErrorException(subscription.validateResults);
        }

        Board.getJPAContext().jpql(Board.class.getName()).update(
                "set subscribe_count=subscribe_count+1 where id=?",
                new Object[] { boardId }
        );

        return subscription;
    }

    public boolean destroy(User user, int boardId) {
        Board board = Board.find(boardId);
        if (board == null) {
            throw new RecordNotFoundException("board not found");
        }

        Subscription.delete("user_id=:user_id and board_id=:board_id", map(
                "user_id", user.id(),
                "board_id", board.id()
        ));

        Board.getJPAContext().jpql(Board.class.getName()).update(
                "set subscribe_count=subscribe_count-1 where id=?",
                new Object[] { boardId }
        );

        return true;
    }
}
