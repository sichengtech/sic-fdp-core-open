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
package com.sicheng.common.mapper.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class MapAdapter extends XmlAdapter<MapConvertor, Map<String, Object>> {

    @Override
    public MapConvertor marshal(Map<String, Object> map) throws Exception {
        MapConvertor convertor = new MapConvertor();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);
            convertor.addEntry(e);
        }
        return convertor;
    }

    @Override
    public Map<String, Object> unmarshal(MapConvertor map) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        for (MapConvertor.MapEntry e : map.getEntries()) {
            result.put(e.getKey(), e.getValue());
        }
        return result;
    }

}  

