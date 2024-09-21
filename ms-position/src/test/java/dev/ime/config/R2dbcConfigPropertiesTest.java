package dev.ime.config;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class R2dbcConfigPropertiesTest {

    @Test
    void testSettersAndGetters() {
    	
        R2dbcConfigProperties props = new R2dbcConfigProperties();
        
        props.setUsername("newuser");
        assertEquals("newuser", props.getUsername());
        
        props.setPassword("newpass");
        assertEquals("newpass", props.getPassword());
        
        props.setDatabase("newdb");
        assertEquals("newdb", props.getDatabase());
        
        props.setHost("newhost");
        assertEquals("newhost", props.getHost());
        
        props.setPort("1234");
        assertEquals("1234", props.getPort());
        
        props.setDriver("mysql");
        assertEquals("mysql", props.getDriver());
    }
}
