package com.sharemate.item;

public class NotOwnerException extends  RuntimeException{
    public NotOwnerException (String message) {
        super(message);
    }
}
