package com.whirlpool.order.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(namespace = "xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \" xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance",
        localName = "hd:VendorCancelResponse")
public class LineCancelDTO {
    public String po_nbr = "";
    public CancelLnItemCAResponse lines[] = null;
    public String loc_nbr = "";
}
