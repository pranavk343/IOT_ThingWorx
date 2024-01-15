package com.whirlpool.order.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "line")
@JsonFilter("msgdesc")
public class CancelLnItemCAResponse {


    public String item_no = "";
    public String qty = "";
    public String model = "";
    public String cost = "";
    public String cancellation_nbr = "";
    public String prod_stat_cd = "";////CHG0133705 -- 1/8/2021
    public String can_ts = "";
    public String err_msg_cd = "";
    public String err_msg_desc = "";
}
