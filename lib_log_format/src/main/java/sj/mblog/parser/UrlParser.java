package sj.mblog.parser;

import android.text.TextUtils;
import android.util.Log;

public class UrlParser implements Parser {

    @Override
    public String parse(Object object) {
        if (object == null) {
            return null;
        }

        String url = object.toString();

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        int spitP = url.indexOf("?");
        int spitAnd = url.indexOf("&");
        if (spitP < 0 || spitAnd < 0 || spitAnd < spitP) {
            return null;
        }

        String requestStr = url.substring(spitP + 1);
        String[] request = requestStr.split("&");
        if (request == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(url);
        builder.append("\n");

        int maxLength = 0;
        for (String str : request) {
            //Log.d("->>", "测试:" + str);
            //在这个位置,如果str是null,整个拦截器就出异常,无法正常往解析,next走下去
            if (TextUtils.isEmpty(str)) {
                continue;
            }
            int index = str.indexOf("=");
            if (index == -1) {
                continue;
            }
            String key = str.substring(0, index);
            maxLength = maxLength < key.length() ? key.length() : maxLength;
        }
        for (String str : request) {
            String[] data = str.split("=");
            if (data != null && data.length == 2) {
                int space = maxLength - data[0].length();
                for (int i = 0; i < space; i++) {
                    data[0] += " ";
                }
                builder.append(String.format("\n%s = %s", data[0], data[1]));
            } else {
                builder.append(String.format("\n%s", str));
            }
        }
        return builder.toString();
    }
}
