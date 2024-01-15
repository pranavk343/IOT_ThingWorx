package com.whirlpool.order.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.*;
import lombok.Data;

@Data
@JacksonXmlRootElement(namespace = "xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorOrderStatusResponse.xsd\" xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance",
        localName = "hd:OrderStatusResponses")
public class OrderStatusResponses {

   @JacksonXmlProperty(localName = "OrderStatusResponse")
   private com.whirlpool.order.dto.OrderStatusResponse orderStatusResponse;
}
