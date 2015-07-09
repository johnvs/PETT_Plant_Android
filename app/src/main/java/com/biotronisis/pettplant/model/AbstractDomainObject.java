package com.biotronisis.pettplant.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public abstract class AbstractDomainObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ID_COLUMN = "id";
	public static final String CREATE_DATE_COLUMN = "createDate";
	public static final String UPDATE_DATE_COLUMN = "updateDate";
	
	@DatabaseField(generatedId = true, columnName = ID_COLUMN)
	private Long id;
	
	@DatabaseField(canBeNull = false, columnName = CREATE_DATE_COLUMN)
	private Long createDate;
	
	@DatabaseField(canBeNull = false, columnName = UPDATE_DATE_COLUMN)
	private Long updateDate;

	public AbstractDomainObject() {
		long now = System.currentTimeMillis();
		this.createDate = now;
		this.updateDate = now;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}
	
}
