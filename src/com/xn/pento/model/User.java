package com.xn.pento.model;

import com.xn.pento.common.BaseModel;
import net.csdn.annotation.validate.Validate;

import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;
import static net.csdn.validate.ValidateHelper.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-2-20
 * Time: PM1:32
 * To change this template use File | Settings | File Templates.
 */
public class User extends BaseModel {
    public static final int guestUserId = 101;

    public List<Friendship> interests(int offset, int count) {
        return Friendship.where("user_id=:user_id", map(
                "user_id", id()
        )).offset(offset).limit(count).fetch();
    }

    public List<Friendship> followers(int offset, int count) {
        return Friendship.where("follow_id=:follow_id", map(
                "follow_id", id()
        )).offset(offset).limit(count).fetch();
    }

    public List<Friendship> friends(int offset, int count) {
        return Friendship.where("user_id=:user_id and friend=1", map(
                "user_id", id()
        )).offset(offset).limit(count).fetch();
    }

    public List<UserToken> user_tokens() {
        return UserToken.where("user_id=:user_id", map(
                "user_id", id()
        )).fetch();
    }

    public List<Subscription> subscriptions(int offset, int count) {
        return Subscription.where("user_id=:user_id", map(
                "user_id", id()
        )).offset(offset).limit(count).fetch();
    }

    @Validate
    private final static Map $email = map(
            format, map("message", "{}格式不正确", "with", "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
    );

    public boolean isGuest() {
        return id() == guestUserId;
    }
}
