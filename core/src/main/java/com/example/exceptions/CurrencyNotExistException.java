package com.example.exceptions;

/**
 * Created by bildovich on 23.10.2016.
 */

public class CurrencyNotExistException extends Exception {
    public CurrencyNotExistException() {
    }

    public CurrencyNotExistException(String s) {
        super(s);
    }

    public CurrencyNotExistException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CurrencyNotExistException(Throwable throwable) {
        super(throwable);
    }

    public CurrencyNotExistException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
