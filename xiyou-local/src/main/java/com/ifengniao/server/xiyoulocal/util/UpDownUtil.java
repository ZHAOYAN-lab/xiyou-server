package com.ifengniao.server.xiyoulocal.util;

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;
import com.ifengniao.common.component.http.util.model.HttpFileEntity;
import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.server.xiyoulocal.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.Map;


@Slf4j
public class UpDownUtil {

    public static void simpleDownload(String url, File file) throws Exception {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        HttpUtil.downloadFile(url, file, new StreamProgress() {
            @Override
            public void start() {
                log.info(file.getName() + "文件开始下载");
            }

            @Override
            public void progress(long total, long progressSize) {
                log.info(file.getName() + "文件下载进度，total：{}, progressSize：{}", total, progressSize);
            }

            @Override
            public void finish() {
                log.info(file.getName() + "文件下载完毕");
            }
        });
        if (!file.exists()) {
            //文件不存在，下载失败
            throw new BaseException(file.getName() + "文件下载失败");
        }
    }

    public static String simpleUploadByUrl(String url, ContentType contentType, String fileName, String downloadUrl) throws Exception {
        return simpleUpload(url, contentType, fileName, HttpUtil.downloadBytes(downloadUrl));
    }

    public static String simpleUpload(String url, ContentType contentType, String fileName, byte[] content) throws Exception {
        var uploadResult = com.ifengniao.common.component.http.util.HttpUtil.getIntance().postFile(url,
                Map.of("file", new HttpFileEntity(content, contentType, fileName))
        );
        log.info("云端文件上传接口返回：{}", uploadResult);
        var resultMsg = JsonUtil.fromJson(uploadResult, ResultMsg.class);
        if (resultMsg == null || !Constants.HTTP_SUCCESS.equals(resultMsg.getCode())) {
            throw new BaseException("云端文件上传失败");
        }
        var uploadDetail = (Map<String, String>) resultMsg.getDetail();
        if (uploadDetail == null || StringUtils.isBlank(uploadDetail.get("name"))) {
            throw new BaseException("云端文件上传失败");
        }
        return uploadDetail.get("name");
    }

}
