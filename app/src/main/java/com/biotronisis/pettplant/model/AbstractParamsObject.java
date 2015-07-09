package com.biotronisis.pettplant.model;

import com.j256.ormlite.field.DatabaseField;

public abstract class AbstractParamsObject extends AbstractDomainObject {
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_USE_COLUMN = "defaultUse";
	
	@DatabaseField(columnName=DEFAULT_USE_COLUMN)
	private Boolean defaultUse;

	public Boolean isDefaultUse() {
		return defaultUse;
	}

	public void setDefaultUse(Boolean defaultUse) {
		this.defaultUse = defaultUse;
	}
}
