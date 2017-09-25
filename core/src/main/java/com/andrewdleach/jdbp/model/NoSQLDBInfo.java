package com.andrewdleach.jdbp.model;

public abstract class NoSQLDBInfo extends DBInfo {

	private String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
}
