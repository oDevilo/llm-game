/*
 * Copyright 2025-2030 Fluxion Team (https://github.com/Fluxion-io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.devil.llm.avalon.utils.time;

import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brozen
 */
public class Formatters {


    public static final String YMD_HMS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD = "yyyy-MM-dd";
    public static final String HMS = "HH:mm:ss";

    /**
     * 默认使用的时区，与{@link #DEFAULT_ZONE_OFFSET}保持一致。
     */
    public static final ZoneId DEFAULT_ZONE;

    /**
     * 时区偏移量，可通过环境变量“fluxion.time.zone-offset”初始化，默认+8东八区。
     */
    public static final ZoneOffset DEFAULT_ZONE_OFFSET;

    /**
     * 格式化器缓存
     */
    static final Map<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>();
    static {
        String configZoneOffset = System.getProperty("fluxion.time.zone-offset");
        ZoneOffset zoneOffset = TimeUtils.defaultZoneOffset();
        if (StringUtils.isNotBlank(configZoneOffset)) {
            zoneOffset = ZoneOffset.of(configZoneOffset);
        }
        DEFAULT_ZONE = DEFAULT_ZONE_OFFSET = zoneOffset;

        FORMATTERS.put(YMD_HMS, DateTimeFormatter.ofPattern(YMD_HMS).withZone(DEFAULT_ZONE));
        FORMATTERS.put(YMD, DateTimeFormatter.ofPattern(YMD).withZone(DEFAULT_ZONE));
        FORMATTERS.put(HMS, DateTimeFormatter.ofPattern(HMS).withZone(DEFAULT_ZONE));
    }


    /**
     * 获取指定pattern的格式化器
     */
    public static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTERS.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }


    /**
     * 获取"<code>yyyy-MM-dd HH:mm:ss</code>"格式的日期格式化器
     */
    public static DateTimeFormatter ymdhms() {
        return getFormatter(YMD_HMS);
    }

    public static DateTimeFormatter ymdhmss() {
        return getFormatter(YMD_HMS_SSS);
    }


    /**
     * 获取"<code>yyyy-MM-dd</code>"格式的日期格式化器
     */
    public static DateTimeFormatter ymd() {
        return getFormatter(YMD);
    }


    /**
     * 获取"<code>HH:mm:ss</code>"格式的日期格式化器
     */
    public static DateTimeFormatter hms() {
        return getFormatter(HMS);
    }

}
