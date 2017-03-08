package com.xn.pento.service;

import com.xn.pento.cache.AppCache;
import com.xn.pento.cache.CacheProvider;
import com.xn.pento.common.BaseService;
import com.xn.pento.common.VisibleType;
import com.xn.pento.model.*;
import net.csdn.common.exception.RecordNotFoundException;
import net.csdn.common.exception.ValidateErrorException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Map;

import static net.csdn.modules.http.ApplicationController.map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: AM10:42
 * To change this template use File | Settings | File Templates.
 */
public class PinService extends BaseService {
    public Pin findById(final int pinId) throws Exception {
        return (Pin)AppCache.fetch("pin:" + pinId, new CacheProvider() {
            @Override
            public Class getDataClass() {
                return Pin.class;
            }

            @Override
            public Object getData() {
                return Pin.find(pinId);
            }
        });
    }

    public Map imageMap(String imageId) throws Exception {
        if (imageId == null) {
            return map();
        }

        File imageFile = findService(FileStoreService.class).getFilePath(imageId);
        BufferedImage image = ImageIO.read(imageFile);
        int width = image.getWidth();
        int height = image.getHeight();

        height = (int)(height / (width * 1.0 / 200));
        width = 200;

        File scaledImageFile = new File(imageFile.getAbsolutePath() + "_t200");
        if (scaledImageFile.exists()) {
            return map(
                    "image", imageId,
                    "image_height", height
            );
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH),0,0,null);
        ImageIO.write(img, "jpg", scaledImageFile);

        return map(
                "image", imageId,
                "image_height", height
        );
    }

    public Pin create(User user, String text, String content, int boardId, String imageId) throws Exception {
        Board board = Board.find(boardId);
        if (board == null || board.attrAsInt("user_id") != user.id()) {
            throw new RecordNotFoundException("board not found");
        }

        PinContent pinContent = PinContent.create(map(
                "content", content
        ));
        if (!pinContent.save()) {
            throw new ValidateErrorException(pinContent.validateResults);
        }

        Pin pin = Pin.create(map(
                imageMap(imageId),
                "content_id", pinContent.id(),
                "text", text,
                "board_id", board.id(),
                "user_id", user.id(),
                "visible", VisibleType.visible
        ));

        if (!pin.save()) {
            throw new ValidateErrorException(pin.validateResults);
        }

        // update updated_at to make board on the top
        Board.getJPAContext().jpql(Board.class.getName()).update(
                "set pin_count=pin_count+1, updated_at=now() where id=?",
                new Object[] { board.id() }
        );

        // update user pin_count
        User.getJPAContext().jpql(User.class.getName()).update(
                "set pin_count=pin_count+1, updated_at=now() where id=?",
                new Object[] { user.id() }
        );

        // send to followers
        findService(TimelineService.class).distributePin(user, pin, board);

        return pin;
    }

    public Pin repin(User user, int pinId, String text, int boardId) throws Exception {
        Board board = Board.find(boardId);
        if (board == null || board.attrAsInt("user_id") != user.id()) {
            throw new RecordNotFoundException("board not found");
        }

        Pin replyPin = findById(pinId);
        if (replyPin == null) {
            throw new RecordNotFoundException("pin not found");
        }

        replyPin.attr("repin_count", replyPin.attrAsInt("repin_count") + 1);
        if (!replyPin.update()) {
            throw new ValidateErrorException(replyPin.validateResults);
        }

        Pin pin = null;
        if (replyPin.attr("pin_id", Integer.class) != null) {
            pin = findById(replyPin.attrAsInt("pin_id"));
            pin.attr("repin_count", pin.attrAsInt("repin_count") + 1);
            if (!pin.update()) {
                throw new ValidateErrorException(pin.validateResults);
            }
        }

        Pin repostPin = Pin.create(map(
                "text", text,
                "user_id", user.id(),
                "board_id", board.id(),
                "reply_pin_id", pin != null ? null : replyPin.id(),
                "pin_id", pin != null ? pin.id() : null,
                "visible", VisibleType.visible
        ));

        if (!repostPin.save()) {
            throw new ValidateErrorException(repostPin.validateResults);
        }

        // update updated_at to make board on the top
        Board.getJPAContext().jpql(Board.class.getName()).update(
                "set pin_count=pin_count+1, updated_at=now() where id=?",
                new Object[] { board.id() }
        );

        // update user pin_count
        User.getJPAContext().jpql(User.class.getName()).update(
                "set pin_count=pin_count+1, updated_at=now() where id=?",
                new Object[] { user.id() }
        );

        // distribute to followers
        findService(TimelineService.class).distributePin(user, repostPin, board);

        return repostPin;
    }

    public boolean destroy(User user, int pinId) throws Exception {
        Pin pin = findById(pinId);
        if (pin == null || pin.attrAsInt("user_id") != user.id()) {
            throw new RecordNotFoundException("pin not found");
        }

        Board.getJPAContext().jpql(Board.class.getName()).update(
                "set pin_count=pin_count-1 where id=?",
                new Object[] { pin.attrAsInt("board_id") }
        );

        // update user pin_count
        User.getJPAContext().jpql(User.class.getName()).update(
                "set pin_count=pin_count-1, updated_at=now() where id=?",
                new Object[] { user.id() }
        );

        pin.userRemove();
        return pin.save();
    }

    public String findContentById(final int contentId) throws Exception {
        return (String)AppCache.fetch("pin_content:" + contentId, new CacheProvider() {
            @Override
            public Class getDataClass() {
                return PinContent.class;
            }

            @Override
            public Object getData() {
                PinContent content = PinContent.find(contentId);
                if (content != null) {
                    return content.attr("content");
                }

                return null;
            }
        });
    }
}
