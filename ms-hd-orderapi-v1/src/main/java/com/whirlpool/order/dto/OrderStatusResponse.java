package com.whirlpool.order.dto;

import lombok.Data;


@Data
public class OrderStatusResponse {

    
    public String mfr_shp_nbr = "";

    
    public String po_nbr = "";

    
    public String loc_nbr = "";

    
    public String can_allow_flg = "";

    
    public String rga_pfx_ind = "";

    
    public String prod_stat_cd = "";

    
    public String prod_stat_desc = "";

    
    public String prod_stat_ts = "";

    
    public String err_msg_cd = "";

    
    public String err_msg_desc = "";

}
