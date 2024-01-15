/*
 * Created on Aug 2, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.whirlpool.order.dto;


import lombok.Data;

@Data
public class USCancelLnRequestDTO
{
	private String custBase = "";
	private String custSuf = "";
	private String userId = "";
	private String poNumber = "";
	private String storeNumber = "";
	private String dlvryStatCd = "";
	private String dlvryStatTs = "";
	private String prodStatCd = "";
	private String prodStatTs = "";
	private String canAllowFlg = "";
	private String extnlRefNbr = "";
	private String vndrFlg = "";
	private String wplFlag = "";
	private int vendorNumber;
	private String msNumber = "";
	private USCancelLnItem lineItems[] = null;
}
