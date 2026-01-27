package com.jasonmaggard.smart_api.api.llm.exception;

public class LLMException extends RuntimeException {
    
    public LLMException(String message) {
        super(message);
    }
    
    public LLMException(String message, Throwable cause) {
        super(message, cause);
    }
}
