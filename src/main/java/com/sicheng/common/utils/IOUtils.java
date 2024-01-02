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

import java.io.*;

/**
 * <p>
 * 标题: IOUtils
 * </p>
 * <p>
 * 描述:
 * </p>
 * <p>
 * 公司: 思程科技 www.sicheng.net
 * </p>
 *
 * @author zhaolei
 * @date 2017年9月13日 下午6:05:44
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

    /**
     * 流转数组 从输入流中读取数据到字节数组，供后续业务使用
     *
     * @param inputStream
     * @return
     */
    public static byte[] inputStream2ByteArray(InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //谁读了流，谁负责关，流被读了就不能再被使用了.
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 数组转流 把字节数组中的内容读到输入流，供后续业务使用
     *
     * @param data
     * @return
     */
    public static InputStream byteArray2InputStream(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return inputStream;
    }

    /**
     * 输入流转输出流 从输入流中读取数据，再写入到输出流
     *
     * @param inputStream
     * @param outputStream
     * @return
     */
    public static boolean inputStream2OutStream(InputStream inputStream, OutputStream outputStream) {
        if (inputStream == null) {
            throw new NullPointerException("inputStream is null");
        }
        if (outputStream == null) {
            throw new NullPointerException("outputStream is null");
        }
        try {
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            //谁读了流，谁负责关，流被读了就不能再被使用了
            if (inputStream != null) {
                try {
                    // 关闭输入流
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭输出流
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}