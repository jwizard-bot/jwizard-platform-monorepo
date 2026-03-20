package xyz.jwizard.jwl.http.annotation;

import xyz.jwizard.jwl.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResponseStatus {
    HttpStatus value();
}
