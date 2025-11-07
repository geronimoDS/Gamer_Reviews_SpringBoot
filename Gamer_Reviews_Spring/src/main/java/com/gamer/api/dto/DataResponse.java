package com.gamer.api.dto;

public class DataResponse<T> extends BaseResponse {
    private T data;

    public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public DataResponse(boolean success, int code, String message, T data){
        super(success, code, message);
        this.data = data;
    }

   
}