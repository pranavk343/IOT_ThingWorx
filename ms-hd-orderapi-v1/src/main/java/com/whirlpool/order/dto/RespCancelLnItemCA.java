package com.whirlpool.order.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "line")
public class RespCancelLnItemCA {


    public String Line = "";
    public String QTY = "";
    public String Material = "";
    public String Cost = "";
    public String shorttext = "";
    public String shorttext1 = "";////CHG0133705 -- 1/8/2021

    transient public com.whirlpool.order.dto.ErrorDetail err= new com.whirlpool.order.dto.ErrorDetail();///12/30/2020
}
