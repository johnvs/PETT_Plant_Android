package com.biotronisis.pettplant.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.biotronisis.pettplant.type.CommunicationType;

@DatabaseTable(tableName = "communicationParams")
public class CommunicationParams extends AbstractParamsObject {
	private static final long serialVersionUID = 1L;
	
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_COMMUNICATION_TYPE = "communicationType";
	public static final String COLUMN_ADDRESS = "address";
	

	@DatabaseField(columnName=COLUMN_NAME)
	private String name;
	
	@DatabaseField(columnName=COLUMN_COMMUNICATION_TYPE, dataType=DataType.ENUM_STRING)
	private CommunicationType communicationType;
	
	@DatabaseField(columnName=COLUMN_ADDRESS)
	private String address;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommunicationType getCommunicationType() {
		return communicationType;
	}

	public void setCommunicationType(CommunicationType communicationType) {
		this.communicationType = communicationType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

//	@Override
	public String toString() {
		return "CommunicationParams [name=" + name + ", communicationType=" + communicationType + ", address=" + address + "]";
	}
	
}
