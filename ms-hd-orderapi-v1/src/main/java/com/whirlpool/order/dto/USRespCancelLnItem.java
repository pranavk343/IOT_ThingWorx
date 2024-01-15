/*
 * Created on Aug 9, 2006
 */
package com.whirlpool.order.dto;

import lombok.Data;

@Data
public class USRespCancelLnItem
{	
	//public String orderQuantity = "";
	//public String cancelQuantity = "";
	
	public String modelNumber = "";
	public boolean isCancelled;
	public String rgaNumber = "";
	//public String error = "null";
	public ErrorDetail error;
	public static ErrorDetail err= new ErrorDetail();
	
	//public CancelLnItem mNumber[] = null;/// 12/29
}
