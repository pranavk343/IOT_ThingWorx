package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class ErrorDetail {

    public int errorCode;
    public String description = "";
}
