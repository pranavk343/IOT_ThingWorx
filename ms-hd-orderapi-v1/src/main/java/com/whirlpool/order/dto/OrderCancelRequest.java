package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class OrderCancelRequest {
    private String custBase = "";
    private String custSuf = "";
    private String userId = "";
    private String po_nbr = "";
    private String loc_nbr = "";
    private String dlvry_stat_cd = "";
    private String dlvry_stat_ts = "";
    private String prod_stat_cd = "";
    private String prod_stat_ts = "";
    private String can_allow_flg = "";
    private String extnl_ref_nbr = "";
    private String vndrFlg = "";
    private String wplFlag = "";
}