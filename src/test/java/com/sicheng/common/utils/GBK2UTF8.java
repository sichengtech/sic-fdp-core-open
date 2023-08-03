package com.sicheng.common.utils;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.Collection;

/**
 * <p> @Title GBKToUTF8
 * <p> @Description 将GBK编码格式的文件转换为UTF-8编码格式的文件
 *
 * 我们在新拿到一个项目的时候，尤其是老项目，很可能之前的项目编码是GBK，而我们的编译器默认编码为UTF-8，会出现乱码，如果我们一个一个文件的进行编码转换会非常麻烦，所以使用java实现了一个批量将GBK编码转换为UTF-8编码的工具类。
 *
 */
public class GBK2UTF8 {
    public static void main(String[] args) throws Exception {

        // GBK编码格式源码文件路径
        String gbkDirPath = "C:\\dev-java\\IdeaProjects\\RSF\\src\\test\\java";
        // 转为UTF-8编码格式源码文件保存路径
        String utf8DirPath = "C:\\dev-java\\IdeaProjects\\RSF\\src\\test\\java333";

        // 获取所有java文件
        Collection<File> gbkFileList = FileUtils.listFiles(new File(gbkDirPath), new String[]{"java"}, true);
        for (File gbkFile : gbkFileList) {
            // UTF-8编码格式文件保存路径
            String utf8FilePath = utf8DirPath + gbkFile.getAbsolutePath().substring(gbkDirPath.length());
            // 使用GBK编码格式读取文件，然后用UTF-8编码格式写入数据
            FileUtils.writeLines(new File(utf8FilePath), "UTF-8", FileUtils.readLines(gbkFile, "GBK"));
        }
    }
}
