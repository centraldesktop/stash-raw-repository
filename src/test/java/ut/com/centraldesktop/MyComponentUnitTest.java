package ut.com.centraldesktop;

import org.junit.Test;
import com.centraldesktop.MyPluginComponent;
import com.centraldesktop.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}