package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class CACancelLnResponseDTO {

    public String po_nbr = "";


    public RespCancelLnItemCA lines[] = null;
    public String loc_nbr = "";
    public String prod_rga_nbr = "";
    public String prod_stat_cd = "";
    public String can_ts = "";
    public String err_msg_cd = "";
    public String err_msg_desc = "";
    private String cancellationNbr = "";////CHG0133705 -- 1/8/2021


}
