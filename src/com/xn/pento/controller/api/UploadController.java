package com.xn.pento.controller.api;

import com.xn.pento.common.BaseController;
import com.xn.pento.common.Result;
import com.xn.pento.service.FileStoreService;
import net.csdn.annotation.filter.BeforeFilter;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-21
 * Time: PM11:07
 * To change this template use File | Settings | File Templates.
 */
public class UploadController extends BaseController {
//    @BeforeFilter
//    private final static Map $checkAuth = map(except, list());

    /**
     *
     * 文件上载，通过multiple part方式上载
     *
     * @throws Exception
     */

    @At(path = "/api/upload", types = RestRequest.Method.POST)
    public void upload() throws Exception {
        FileStoreService fileStoreService = findService(FileStoreService.class);
        List<FileItem> fileItems = request.getFileItems();
        Map md5Map = map();
        for (FileItem fileItem : fileItems) {
            if (fileItem.getName().isEmpty()) {
                continue;
            }

            String fileMD5 = fileStoreService.saveFile(fileItem);
            md5Map.put(fileItem.getFieldName(), fileMD5);
        }

        render(Result.ok(md5Map));
    }
}
