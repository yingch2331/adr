package com.liyuan3210.adr.util;

public class KnownChannelException extends RuntimeException {
	  private static final long serialVersionUID = -2056392320L;
	  private String code;
	  private String message;

	  public KnownChannelException(String code, String message)
	  {
	    this.code = code;
	    this.message = message;
	  }

	  public KnownChannelException(String code, String message, Throwable cause) {
	    super(cause);
	    this.code = code;
	    this.message = message;
	  }

	  public KnownChannelException(Throwable cause) {
	    super(cause);
	  }

	  public String getCode() {
	    return this.code;
	  }

	  public void setCode(String code) {
	    this.code = code;
	  }

	  public String getMessage() {
	    return this.message;
	  }

	  public void setMessage(String message) {
	    this.message = message;
	  }
}
