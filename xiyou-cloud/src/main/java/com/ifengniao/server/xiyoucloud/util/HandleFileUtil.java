package com.ifengniao.server.xiyoucloud.util;

import cn.hutool.core.io.BOMInputStream;
import cn.hutool.core.io.CharsetDetector;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.text.csv.CsvUtil;
import com.ifengniao.server.xiyoucloud.dto.export.BeaconExportDTO;
import com.ifengniao.server.xiyoucloud.dto.export.BeaconExportJPDTO;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class HandleFileUtil {

    public static void saveTempFile(String from, String to, String uploadPath) throws Exception {
        saveTempFile(List.of(new FileTransferModel(from, to)), uploadPath);
    }

    public static void saveTempFile(FileTransferModel fileTransferModel, String uploadPath) throws Exception {
        saveTempFile(List.of(fileTransferModel), uploadPath);
    }

    /**
     * 操作文件
     *
     * @param list
     * @return
     */
    public static void saveTempFile(List<FileTransferModel> list, String uploadPath) throws Exception {
        for (FileTransferModel fileTransferModel : list) {
            if (!fileTransferModel.getFrom().startsWith(uploadPath)) {
                fileTransferModel.setFrom(uploadPath + fileTransferModel.getFrom());
            }

            File fromFile = new File(fileTransferModel.getFrom());
            if (!fromFile.exists()) {
                throw new Exception("目标文件不可达");
            }
            if (StringUtils.isNotBlank(fileTransferModel.getTo())) {
                if (!fileTransferModel.getTo().startsWith(uploadPath)) {
                    fileTransferModel.setTo(uploadPath + fileTransferModel.getTo());
                }

                File toFile = new File(fileTransferModel.getTo());
                if (!toFile.getParentFile().exists()) { //判断文件父目录是否存在
                    if (!toFile.getParentFile().mkdirs()) {
                        throw new Exception("文件夹操作失败");
                    }
                }
            }
        }
        for (FileTransferModel fileTransferModel : list) {
            if (StringUtils.isNotBlank(fileTransferModel.getTo())) {
//            File fromFile = new File(fileTransferModel.getFrom());
//            File toFile = new File(fileTransferModel.getTo());
//            fromFile.renameTo(toFile);
                Files.move(Paths.get(fileTransferModel.getFrom()), Paths.get(fileTransferModel.getTo()),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
                );
            } else {
                Files.deleteIfExists(Paths.get(fileTransferModel.getFrom()));
            }
        }
    }

    public static void deleteFileOrDir(String path) throws Exception {
        Path start = Paths.get(path);
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void main(String[] args) throws Exception {
//        List<BeaconExportDTO> testList = new ArrayList<>();
//        testList.add(new BeaconExportDTO("aabbccddeeff", "1", "TYPE1", "请删除此行的示例内容，并填写批注册内容"));
//        writeToCsv("D://example_zh.csv", testList);
//        System.out.println(readFromCsv("D://example_zh.csv", BeaconExportDTO.class));

//        List<BeaconExportJPDTO> testList = new ArrayList<>();
//        testList.add(new BeaconExportJPDTO("aabbccddeeff", "1", "TYPE1", "この行のサンプル内容を削除して、バッチ登録内容を記入してください"));
//        writeToCsv("D://example_jp.csv", testList);
//        System.out.println(readFromCsv("D://example_jp.csv", BeaconExportJPDTO.class));

//        var aaa = readFromCsv("C:\\Users\\wudon\\Downloads\\example_zh.csv", BeaconExportDTO.class);
//        System.out.println(aaa);
//        File file = new File("C:\\Users\\wudon\\Downloads\\example_zh.csv");
//
//        System.out.println("Charset Name: " + CharsetDetector.detect(file));
    }

    public static <T> List<T> readFromCsv(String path, Class<T> clazz) throws Exception {
        var file = new File(path);
        var charset = CharsetDetector.detect(file);
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        try (
                var is = Files.newInputStream(file.toPath());
                var bomis = new BOMInputStream(is, charset.name());
                var reader = IoUtil.getReader(bomis);
                var csv = CsvUtil.getReader();
        ) {
            return csv.read(reader, clazz);
        }
    }

    public static <T> void writeToCsv(String path, List<T> testList) throws Exception {
//        EF BB BF
        FileWriter.create(new File(path), StandardCharsets.UTF_8).write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, 0, 3);
        CsvUtil.getWriter(path, StandardCharsets.UTF_8, true).writeBeans(testList);
    }

    public static class FileTransferModel {

        private String from;
        private String to;

        public FileTransferModel() {
        }

        public FileTransferModel(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }
    }

}
