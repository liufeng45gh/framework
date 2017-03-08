package com.xn.pento.service;

import com.xn.pento.cache.AppCache;
import com.xn.pento.cache.CacheProvider;
import com.xn.pento.common.BaseService;
import com.xn.pento.model.Friendship;
import com.xn.pento.model.User;
import net.csdn.common.exception.ArgumentErrorException;
import net.csdn.common.exception.ValidateErrorException;

import java.util.ArrayList;
import java.util.List;

import static net.csdn.modules.http.ApplicationController.list;
import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-18
 * Time: PM1:12
 * To change this template use File | Settings | File Templates.
 */
public class FriendshipService extends BaseService {
    public List<User> interests(final User user, int offset, int count) throws Exception {
        List<Friendship> friendships = user.interests(offset, count);
        List<User> userFriends = list();
        UserService userService = findService(UserService.class);
        for (Friendship friendship : friendships) {
            userFriends.add(userService.findById(friendship.attrAsInt("follow_id")));
        }

        return userFriends;
    }

    public List<User> followers(final User user, int offset, int count) throws Exception {
        List<Friendship> friendships = user.followers(offset, count);

        List<User> userFriends = list();
        UserService userService = findService(UserService.class);
        for (Friendship friendship : friendships) {
            userFriends.add(userService.findById(friendship.attrAsInt("user_id")));
        }

        return userFriends;
    }

    public List<User> friends(final User user, int offset, int count) throws Exception {
        List<Friendship> friendships = user.friends(offset, count);

        List<User> friends = list();
        UserService userService = findService(UserService.class);
        for (Friendship interest : friendships) {
            friends.add(userService.findById(interest.attrAsInt("follow_id")));
        }

        return friends;
    }

    public boolean create(User user, User interestUser) throws Exception {
        if (interestUser == null) {
            throw new ArgumentErrorException("interest user not found");
        }

        Friendship interest = Friendship.where("user_id=:user_id and follow_id=:follow_id", map(
                "user_id", user.id(),
                "follow_id", interestUser.id()
        )).single_fetch();

        if (interest == null) {
            interest = Friendship.create(map(
                    "user_id", user.id(),
                    "follow_id", interestUser.id()
            ));

            User.getJPAContext().jpql(User.class.getName()).update(
                    "set subscribe_count=subscribe_count+1 where id=?",
                    new Object[] {user.id()}
            );
            User.getJPAContext().jpql(User.class.getName()).update(
                    "set follower_count=follower_count+1 where id=?",
                    new Object[] {interestUser.id()}
            );
        }

        Friendship follower = Friendship.where("user_id=:user_id and follow_id=:follow_id", map(
                "user_id", interestUser.id(),
                "follow_id", user.id()
        )).single_fetch();
        if (follower != null) {
            interest.attr("friend", true);
            follower.attr("friend", true);
            if (!follower.save()) {
                throw new ValidateErrorException(follower.validateResults);
            }
        }

        if (!interest.save()) {
            throw new ValidateErrorException(interest.validateResults);
        }

        return true;
    }

    public boolean destroy(User user, User interestUser) throws Exception {
        if (interestUser == null) {
            throw new ArgumentErrorException("interest user not found");
        }

        Friendship interest = Friendship.where("user_id=:user_id and follow_id=:follow_id", map(
                "user_id", user.id(),
                "follow_id", interestUser.id()
        )).single_fetch();

        if (interest == null) {
            // nothing to do, just return success
            return true;
        }

        User.getJPAContext().jpql(User.class.getName()).update(
                "set subscribe_count=subscribe_count-1 where id=?",
                new Object[] {user.id()}
        );
        User.getJPAContext().jpql(User.class.getName()).update(
                "set follower_count=follower_count-1 where id=?",
                new Object[] {interestUser.id()}
        );

        Friendship follower = Friendship.where("user_id=:user_id and follow_id=:follow_id", map(
                "user_id", interestUser.id(),
                "follow_id", user.id()
        )).single_fetch();

        if (follower != null) {
            interest.attr("friend", false);
            follower.attr("friend", false);
            if (!follower.save()) {
                throw new ValidateErrorException(interest.validateResults);
            }
        }

        interest.delete();
        return true;
    }
}
