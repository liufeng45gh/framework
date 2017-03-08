package com.xn.pento.service;

import com.xn.pento.cache.AppCache;
import com.xn.pento.cache.CacheProvider;
import com.xn.pento.common.BaseService;
import com.xn.pento.model.User;
import com.xn.pento.model.UserToken;
import com.xn.pento.util.DateConverter;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.common.exception.ValidateErrorException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-7
 * Time: PM3:46
 * To change this template use File | Settings | File Templates.
 */
public class UserService extends BaseService {
    public User findById(final int userId) throws Exception {
        return (User) AppCache.fetch("user:" + userId, new CacheProvider() {
            @Override
            public Class getDataClass() {
                return User.class;
            }

            @Override
            public Object getData() {
                return User.find(userId);
            }
        });
    }

    public User findByWeiboId(final String weiboId) throws Exception {
        return User.where("weibo_id=:weibo_id", map(
                "weibo_id", weiboId
        )).single_fetch();
    }

    public UserToken findUserToken(final String token) throws Exception {
        return (UserToken)AppCache.fetch("user_token:" + token, new CacheProvider() {
            @Override
            public Class getDataClass() {
                return UserToken.class;
            }

            @Override
            public Object getData() {
                return UserToken.where("token=:token", map(
                        "token", token
                )).single_fetch();
            }
        });
    }

    public User findByToken(final String token) throws Exception {
        UserToken userToken = findUserToken(token);
        if (userToken != null) {
            // update last access time
            userToken.attr("updated_at", DateConverter.now());
            AppCache.update("user_token:" + token, userToken);

            if (userToken.attrAsInt("user_id") == User.guestUserId) {
                User user = new User();
                user.attr("id", User.guestUserId);
                return user;
            }

            return findService(UserService.class).findById(userToken.attrAsInt("user_id"));
        }

        return null;
    }

    public UserToken loginByWeibo(String weiboId, String accessToken, Map<String, String> info) throws Exception {
        User user = findByWeiboId(weiboId);
        if (user == null) {
            user = User.create(map(
                    "weibo_id", weiboId,
                    "status", 0
            ));
            if (!user.save()) {
                throw new ValidateErrorException(user.validateResults);
            }
        }

        String token = RandomStringUtils.randomAlphanumeric(20);
        UserToken userToken = UserToken.create(map(
                "user_id", user.id(),
                "token", token,
                "valid", true,
                "weibo_access_token", accessToken,
                "ip", info.get("ip"),
                "user_agent", info.get("user_agent"),
                "mac", info.get("mac")
        ));
        userToken.save();

        return userToken;
    }

    public UserToken loginByGuest(Map<String, String> info) throws Exception {
        String token = RandomStringUtils.randomAlphanumeric(20);
        UserToken userToken = UserToken.create(map(
                "user_id", User.guestUserId,
                "token", token,
                "valid", true,
                "ip", info.get("ip"),
                "user_agent", info.get("user_agent"),
                "mac", info.get("mac")
        ));
        userToken.save();

        return userToken;
    }

    public User create(Map<String, String> params) throws Exception {
        User user = User.create(params);
        if (!user.save()) {
            throw new ValidateErrorException(user.validateResults);
        }

        return user;
    }

    public void update(int userId, Map<String, String> params) throws Exception {
        final User user = findById(userId);
        if (user == null) {
            throw new RecordNotFoundException("user not found");
        }

        user.updateAttr(params);

        if (!user.update("user:" + user.id())) {
            throw new ValidateErrorException(user.validateResults);
        }
    }

}
