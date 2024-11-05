package dev.ime.application.utility;


import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.domain.model.CrewMember;

@ExtendWith(MockitoExtension.class)
class ReflectionUtilsTest {

	@InjectMocks
	private ReflectionUtils reflectionUtils;
	
	@Test
	void getFieldNames_WithDomainClass_ReturnFieldSet() {
		Set<String> fieldNamesSet =  reflectionUtils.getFieldNames(CrewMember.class);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(fieldNamesSet).isNotNull(),
				()-> Assertions.assertThat(fieldNamesSet).hasSize(5)
				);
	}

}
