package dev.ime.application.utility;


import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.ShipclassDto;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.model.Shipclass;

@ExtendWith(MockitoExtension.class)
class SortingValidatorTest {

	@Mock
	private ReflectionUtils reflectionUtils;
	
	private SortingValidator sortingValidator;
	
	private final Set<String> fieldsSet = Set.of(
			GlobalConstants.SHIPCLASS_ID,
			GlobalConstants.SHIPCLASS_NAME,
			GlobalConstants.SHIPCLASS_DESC);
	
	@BeforeEach
	private void setUp() {
		
		Mockito.when(reflectionUtils.getFieldNames(Shipclass.class)).thenReturn(fieldsSet);
        sortingValidator = new SortingValidator(reflectionUtils);
		
	}
	
	@Test
	void getDefaultSortField_WithClassShipclass_ReturnIdField() {		

		String idField = sortingValidator.getDefaultSortField(ShipclassDto.class);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(idField).isNotNull(),
				()-> Assertions.assertThat(idField).isNotEmpty(),
				()-> Assertions.assertThat(idField).isEqualTo(GlobalConstants.SHIPCLASS_ID)
				);
		
	}

	@Test
	void isValidSortField_WithValidParameters_ReturnTrue() {		

		boolean result = sortingValidator.isValidSortField(ShipclassDto.class, GlobalConstants.SHIPCLASS_ID);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isTrue()
				);
		
	}

}
