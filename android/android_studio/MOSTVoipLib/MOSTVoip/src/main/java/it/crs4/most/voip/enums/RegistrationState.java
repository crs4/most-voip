/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.enums;

public enum RegistrationState {
    REQUEST_TIMEOUT("REQUEST_TIMEOUT", 408),
    FORBIDDEN("FORBIDDEN", 403),
    NOT_FOUND("NOT_FOUND", 404),
    OK("REGISTERED", 200),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", 503);

    private String stringValue;
    private int intValue;

    private RegistrationState(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    public int intValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
