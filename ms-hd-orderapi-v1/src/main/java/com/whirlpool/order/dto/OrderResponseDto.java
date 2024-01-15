package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class OrderResponseDto {
    private String rCode = "";
    private String reqResults = "";
    private String mygOrderNo = "";
    private String poNo = "";
    private String xDockName = "";
    private String xDockAddr = "";
    private String xDockCity = "";
    private String xDockSt = "";
    private String xDockZip = "";
    private String xDockPhone = "";
}
