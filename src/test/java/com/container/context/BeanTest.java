package com.container.context;

import com.container.context.exceptions.BeanCreationException;
import com.container.context.exceptions.DeniedBeanCreationException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class BeanTest {

    private Bean beanInstance;


    @Before
    public void setUp() throws ClassNotFoundException {
        beanInstance = new Bean("String", Class.forName("java.lang.String"));
    }

    private static  Object[] getBeanParameters(){
        return new Object[]{
                new Object[]{true,true, "report1"},
                new Object[]{true,false, "report2"},
                new Object[]{false,true, "report3"},
                new Object[]{false,false, "report4"},
                new Object[]{false,false, ""},
                new Object[]{false,false, null}
        };
    }

    @Test
    @Parameters(method = "getBeanParameters")
    public void testGettersAndSetters(boolean copied, boolean denied, String report){
        beanInstance.setCopied(copied);
        beanInstance.setDenied(denied);
        beanInstance.setReport(report);
        assertEquals(copied, beanInstance.isCopied());
        assertEquals(denied,beanInstance.isDenied());
        if (report == null || report.isEmpty())
            assertTrue(beanInstance.getReport() == null);
        else
            assertEquals(report, beanInstance.getReport());
    }

    @Test(expected = DeniedBeanCreationException.class)
    public void createSnowflakeShouldThrowDeniedBeanCreationException() throws BeanCreationException, DeniedBeanCreationException {
        beanInstance.setDenied(true);
        beanInstance.createSnowflake();
    }

    @Test
    public void createSnowFlakeShouldReturnTheSameInstanceIfBeanIsNotCopied() throws BeanCreationException, DeniedBeanCreationException {
        String s1  = (String) beanInstance.createSnowflake();
        String s2 = (String) beanInstance.createSnowflake();
        assertTrue(s1 == s2);
    }

    @Test
    public void createSnowFlakeShouldReturnDifferentInstances() throws BeanCreationException, DeniedBeanCreationException {
        beanInstance.setCopied(true);
        String s1 = (String) beanInstance.createSnowflake();
        String s2 = (String) beanInstance.createSnowflake();
        assertFalse(s1 == s2);
    }
}
