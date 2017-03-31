/*
 * StringUtils.java
 * Copyright (c) 2000-2001 Brad D Matlack.
 * License http://www.gnu.org/copyleft
 */
package com.tarena.util;

import java.util.List;
import java.util.StringTokenizer;

/**
 * StringUtils has code borrowed from org.apache.turbine.util.StringUtils
 */
public class StringUtils {

    /**
     * Remove Underscores from a string and replaces first Letters with Capitals. foo_bar becomes FooBar
     */
    public static String removeUnderScores(String data) {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;

        StringTokenizer st = new StringTokenizer(temp, "_");
        while (st.hasMoreTokens()) {
            String element = (String) st.nextElement();
            out.append(capitalize(element));
        }
        return out.toString();
    }

    /**
     * Makes the first letter caps and leaves the rest as is.
     */
    public static String capitalize(String data) {
        StringBuffer sbuf = new StringBuffer(data.length());
        sbuf.append(data.substring(0, 1).toUpperCase()).append(data.substring(1));
        return sbuf.toString();
    }

    /**
     * Capitalizes a letter.
     */
    public static String capitalize(java.lang.String data, int pos) {
        StringBuffer buf = new StringBuffer(data.length());
        buf.append(data.substring(0, pos - 1));
        buf.append(data.substring(pos - 1, pos).toUpperCase());
        buf.append(data.substring(pos, data.length()));
        return buf.toString();
    }

    /**
     * un-capitalizes a letter.
     */
    public static String unCapitalize(String data, int pos) {
        StringBuffer buf = new StringBuffer(data.length());
        buf.append(data.substring(0, pos - 1));
        // System.out.println("1:" + buf.toString());
        buf.append(data.substring(pos - 1, pos).toLowerCase());
        // System.out.println("1:" + buf.toString());
        buf.append(data.substring(pos, data.length()));
        // System.out.println("1:" + buf.toString());
        return buf.toString();
    }

    /**
     * Splits the provided CSV text into a list.
     * 
     * @param text
     *            The CSV list of values to split apart.
     * @param pos
     *            The piece of the array
     * @return The list of values.
     */
    public static String[] split(String text, String separator) {
        StringTokenizer st = new StringTokenizer(text, separator);
        String[] values = new String[st.countTokens()];
        int pos = 0;
        while (st.hasMoreTokens()) {
            values[pos++] = st.nextToken();
        }
        return values;
    }

    /**
     * Joins the elements of the provided array into a single string containing a list of CSV elements.
     * 
     * @param list
     *            The list of values to join together.
     * @param separator
     *            The separator character.
     * @return The CSV text.
     */
    public static String join(String[] list, String separator) {
        StringBuffer csv = new StringBuffer();
        for (int i = 0; i < list.length; i++) {
            if (i > 0) {
                csv.append(separator);
            }
            csv.append(list[i]);
        }
        return csv.toString();
    }

    /**
     * convert the list to String separator by the param:separator
     * 
     * @param list
     * @param separator
     * @return String
     */
    public static String convertList2String(List<String> list, String separator) {
        if (list == null) {
            return "";
        }
        StringBuffer strs = new StringBuffer();
        for (String str : list) {
            strs.append(separator).append(str);
        }
        return strs.substring(1);
    }

    /**
     * 将字符串中tag替换成为指定的info，只替换一次
     * 
     * @param source
     *            ，原来的字符串
     * @param info
     *            ，替换tag的字符串
     * @param tag
     *            ，要被替换掉的tag
     * @return 替换后的内容
     */
    public static StringBuffer replace(String source, String info, String tag) {
        if ((source == null) || (source.length() == 0))
            return new StringBuffer("");

        if ((info == null) || (tag == null) || (tag.length() == 0))
            return new StringBuffer(source);

        int index = source.indexOf(tag);
        if (index < 0)
            return new StringBuffer(source);

        int length = tag.length();
        return (new StringBuffer(source)).replace(index, index + length, info);
    }

    /**
     * 将字符串中所有的tag全部替换成为指定的info
     * 
     * @param source
     *            ，原来的字符串
     * @param info
     *            ，替换tag的字符串
     * @param tag
     *            ，要被替换掉的tag
     * @return 替换后的内容
     */
    public static String replaceAll(String source, String info, String tag) {
        if ((source == null) || (source.length() == 0))
            return "";

        if ((info == null) || (tag == null) || (tag.length() == 0))
            return source;

        int index = source.indexOf(tag);
        boolean valid = (index >= 0);
        if (!valid)
            return source;

        StringBuffer ret = new StringBuffer();
        int start = 0;
        int length = tag.length();
        while (valid) {
            ret.append(source.substring(start, index)).append(info);
            start = index + length;
            index = source.indexOf(tag, start);
            valid = (index >= 0);
        }
        ret.append(source.substring(start));
        return ret.toString();
    }

    /**
     * 将字符串中所有的tag全部替换成为指定的info
     * 
     * @param source
     *            ，原来的字符串
     * @param info
     *            ，替换tag的字符串
     * @param tag
     *            ，要被替换掉的tag
     * @return 替换后的内容
     */
    public static String replaceAll(String source, String info, String startTag, String endTag) {
        if ((source == null) || (source.length() == 0)) {
            return "";
        }
        if ((info == null) || (startTag == null) || (startTag.length() == 0) || (endTag == null)
                || (endTag.length() == 0)) {
            return source;
        }
        int sIndex = source.indexOf(startTag);
        int eIndex = source.indexOf(endTag);
        boolean valid = (sIndex >= 0 && eIndex >= 0);
        if (!valid) {
            return source;
        } else {
            if (sIndex > eIndex) {
                eIndex = source.indexOf(endTag, sIndex);
            }
        }
        int sLength = startTag.length();
        int eLength = endTag.length();
        StringBuffer ret = new StringBuffer();
        int start = 0;
        while (valid) {
            info = source.substring(sIndex + 1, eIndex).trim();
            ret.append(source.substring(start, sIndex + 1)).append(info).append(endTag);
            start = eIndex + 1;
            sIndex = source.indexOf(startTag, start);
            eIndex = source.indexOf(endTag, start);
            valid = (sIndex >= 0 && eIndex >= 0 && eIndex > sIndex);
        }
        ret.append(source.substring(start));
        return ret.toString();
    }

    /**
     * 将输入字符中的SQL保留字进行替换，目前只替换英文半角的单引号' 单引号替换方法：一个单引号换成连续的两个单引号，例如'ABC'D替换成''ABC''D
     * 
     * @param s
     * @return
     */
    public static String getSQLencode(String s) {
        if ((s == null) || (s.length() == 0))
            return "";
        StringBuffer sb = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
            case '\'':
                sb.append("''");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 使用MD5算法进行加密
     * 
     * @param sourcePassword
     *            待加密的明文密码
     * @return 返回加密后的字符串。32位。
     */
    public static String getEncodedPassword(String sourcePassword) {
        String unionPassword = "";
        if (sourcePassword != null)
            unionPassword = new String(sourcePassword);
        MD5 md = new MD5();
        md.Update(unionPassword);
        return md.asHex().toUpperCase();
    }

    /**
     * 模板替换
     * @param source
     * @param replacement
     * @return
     */
	public static String tpl(String source, String... replacement) {
		if (replacement.length == 0) {
			return source;
		}
		for (int i = 0; i < replacement.length; i++) {
			int start = source.indexOf("${");
			if (start < 0) {
				return source;
			}
			int end = source.indexOf("}") + 1;
			String pattern = source.substring(start, end);
			source = source.replace(pattern, replacement[i]);
		}
		return source;
	}
    
    /**
     * 判断字符串是否为空
     * 
     * @param str
     *            输入字符串
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否不为空
     * 
     * @param str
     *            输入字符串 
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static void main(String[] args) {
    	System.out.println(tpl("aaa${1}ccc${2}", "bbb", "ddd"));
	}
}