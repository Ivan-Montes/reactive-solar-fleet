package dev.ime.application.utility;


import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.PositionDto;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.Position;

@ExtendWith(MockitoExtension.class)
class SortingValidatorTest {

	@Mock
	private ReflectionUtils reflectionUtils;
	
	private SortingValidator sortingValidator;
	
	private final Set<String> fieldsSet = Set.of(
			GlobalConstants.POSITION_ID,
			GlobalConstants.POSITION_NAME,
			GlobalConstants.POSITION_DESC);
	
	@BeforeEach
	private void setUp() {
		
		Mockito.when(reflectionUtils.getFieldNames(Position.class)).thenReturn(fieldsSet);
        sortingValidator = new SortingValidator(reflectionUtils);
		
	}
	
	@Test
	void getDefaultSortField_WithClassPosition_ReturnIdField() {		

		String idField = sortingValidator.getDefaultSortField(PositionDto.class);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(idField).isNotNull(),
				()-> Assertions.assertThat(idField).isNotEmpty(),
				()-> Assertions.assertThat(idField).isEqualTo(GlobalConstants.POSITION_ID)
				);
		
	}

	@Test
	void isValidSortField_WithValidParameters_ReturnTrue() {		

		boolean result = sortingValidator.isValidSortField(PositionDto.class, GlobalConstants.POSITION_ID);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isTrue()
				);
		
	}

}
