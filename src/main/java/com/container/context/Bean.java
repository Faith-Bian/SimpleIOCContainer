/**
 * <p>
 * The package contains classes that are used
 * to represent the context of W1nter container.
 * </p>
 */
package com.container.context;

import com.container.context.exceptions.BeanCreationException;
import com.container.context.exceptions.DeniedBeanCreationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Bean instances are used to contain information about classes,
 * marked as snowflakes.</p>
 * <p>The are responsible for snowflake instantiation and reporting.</p>
 */
public class Bean {
    private boolean copied;
    private boolean denied;
    private String report;
    private final String snowFlakeName;
    private final Class<?> beanClass;
    private Object beanInstance;


    /**
     * Constructs bean instance with specified snowflake name and class, that was marked with that annotation.
     * @param snowFlakeName name specified in the value of Snowflake annotation <b>@l Snowflake(snowflakeName = "Mindy")</b>.
     *                      Cannot be null.
     * @param beanClass class marked with Snowflake annotation. Cannot be null.
     */
    Bean(String snowFlakeName, Class<?> beanClass) {
        if (snowFlakeName == null)
            throw new NullPointerException("Snowflake name is null!");
        if (beanClass == null)
            throw new NullPointerException("Bean class is null!");
        this.snowFlakeName = snowFlakeName;
        this.beanClass = beanClass;
    }

    /**
     *
     * @return Returns true if class is not a singleton.
     */
    boolean isCopied() {
        return copied;
    }

    /**
     * @param copied Sets the copied value, if true - multiple instances of the object may be created inside container.
     */
    void setCopied(boolean copied) {
        this.copied = copied;
    }

    /**
     * @return Return true if object creation inside container is prohibited.
     */
    boolean isDenied() {
        return denied;
    }

    /**
     * @param denied Sets the denied value. If true - object creation inside container will throw BeanCreationException
     */
    void setDenied(boolean denied) {
        this.denied = denied;
    }

    /**
     *
     * @return Returns a string representing a path to the report file.
     */
    String getReport() {
        return report;
    }

    /**
     * @param report String representing a path to the report file.
     *               Empty stings or null references will not set the report value.
     */
    void setReport(String report) {
        if (report != null && !report.isEmpty())
        this.report = report;
    }

    /**
     * The method is responsible for bean creation inside container. For copied objects after each call
     * a new instance of af a class will be created. If a report path was specified, a report will be generated.
     * @return Returns an instance of an object that was marked with snowflake annotation.
     * @throws DeniedBeanCreationException will be thrown if bean is marked as denied.
     * @throws BeanCreationException will be thrown if new instance of class cannot be created.
     */
    Object createSnowflake() throws DeniedBeanCreationException, BeanCreationException {
        if (this.denied) throw new DeniedBeanCreationException();
        try {
            if (this.copied || beanInstance == null) {
                    beanInstance = beanClass.newInstance();
            }
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException(e);
        }
        if (this.report != null && !this.report.isEmpty())
            reportSnowflake();
        return beanInstance;
    }

    /**
     * Generates a report. And writes it to the specified path.
     */
    private void reportSnowflake(){
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(this.report)));
        out.format("Class:%n  %s%n%n", beanClass.getCanonicalName());
        out.format("Modifiers:%n  %s%n%n",
                Modifier.toString(beanClass.getModifiers()));

        out.format("Type Parameters:%n");
        TypeVariable[] tv = beanClass.getTypeParameters();
        if (tv.length != 0) {
            out.format("  ");
            for (TypeVariable t : tv)
                out.format("%s ", t.getName());
            out.format("%n%n");
        } else {
            out.format("  -- No Type Parameters --%n%n");
        }

        out.format("Implemented Interfaces:%n");
        Type[] intfs = beanClass.getGenericInterfaces();
        if (intfs.length != 0) {
            for (Type intf : intfs)
                out.format("  %s%n", intf.toString());
            out.format("%n");
        } else {
            out.format("  -- No Implemented Interfaces --%n%n");
        }

        out.format("Inheritance Path:%n");
        List<Class> l = new ArrayList<Class>();
        printAncestor(beanClass, l);
        if (l.size() != 0) {
            for (Class<?> cl : l)
                out.format("  %s%n", cl.getCanonicalName());
            out.format("%n");
        } else {
            out.format("  -- No Super Classes --%n%n");
        }

        out.format("Annotations:%n");
        Annotation[] ann = beanClass.getAnnotations();
        if (ann.length != 0) {
            for (Annotation a : ann)
                out.format("  %s%n", a.toString());
            out.format("%n");
        } else {
            out.format("  -- No Annotations --%n%n");
        }
        } catch (IOException e) {
            System.out.println(e);
        }
        finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Recursively prints the parent classes of the class specified as input parameter.
     * @param c Represents the class, which superclasses should be printed.
     * @param l An empty list serves for recursive calls to the method.
     */
    private  void printAncestor(Class<?> c, List<Class> l) {
        Class<?> ancestor = c.getSuperclass();
        if (ancestor != null) {
            l.add(ancestor);
            printAncestor(ancestor, l);
        }
    }
}
