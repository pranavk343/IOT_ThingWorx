package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class OrderCancelResponseDto {
    private String po_nbr = "";
    private String loc_nbr = "";
    private String prod_rga_nbr = "";
    private String prod_stat_cd = "";
    private String can_ts = "";
    private String err_msg_cd = "";
    private String err_msg_desc = "";
    private String cancellation_nbr = "";
}
