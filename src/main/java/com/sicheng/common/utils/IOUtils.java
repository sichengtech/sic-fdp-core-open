/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
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