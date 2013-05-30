/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blit.yfez.yp.xmlanalyzeapp.exception;

/**
 *
 * @author CBX
 */
public class TooManyRowsException extends RuntimeException{

    public TooManyRowsException() {
        super();
    }

    public TooManyRowsException(String message) {
        super(message);
    }   
}
