package com.container;

import com.container.context.Bean;
import com.container.context.exceptions.BeanCreationException;
import org.junit.Test;
import java.io.IOException;


public class W1nterTest {
    private static final W1nter instance = new W1nter();

    @Test(expected = UnsupportedOperationException.class)
    public void getSetOfPathsShouldReturnUnmodifiableSet() throws IOException, BeanCreationException {
        instance.getSetOfPaths().add("String");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getCreatedBeansShouldReturnUnmodifiableMap() throws ClassNotFoundException {
        instance.getCreatedBeans().put("Bean", new Bean("String", Class.forName("java.lang.String")));
    }
}