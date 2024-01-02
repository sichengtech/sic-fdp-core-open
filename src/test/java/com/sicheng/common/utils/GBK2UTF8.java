/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
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
