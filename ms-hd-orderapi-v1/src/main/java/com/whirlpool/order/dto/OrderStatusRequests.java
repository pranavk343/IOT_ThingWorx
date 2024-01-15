package com.whirlpool.order.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "OrderStatusRequests")
public class OrderStatusRequests {

   public OrderStatusRequest OrderStatusRequest;
}
