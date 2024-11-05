package dev.ime.application.utility;


import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.CrewMemberDto;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.CrewMember;

@ExtendWith(MockitoExtension.class)
class SortingValidatorTest {

	@Mock
	private ReflectionUtils reflectionUtils;
	
	private SortingValidator sortingValidator;
	
	private final Set<String> fieldsSet = Set.of(
			GlobalConstants.CREWMEMBER_ID,
			GlobalConstants.CREWMEMBER_NAME,
			GlobalConstants.CREWMEMBER_SURNAME,
			GlobalConstants.POSITION_ID,
			GlobalConstants.SPACECRAFT_ID);
	
	@BeforeEach
	private void setUp() {
		
		Mockito.when(reflectionUtils.getFieldNames(CrewMember.class)).thenReturn(fieldsSet);
        sortingValidator = new SortingValidator(reflectionUtils);
		
	}
	
	@Test
	void getDefaultSortField_WithClassCrewMember_ReturnIdField() {		

		String idField = sortingValidator.getDefaultSortField(CrewMemberDto.class);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(idField).isNotNull(),
				()-> Assertions.assertThat(idField).isNotEmpty(),
				()-> Assertions.assertThat(idField).isEqualTo(GlobalConstants.CREWMEMBER_ID)
				);
		
	}

	@Test
	void isValidSortField_WithValidParameters_ReturnTrue() {		

		boolean result = sortingValidator.isValidSortField(CrewMemberDto.class, GlobalConstants.CREWMEMBER_ID);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isTrue()
				);
		
	}

}
