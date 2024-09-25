package dev.ime.config;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}

	//Patterns
	public static final String PATTERN_NAME_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";	
	public static final String PATTERN_UUID_ZERO = "00000000-0000-0000-0000-000000000000";
	//Messages
	public static final String MSG_REQUIRED = " *REQUIRED* ";
	public static final String MSG_NODATA = "No data available";
	public static final String MSG_UNKNOWDATA = "Unknow data";
	public static final String MSG_MODLINES = "Modificated lines: ";
	public static final String MSG_PATTERN_SEVERE = "### [** eXception **] -> [%s] ###";
	public static final String MSG_PATTERN_INFO = "### [%s] -> [%s] -> [ %s ]";
	public static final String MSG_COMMAND_ILLEGAL = "Command not supported";
	public static final String MSG_QUERY_ILLEGAL = "Query not supported";
	public static final String MSG_EVENT_ILLEGAL = "Event not supported";
	public static final String MSG_HANDLER_NONE = "No handler found for type";
	public static final String MSG_HANDLER_OK = "CommandHandler processed succesfully";
	public static final String MSG_EVENT_ERROR = "Error processing events";
	public static final String MSG_FLOW_OK = "Reactive flow processed succesfully";
	public static final String MSG_FLOW_ERROR = "Error processing reactive flow";
	public static final String MSG_FLOW_RESULT = "Reactive flow result";
	public static final String MSG_FLOW_PROCESS = "Processing reactive flow";
	public static final String MSG_PUBLISH_EVENT = "Publishing Event";	
	public static final String MSG_PUBLISH_OK = "Publish Event Succesfully";	
	public static final String MSG_PUBLISH_FAIL = "Publish Event Failed";			
	public static final String MSG_PAGED_FAIL = "Invalid page or size parameter";			
	public static final String MSG_REQUEST_FAIL = "Error processing request";	
	//Models
	public static final String POSITION_CAT = "Position";
	public static final String POSITION_DB = "positions";
	public static final String POSITION_ID = "positionId";
	public static final String POSITION_ID_DB = "position_id";
	public static final String POSITION_NAME = "positionName";
	public static final String POSITION_NAME_DB = "position_name";
	public static final String POSITION_DESC = "positionDescription";
	public static final String POSITION_DESC_DB = "position_description";
	public static final String SHIPCLASS_CAT = "Shipclass";
	public static final String SHIPCLASS_DB = "shipclasses";
	public static final String SHIPCLASS_ID = "shipclassId";
	public static final String SHIPCLASS_ID_DB = "shipclass_id";
	public static final String SHIPCLASS_NAME = "shipclassName";
	public static final String SHIPCLASS_NAME_DB = "shipclass_name";
	public static final String SHIPCLASS_DESC = "shipclassDescription";
	public static final String SHIPCLASS_DESC_DB = "shipclass_description";
	public static final String SPACECRAFT_CAT = "Spacecraft";
	public static final String SPACECRAFT_DB = "spacecrafts";
	public static final String SPACECRAFT_ID = "spacecraftId";
	public static final String SPACECRAFT_ID_DB = "spacecraft_id";
	public static final String SPACECRAFT_NAME = "spacecraftName";
	public static final String SPACECRAFT_NAME_DB = "spacecraft_name";
	public static final String CREWMEMBER_CAT = "CrewMember";
	public static final String CREWMEMBER_DB = "crew";
	public static final String CREWMEMBER_ID = "crewMemberId";	
	public static final String CREWMEMBER_ID_DB = "crewMember_id";
	public static final String CREWMEMBER_NAME = "crewMemberName";
	public static final String CREWMEMBER_NAME_DB = "crewMember_name";
	public static final String CREWMEMBER_SURNAME = "crewMemberSurname";
	public static final String CREWMEMBER_SURNAME_DB = "crewMember_surname";
	//Topics
	public static final String POSITION_CREATED = "position.created";
	public static final String POSITION_UPDATED = "position.updated";
	public static final String POSITION_DELETED = "position.deleted";
	public static final String SHIPCLASS_CREATED = "shipclass.created";
	public static final String SHIPCLASS_UPDATED = "shipclass.updated";
	public static final String SHIPCLASS_DELETED = "shipclass.deleted";
	public static final String SPACECRAFT_CREATED = "spacecraft.created";
	public static final String SPACECRAFT_UPDATED = "spacecraft.updated";
	public static final String SPACECRAFT_DELETED = "spacecraft.deleted";
	public static final String CREWMEMBER_CREATED = "crewmember.created";
	public static final String CREWMEMBER_UPDATED = "crewmember.updated";
	public static final String CREWMEMBER_DELETED = "crewmember.deleted";	
	//Exceptions
	public static final String EX_RESOURCENOTFOUND = "ResourceNotFoundException";	
	public static final String EX_RESOURCENOTFOUND_DESC = "Exception is coming, the resource has not been found.";	
	public static final String EX_ENTITYASSOCIATED = "EntityAssociatedException";	
	public static final String EX_ENTITYASSOCIATED_DESC = "Hear me roar, some entity is still associated in the element";	
	public static final String EX_ILLEGALARGUMENT = "IllegalArgumentException";
	public static final String EX_ILLEGALARGUMENT_DESC = "Some argument is not supported";
	public static final String EX_EVENT_UNEXPEC = "Event Unexpected Exception";
	public static final String EX_EVENT_UNEXPEC_DESC = "Event Unexpected Exception";
	public static final String EX_VALIDATION = "ValidationException";
	public static final String EX_VALIDATION_DESC = "Kernel Panic in validation process";
	public static final String EX_UNIQUEVALUE = "UniqueValueException";
	public static final String EX_UNIQUEVALUE_DESC = "Unique Value constraint infringed";
	public static final String EX_INVALIDUUID = "InvalidUUIDException";
	public static final String EX_INVALIDUUID_DESC = "Fail to parse UUID";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_EMPTYRESPONSE = "EmptyResponseException";
	public static final String EX_EMPTYRESPONSE_DESC = "No freak out, just an Empty Response";
	public static final String EX_CREATEJPAENTITY = "CreateJpaEntityException";
	public static final String EX_CREATEJPAENTITY_DESC = "Exception while creation a JPA entity for saving to sql db";
	public static final String EX_CREATEREDISENTITY = "CreateRedisEntityException";
	public static final String EX_CREATEREDISENTITY_DESC = "Exception while creation a REDIS entity for saving to db";
	//Paging and Sorting
	public static final String PS_PAGE = "page";
	public static final String PS_SIZE = "size";
	public static final String PS_BY = "sortBy";
	public static final String PS_DIR = "sortDir";
	public static final String PS_A = "ASC";
	public static final String PS_D = "DESC";	
	
}
