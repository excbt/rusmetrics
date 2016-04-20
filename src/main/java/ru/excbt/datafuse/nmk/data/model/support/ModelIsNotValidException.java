package ru.excbt.datafuse.nmk.data.model.support;

public class ModelIsNotValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4026599635832837503L;

	/**
	 * 
	 */
	public ModelIsNotValidException() {
		super();
	}

	/**
	 * 
	 * @param arg
	 */
	public ModelIsNotValidException(String arg) {
		super(arg);
	}
}
