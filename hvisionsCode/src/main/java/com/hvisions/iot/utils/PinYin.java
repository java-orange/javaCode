package com.hvisions.iot.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYin {
    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    private static class PinyinHolder {
        private static final PinYin instance = new PinYin();
    }

    private PinYin() {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public String toPinYin(String characters) {
        if (characters == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        char[] src = characters.toCharArray();

        for (int i = 0; i < src.length; i++) {
            if (Character.toString(src[i]).matches("[\\u4E00-\\u9FA5]+")) {
                // 如果字符是汉字，进行转换
                String[] py;
                try {
                    py = PinyinHelper.toHanyuPinyinStringArray(src[i], format);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    continue;
                }

                if (py == null) {
                    // just ignore the result if no pinyin found
                } else {
                    // only get the first kind of pinyin
                    result.append(py[0]);
                }
            } else {
                // 非汉字，不转换
                result.append(src[i]);
            }
        }

        return result.toString();
    }

    public static PinYin inst() {
        return PinyinHolder.instance;
    }
}
