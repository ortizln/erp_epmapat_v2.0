package com.erp.emails.service;
public class EmailDeliveryException extends RuntimeException {
    public enum Kind { TRANSIENT, PERMANENT, CONFIGURATION, UNCERTAIN }
    private final Kind kind;
    public EmailDeliveryException(Kind kind,String message,Throwable cause) { super(message,cause);this.kind=kind; }
    public Kind kind(){return kind;}
}
