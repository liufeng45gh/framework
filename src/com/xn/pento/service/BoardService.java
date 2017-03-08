package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.model.*;
import net.csdn.common.exception.AuthException;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.common.exception.ValidateErrorException;
import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-20
 * Time: PM7:29
 * To change this template use File | Settings | File Templates.
 */
public class BoardService extends BaseService {
    public Board findById(int boardId) throws Exception {
        return Board.find(boardId);
    }

    public List<Board> boards(int offset, int count) throws Exception {
        return Board.order("id desc").offset(offset).limit(count).fetch();
    }

    public List<Board> userBoards(User user, int offset, int count) throws Exception {
        return Board.where("user_id=:user_id", map(
                "user_id", user.id()
        )).order("updated_at desc").offset(offset).limit(count).fetch();
    }

    public List<Board> categoryBoards(int categoryId, int offset, int count) throws Exception {
        return Board.where("category_id=:category_id", map(
                "category_id", categoryId
        )).order("updated_at desc").offset(offset).limit(count).fetch();
    }

    public List<Board> recommends(int categoryId) throws Exception {
        return Board.where("category_id=:category_id", map(
                "category_id", categoryId
        )).order("recommend DESC, pin_count DESC").limit(50).fetch();
    }

    public Board create(User user, String name, int categoryId, String cover) throws Exception {
        Board board = Board.create(map(
                "name", name,
                "category_id", categoryId,
                "user_id", user.id(),
                "recommend", false,
                "cover", cover,
                "visible", true
        ));
        if (!board.save()) {
            throw new ValidateErrorException(board.validateResults);
        }

        List<Friendship> followers = user.followers(0, -1);
        SubscriptionService subscriptionService = findService(SubscriptionService.class);
        for (Friendship friendship : followers) {
            subscriptionService.create(friendship.attrAsInt("user_id"), board.id());
        }

        return board;
    }

    public boolean update(User user, int boardId, String name, int categoryId, String cover) throws Exception {
        Board board = findById(boardId);
        if (board == null || board.attrAsInt("user_id") != user.id()) {
            throw new RecordNotFoundException("board not found");
        }

        if (name != null) {
            board.attr("name", name);
        }

        if (cover != null) {
            board.attr("cover", cover);
        }

        if (categoryId != board.attrAsInt("category_id")) {
            board.attr("category_id", categoryId);
            CategoryTimeline.getJPAContext().jpql(CategoryTimeline.class.getName()).update(
                    "set category_id=? where board_id=?",
                    new Object[] {
                            board.attrAsInt("category_id"), board.id()
                    }
            );
        }

        if (!board.update()) {
            throw new ValidateErrorException(board.validateResults);
        }

        return true;
    }

    public boolean destroy(User user, int boardId) throws Exception {
        Board board = Board.find(boardId);
        if (board == null || board.attrAsInt("user_id") != user.id()) {
            throw new RecordNotFoundException("board not found");
        }

        board.userRemove();
        return board.save();
    }
}
