/*
 * Created on Aug 2, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.whirlpool.order.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

@Data
@JsonFilter("errorFilter")
public class USCancelLnResponseDTO
{
	public String poNumber = "";
	public String storeNumber = "";
	//public String prod_rga_nbr = "";
	//public String prod_stat_cd = "";
	//public String can_ts = "";
	//public String err_msg_cd = "";
	//transient public String err_msg_desc = "";
	public int vendorNumber;
	public String msNumber = "";
	public String error = "null";
	//transient public String prodDesc ="";
	transient public ErrorDetail err = null;
	
public USRespCancelLnItem lineItems[] = null;
//public RespCancelLnItem NewitemResp[]=null; ////12/30

}
