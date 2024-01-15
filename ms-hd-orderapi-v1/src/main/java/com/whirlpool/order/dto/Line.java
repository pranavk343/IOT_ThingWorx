package com.whirlpool.order.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "line")
public class Line {


    public String item_no = "";

    public String qty = "";

    public String cost = "";

    public String model="";

    public String prod_stat_cd="";

    public String can_ts="";

    public String err_msg_cd="";
}
