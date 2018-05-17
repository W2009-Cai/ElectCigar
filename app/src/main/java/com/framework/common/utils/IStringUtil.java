package com.framework.common.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类
 *
 * @author xutingz
 */
public class IStringUtil {
    /**
     * 手机号
     */
    public final static String PHONE_PATTERN = "^[1][3-8]\\d{9}$";
    public final static String NUMBER_PATTERN = "^[0-9]\\d{10}$";

    /**
     * 是否为空或空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return null == str || str.trim().length() < 1;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return null == str;
    }

    /**
     * 比较两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        return null != str1 && null != str2 && str1.equals(str2);
    }

    /**
     * 忽略大小写比较两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return null != str1 && null != str2 && str1.equalsIgnoreCase(str2);
    }

    /**
     * <br>正则表达式匹配 </br>
     *
     * @param patternStr
     * @param input
     * @return
     */
    public static boolean isMatcherFinded(String patternStr, CharSequence input) {
        if (!isEmpty(patternStr) && null != input) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(input);
            if (null != matcher && matcher.find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 显示字符串
     *
     * @param source
     * @return
     */
    public static String toString(String source) {
        return isEmpty(source) ? "" : source;
    }

    /**
     * 补齐字符串
     *
     * @param source 原字符串
     * @param total  输出字符串总长度
     * @param c      补齐字符
     * @return
     */
    public static String stringFill(String source, int total, char c) {
        StringBuilder result = new StringBuilder(total);
        if (isEmpty(source)) {
            for (int i = 0; i < total; i++) {
                result.append(c);
            }
        } else {
            int len = source.length();
            if (len > total) {
                result.append(source.substring(0, total));
            } else {
                result.append(source);
                for (; len > 0; len--) {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    public static String getDistanceReplace(float lenMeter, boolean isChinese) {

        if (lenMeter == 0) {
            return "0" + (isChinese ? "公里" : "km");
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + (isChinese ? "公里" : "km");
        }

        int dis = (int) (lenMeter / 10 * 10);

        return dis + (isChinese ? "米" : "m");
    }

    public static SpannableStringBuilder getDistanceReplaceSpannable(float lenMeter) {

        SpannableStringBuilder sBuilder = new SpannableStringBuilder();

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            sBuilder.append(dstr + "KM");

            sBuilder.setSpan(new AbsoluteSizeSpan(9, true), sBuilder.length() - 2, sBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return sBuilder;
        }

//		DecimalFormat fnum = new DecimalFormat("##0.0");
//		String dstr = fnum.format(lenMeter);

        int dstr = (int) (lenMeter / 10 * 10);

        sBuilder.append(dstr + "M");
        sBuilder.setSpan(new AbsoluteSizeSpan(9, true), sBuilder.length() - 1, sBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return sBuilder;
    }

    public static boolean isHttpUrl(String source) {
        if (isEmpty(source)) {
            return false;
        }

        if (source.startsWith("http://") || source.startsWith("https://")) {
            return true;
        }

        return false;
    }


    /**
     * <br> 判断首字母是否为字母</br>
     *
     * @param label
     * @return
     */
    public static boolean isFirstCharLetter(String label) {
        if (label == null || "".equals(label)) {
            return false;
        }
        char c = label.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    public static String intToDoubleStr(int num) {
        return num > 9 ? num + "" : "0" + num;
    }

    /**
     * 去掉所有符号, 空格， 数字
     * \p{Lower} 	A lower-case alphabetic character: [a-z]
     * \p{Upper} 	An upper-case alphabetic character:[A-Z]
     * \p{ASCII} 	All ASCII:[\x00-\x7F]
     * \p{Alpha} 	An alphabetic character:[\p{Lower}\p{Upper}]
     * \p{Digit} 	A decimal digit: [0-9]
     * \p{Alnum} 	An alphanumeric character:[\p{Alpha}\p{Digit}]
     * \p{Punct} 	Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     * \p{Graph} 	A visible character: [\p{Alnum}\p{Punct}]
     * \p{Print} 	A printable character: [\p{Graph}\x20]
     * \p{Blank} 	A space or a tab: [ \t]
     * \p{Cntrl} 	A control character: [\x00-\x1F\x7F]
     * \p{XDigit} 	A hexadecimal digit: [0-9a-fA-F]
     * \p{Space} 	A whitespace character: [ \t\n\x0B\f\r]
     *
     * @param str
     * @return
     */
    public static String replaceAllPSD(String str) {
        if (null != str) {
            return str.replaceAll("[\\pP\\p{Punct}\\p{Space}\\p{Digit}]", "");
        }
        return "";
    }

    /**
     * 去掉所有符号
     *
     * @param str
     * @return
     */
    public static String replaceAllSymbol(String str) {
        String temp = "";
        if (null != str) {
            temp = str.replaceAll("[\t\n\\x0B\f\r，。？！……～#：、“”‘’（）－——；@＠＃＊%％·•／＼｜《》〈〉【】『』「」［］\\[\\]｛｝{}〔〕＿＋＝＾＆¥￥$＄£￡€°℃℉,.?!:;…~\\-*/\\|\"'_+−×÷=^&<>←↑→↓()]", "");
        }
        return temp;
    }

    /**
     * 去掉字符串中的空格回车制表符
     *
     * @param str
     * @return
     */
    public static String replaceAllBlank(String str) {
        if (null != str) {
            return str.replaceAll("\\s*|\t|\r|\n", "");
        }
        return "";
    }

    public static String replaceSpaceContent(String content) {
        try {
            // 将多个换行换成一个换行，
            content = content.replaceAll("\r\n{2,}", "\r\n").replaceAll("\n{2,}", "\n").replaceAll("\t{1,}", " ").replaceAll(" {1,}", " ");
            // 去掉前后换行
            int len = "\n".length();
            if (content.startsWith("\n")) {
                content = content.substring(len);
            }

            if (content.endsWith("\n")) {
                content = content.substring(0, content.length() - len);
            }
        } catch (Exception e) {

        }

        return content;
    }


    /**
     * 整数取万
     *
     * @param num
     * @return
     */
    public static String numToMyriad(long num) {
        String str = "";
        if (num > 9999) {
            String temp = String.valueOf(num);
            String start = temp.substring(0, temp.length() - 4);
            String end = temp.substring(temp.length() - 4, temp.length() - 3);
            str = start;
            if (Integer.parseInt(end) > 0) {
                str += "." + end;
            }
            str += "万";
        } else {
            str = (String.valueOf(num));
        }
        return str;
    }

    /**
     * 逗号分隔数字
     *
     * @param num
     * @return
     */
    public static String numToWithSymbol(long num) {
        String str = (String.valueOf(num));
        if (str.length() > 3) {
            StringBuffer ret = new StringBuffer();
            for (int i = str.length() - 1; i >= 0; i--) {
                ret.append(str.charAt(i));
                if ((str.length() - i) % 3 == 0) {
                    ret.append(",");
                }
            }
            if (ret.toString().endsWith(",")) {
                ret = ret.deleteCharAt(ret.length() - 1);
            }
            return ret.reverse().toString();
        }
        return str;
    }

    /**
     * 判断是否为全数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        return matcher.matches();
    }

    /**
     * 判断是否为邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 判断是否是ip地址
     *
     * @param addr
     * @return
     */
    public static boolean isIpAddress(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.matches();
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int lengthContainCN(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 截取含有中文字符串的长度，一个中文算2个字符长度
     *
     * @param value
     * @param len
     * @return
     */
    public static String subStringContainCN(String value, int len) {
        int valueLength = 0;
        String subValue = "";
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }

            if (valueLength - len == 0) {
                return value.substring(0, i + 1);
            } else if (valueLength - len == 1) {
                return value.substring(0, i);
            }
        }
        return subValue;
    }

    /**
     * 格式化2位小数位
     *
     * @param num
     * @return
     */
    public static String formatDoubleBy2Decimal(double num) {
        if (num == 0) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(num);
    }

    public static String formatDoubleBy1Decimal(double num) {
        if (num == 0) {
            return "0.0";
        }
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(num);
    }

    /**
     * 判断字符是否是中文
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String str : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(str);
        }
        return result.toString();
    }

    /**
     * 判断一个字符是不是由同一个字符组成
     *
     * @param str
     * @return
     */
    public static boolean isSameChars(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                char firstChar = str.charAt(0);
                String temp = str.replaceAll(String.valueOf(firstChar), "");
                if (TextUtils.isEmpty(temp)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
	
    /**
     * 转化手机号格式为3 4 4格式
     */
    public static String changePhone(String phone){
        if (phone == null || phone.length() != 11){
            return "";
        }
        StringBuilder sb = new StringBuilder(phone);
        sb.insert(7," ");
        sb.insert(3," ");
        return sb.toString();
    }
	
    /**
     * 将手机号中间数字隐藏
     */
    public static String changePhoneHide(String phone){
        if (phone != null && phone.length() == 11) {
            String preStr = phone.substring(0, 3);
            String endStr = phone.substring(7,11);
            return preStr+"****"+endStr;
        }
        return "";
    }
}
