package com.xiaolanba.passenger.common.utils;


import com.xiaolanba.passenger.common.base.BaseActivity;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配判断工具
 *
 * @author xutingz
 * @time 2015年11月10日 14:47
 */

public class MatcherUtil {
    /**
     * 判断是否是正确的手机格式
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        if (!phoneNumber.startsWith("1") ||
                phoneNumber.length() != 11) {
            return false;
        }

        try {
            String expression = "((^(12|13|14|15|16|17|18|19)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";

            CharSequence inputStr = phoneNumber;
            Pattern pattern = Pattern.compile(expression);

            Matcher matcher = pattern.matcher(inputStr);

            if (matcher.matches()) {
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    /**
     * 短信验证码是否合法
     */
    public static boolean isIdentifyCodeValid(String vertifyCode) {
        return Pattern.compile("[\\d]{6}").matcher(vertifyCode).matches();
    }

    /**
     * 密码是否合法
     */
    public static boolean isPasswordValid(String password) {
//        return Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$").matcher(password).matches();
        return Pattern.compile("[0-9a-zA-Z]{6,18}").matcher(password).matches();
    }

    /**
     * 邀请码是否合法
     */
    public static boolean isInvitedValid(String invited) {
        return Pattern.compile("[0-9a-zA-Z]{6,18}").matcher(invited).matches();
    }

    /**
     * 检查密码是否合法
     */
    public static boolean checkPasswordAndToast(String passwordTrim, BaseActivity activity) {
        boolean b = MatcherUtil.isPasswordValid(passwordTrim);
        if (!b) {
            int passwordLen = passwordTrim.length();
            if (0 == passwordLen || passwordLen < 6 || passwordLen > 20) {
                //密码长度不符合
            } else {
                //密码格式错误
            }
        }
        return b;
    }


    /**
     * 用户名是否合法
     */
    public static boolean isUserNameValid(String usesrname) {
        return Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,20}$").matcher(usesrname).matches();
    }

    /**
     * 连续的多个换行只保留一个换行
     * 防止在评论的时候出现大量的空白换行
     * @param str
     * add by xutingz
     */
    public static String deleteBlank(String str) {
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '\n') {
                if (i - 1 >= 0 && i - 1 < array.length) {
                    if (array[i - 1] == '\n') {
                        array[i - 1] = ' ';
                    }
                }
            }
        }
        String a = new String(array, 0, array.length);
        return a;
    }

    /**
     * 核对输入的字符是否包含敏感内容
     */
    public static boolean checkSensorString(String str) {
        if (str.contains("共产党") ||
                str.contains("我操") ||
                str.contains("妈的") ||
                str.contains("毛泽东") ||
                str.contains("周恩来") ||
                str.contains("邓小平") ||
                str.contains("胡锦涛") ||
                str.contains("习近平") ||
                str.contains("习进平") ||
                str.contains("习晋平") ||
                str.contains("周永康 ") ||
                str.contains("法轮功") ||
                str.contains("打倒社会") ||
                str.contains("阿扁推翻") ||
                str.contains("安门事") ||
                str.contains("八九政治") ||
                str.contains("罢工门") ||
                str.contains("办理证书") ||
                str.contains("办证") ||
                str.contains("报复执法") ||
                str.contains("踩踏事") ||
                str.contains("藏春阁") ||
                str.contains("成人片") ||
                str.contains("成人电") ||
                str.contains("充气娃") ||
                str.contains("催情") ||
                str.contains("打飞机") ||
                str.contains("打砸办公") ||
                str.contains("大鸡巴") ||
                str.contains("大批贪官") ||
                str.contains("大肉棒") ||
                str.contains("代办证") ||
                str.contains("代孕") ||
                str.contains("代写论") ||
                str.contains("党的官") ||
                str.contains("等级证") ||
                str.contains("地震哥") ||
                str.contains("东北独立") ||
                str.contains("东京热") ||
                str.contains("当小姐") ||
                str.contains("进中央") ||
                str.contains("信拦截") ||
                str.contains("对日强硬") ||
                str.contains("儿园惨") ||
                str.contains("儿园凶") ||
                str.contains("儿园杀") ||
                str.contains("二奶大") ||
                str.contains("法轮") ||
                str.contains("性交") ||
                str.contains("肛交") ||
                str.contains("高考黑") ||
                str.contains("各类文凭") ||
                str.contains("跟踪器") ||
                str.contains("攻官小姐") ||
                str.contains("官商勾") ||
                str.contains("打倒社会") ||
                str.contains("传销组") ||
                str.contains("胡紧套") ||
                str.contains("胡耀邦") ||
                str.contains("激情妹") ||
                str.contains("激情炮") ||
                str.contains("挤乳汁") ||
                str.contains("家属被打") ||
                str.contains("奸成瘾") ||
                str.contains("江贼民") ||
                str.contains("疆独") ||
                str.contains("台独") ||
                str.contains("藏独") ||
                str.contains("姐上门") ||
                str.contains("上门服务") ||
                str.contains("考设备") ||
                str.contains("考试包过") ||
                str.contains("理各种证") ||
                str.contains("乱伦") ||
                str.contains("裸聊") ||
                str.contains("媒体封锁") ||
                str.contains("蒙汗药") ||
                str.contains("迷奸") ||
                str.contains("迷昏") ||
                str.contains("嫖妓") ||
                str.contains("嫖鸡") ||
                str.contains("枪决女犯") ||
                str.contains("枪决现场") ||
                str.contains("窃听器") ||
                str.contains("群起抗暴") ||
                str.contains("三级片") ||
                str.contains("色电影") ||
                str.contains("色视频") ||
                str.contains("万能钥匙") ||
                str.contains("席临终") ||
                str.contains("要射了") ||
                str.contains("淫荡") ||
                str.contains("淫乱") ||
                str.contains("专业代写")
                ) {
            return false;
        }
        return true;
    }

    /**
     * 2、0＜评论量＜1000，显示具体的数量，如870；
     * 3、1000≤评论量＜10000，显示单位为k，采用四舍五入，保留小数点后一位；（如：4579评论量，显示为4.6k）
     * 4、评论量≥10000，显示单位为万，采用四舍五入，保留小数点后一位；（如：45790评论量，显示为4.6万）
     * 保留小数点后一位，小数点第二位做四舍五入处理
     * 小于10000返回原数值的字符串
     * 改方法会四舍五入
     */
    public static String getRoundFloatByNum(int num) {
        if (num >= 1000 && num < 10000) {
            float bd = (num) / (float) 1000;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            nf.setRoundingMode(RoundingMode.HALF_EVEN);
            nf.setGroupingUsed(false);//去掉逗号
            String strRounded = nf.format(bd);
            return strRounded + "k";
        } else if (num >= 10000) {
            float bd = (num) / (float) 10000;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            nf.setRoundingMode(RoundingMode.HALF_EVEN);
            nf.setGroupingUsed(false);//去掉逗号
            String strRounded = nf.format(bd);
            return strRounded + "万";
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 四舍五入，转换为万
     *
     * @return
     */
    public static String getRoundWan(int num) {
        if (num >= 10000) {
            float bd = (num) / (float) 10000;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            nf.setRoundingMode(RoundingMode.HALF_EVEN);
            nf.setGroupingUsed(false);//去掉逗号
            String strRounded = nf.format(bd);
            return strRounded + "万";
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 根据传入的数字，如果大于10000,返回以万为单位
     * 保留小数点一位
     * 该方法不会四舍五入(向下取值)
     */
    public static String getMathFloatByNum(int num) {
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
     * 采用进一法,只要末尾大于0就向上取值
     *
     * @param num
     * @return
     */
    public static String ceilNum(long num) {
        if (num >= 10000) {
            long tt = num / 10000;
            long t = num % 10000;
            int t2 = (int) Math.ceil(t / 1000f);
            if (0 != t2) {
                if (t2 == 10) {
                    tt += 1;
                    return String.format("%d万", tt);
                } else {
                    return String.format("%d.%d万", tt, t2);
                }
            } else {
                return String.format("%d万", tt);
            }
        }
        return num + "";
    }

    /**
     * 过滤emoji表情
     *
     * @param str
     * @return
     */
    public static String removeEmojiUnicode(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
        return str;
    }
}
