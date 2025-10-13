package com.rainydaysengine.rainydays.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ToJson {

    public static String toJson(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
