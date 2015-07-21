package com.biotronisis.pettplant.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.biotronisis.pettplant.persist.AppParams;
import com.biotronisis.pettplant.type.CommunicationType;

public class CommunicationParams  {

	public static final String NAME = "name";
	public static final String COMMUNICATION_TYPE = "communicationType";
   public static final String ADDRESS = "address";
   public static final String NONE = "none";

	private String name;
	private CommunicationType communicationType;
	private String address;

	public CommunicationParams(Context context) {
      SharedPreferences appParams = context.getSharedPreferences(AppParams.PETT_PLANT_DATA_FILE, 0);
      name = appParams.getString(NAME, NONE);
      address = appParams.getString(ADDRESS, NONE);
      communicationType = CommunicationType.getCommType(appParams.getInt(COMMUNICATION_TYPE, CommunicationType.MOCK.getValue()));
   }
	
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
