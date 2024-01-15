package com.whirlpool.order.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class CACancelLnRequestDTO {
    public String custBase = "";
    public String custSuf = "";
    public String userId = "";
    public String po_nbr = "";
    public String loc_nbr = "";
    public String dlvry_stat_cd = "";
    public String dlvry_stat_ts = "";
    public String prod_stat_cd = "";
    public String prod_stat_ts = "";
    public String can_allow_flg = "";
    public String extnl_ref_nbr = "";
    public String vndrFlg = "";
    public String wplFlag = "";
    public List<Line> lines = new ArrayList<>();
    // public Line[] items;
}
