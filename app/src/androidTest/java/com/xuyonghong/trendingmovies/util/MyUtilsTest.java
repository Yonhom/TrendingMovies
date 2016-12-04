package com.xuyonghong.trendingmovies.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by xuyonghong on 2016/12/3.
 */
public class MyUtilsTest {
    @Test
    public void getYearInDateString() throws Exception {
        assertEquals("1972", MyUtils.getYearInDateString("1972-2-2"));
    }

}