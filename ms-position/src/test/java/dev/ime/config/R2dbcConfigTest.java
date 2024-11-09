package dev.ime.config;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@ExtendWith(MockitoExtension.class)
class R2dbcConfigTest {

	@Mock
    private R2dbcConfigProperties r2dbcConfigProperties;

    @InjectMocks
    private R2dbcConfig r2dbcConfig;

    @Test
    void testR2dbcEntityTemplateCreation() {

    	Mockito.when(r2dbcConfigProperties.getDriver()).thenReturn("postgresql");
    	Mockito.when(r2dbcConfigProperties.getHost()).thenReturn("localhost");
    	Mockito.when(r2dbcConfigProperties.getPort()).thenReturn("5432");
    	Mockito.when(r2dbcConfigProperties.getUsername()).thenReturn("user");
        Mockito.when(r2dbcConfigProperties.getPassword()).thenReturn("password");
        Mockito.when(r2dbcConfigProperties.getDatabase()).thenReturn("testdb");

        R2dbcEntityTemplate r2dbcEntityTemplate = r2dbcConfig.r2dbcEntityTemplate(r2dbcConfigProperties);

        org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(r2dbcEntityTemplate).isNotNull(),
				()-> Assertions.assertThat(r2dbcEntityTemplate.getDatabaseClient()).isNotNull(),
				()-> Assertions.assertThat(r2dbcEntityTemplate.getDatabaseClient().getConnectionFactory()).isNotNull()
				);    
      
    }

}
