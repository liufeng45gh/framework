package com.xn.pento.service;

import com.xn.pento.common.BaseService;
import com.xn.pento.common.WeiboFriendStatusType;
import com.xn.pento.model.User;
import com.xn.pento.model.WeiboFriend;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-29
 * Time: AM11:17
 * To change this template use File | Settings | File Templates.
 */
public class WeiboFriendService extends BaseService {
    public void weiboFriendStatus(User user, JSONArray weiboIds) throws Exception {
        UserService userService = findService(UserService.class);
        Map statusMap = map();
        for (int i = 0; i < weiboIds.size(); i++) {
            JSONObject weiboUser = weiboIds.getJSONObject(i);
            String weiboId = weiboUser.getString("id");

            User existUser = userService.findByWeiboId(weiboId);
            if (user == null) {
                statusMap.put(weiboId, WeiboFriendStatusType.weiboUser);
            }
        }
    }
}
