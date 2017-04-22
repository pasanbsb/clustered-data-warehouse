package com.pasanb.cdw.core.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Pasan on 6/26/2016.
 */
public class Validation {
    private static final Validation INSTANCE = new Validation();

    private ConcurrentHashMap<String, Pattern> validationsMap;
    private Validation() {
        this.validationsMap = new ConcurrentHashMap<>();
    }

    public static Validation getInstance() {
        return INSTANCE;
    }
    public void addValidation(String propertyId, String regex) throws Exception{
        validationsMap.put(propertyId, Pattern.compile(regex));
    }

    public Pattern getValidation(String propertyId) {
        return validationsMap.get(propertyId);
    }
}
