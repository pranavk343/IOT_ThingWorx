/*
 * Created on Aug 9, 2006
 */
package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class USCancelLnItem
{	
	public String orderQuantity = "";
	public String cancelQuantity = "";
	public String modelNumber = "";
	public boolean isCancelled = false;
	public String headerValue = "";

}
