package com.zp.utils;

import java.util.Random;

/**
 * @Author zp
 * @create 2020/12/11 15:13
 */
public class RandomUtil {
    public static int electRandom() {
        Random random = new Random();
        // 产生一个150-300范围内的随机数
        return random.nextInt(150) + 150;
    }
}
