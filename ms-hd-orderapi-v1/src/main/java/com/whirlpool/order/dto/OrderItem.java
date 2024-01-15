package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class OrderItem {
    private String qty = "";
    private String model = "";
    private String custSer = "";
    //START CHG0014786 - Order Source/Expected Price
    private String cost = "";
    //END CHG0014786 - Order Source/Expected Price
}
