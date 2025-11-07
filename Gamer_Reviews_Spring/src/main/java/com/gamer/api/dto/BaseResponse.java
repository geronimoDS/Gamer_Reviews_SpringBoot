package com.gamer.api.dto;

public class BaseResponse {
	private boolean success;
    private boolean error;
    private int code;
    private String message;

	
    public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
    public BaseResponse(boolean success, int code, String message){
        this.success = success;
        this.error = !success;
        this.code = code;
        this.message = message;
    }

}
