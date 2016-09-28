/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkState;

import java.time.Instant;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.09.2016
 * 
 */
public class ApiActionCallMetrics {

	private final String callerClassName;
	private final String callerMethod;
	private final Instant beginCall;
	private final Instant endCall;

	/**
	 * 
	 */
	public ApiActionCallMetrics() {
		this.callerClassName = null;
		this.callerMethod = null;
		this.beginCall = null;
		this.endCall = null;
	}

	/**
	 * 
	 * @param callerClassName
	 * @param callerMethod
	 */
	public ApiActionCallMetrics(String callerClassName, String callerMethod) {
		this.callerClassName = callerClassName;
		this.callerMethod = callerMethod;
		this.beginCall = null;
		this.endCall = null;
	}

	/**
	 * 
	 * @param src
	 * @param beginCall
	 */
	private ApiActionCallMetrics(ApiActionCallMetrics src, Instant beginCall, Instant endCall) {
		this.callerClassName = src.callerClassName;
		this.callerMethod = src.callerMethod;
		this.beginCall = beginCall;
		this.endCall = endCall;
	}

	/**
	 * 
	 * @return
	 */
	public static ApiActionCallMetrics newMetrics() {
		return new ApiActionCallMetrics();
	}

	/**
	 * 
	 * @return
	 */
	public ApiActionCallMetrics start() {
		checkState(this.beginCall == null);
		return new ApiActionCallMetrics(this, Instant.now(), null);
	}

	/**
	 * 
	 * @return
	 */
	public ApiActionCallMetrics end() {
		checkState(this.beginCall != null && this.endCall == null);
		return new ApiActionCallMetrics(this, this.beginCall, Instant.now());
	}

	/**
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return this.beginCall == null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return this.beginCall != null && this.endCall != null;
	}

}
