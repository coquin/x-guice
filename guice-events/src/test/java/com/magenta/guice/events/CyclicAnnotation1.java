package com.magenta.guice.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: dalex
 * Date: 18.06.2009
 * Time: 10:33:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Filter
@CyclicAnnotation2
@interface CyclicAnnotation1 {
}
