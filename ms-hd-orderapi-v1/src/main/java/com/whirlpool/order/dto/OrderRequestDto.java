package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class OrderRequestDto {
    private String custBase = "";
    private String custSuf = "";
    private String userId = "";
    private String gemsNo = "";
    private String custName = "";
    private String poNo = "";
    private String poChange = "N";
    private String shName = "";
    private String shAddr1 = "";
    private String shAddr2 = "";
    private String shCit = "";
    private String shSt = "";
    private String shZip = "";
    private String shPh1 = "";
    private String shPh2 = "";
    private String shPh3 = "";
    private OrderItem items[] = null;
    private String conFirst = "";
    private String conLast = "";
    private String conAddr1 = "";
    private String conAddr2 = "";
    private String conCit = "";
    private String conSt = "";
    private String conZip = "";
    private String conPh1 = "";
    private String conPh2 = "";
    private String conPh3 = "";
    private String rqDte = "";
    private String carrier = "";
    private String vndrFlg = "";
    private String wplFlag = "";
    private String nextdayFlag = "";
    private String extSalesdocNum = "";
    //START : CHG0017537 - Home Depot Box Project
    private String dvid = "";
    private String agntid = "";
    private String msno = "";
    //END : CHG0017537 - Home Depot Box Project
    //START : CHG0017537 - Home Depot Box Project
    private String dvname = "";
    private String sfid = "";
    //END : CHG0017537 - Home Depot Box Project
}
