package com.whirlpool.order.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.OrderStatusRequest;
import com.whirlpool.order.dto.OrderStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;

@Service
public class OrderStatusCheckService {

    Logger logger = LoggerFactory.getLogger(OrderStatusCheckService.class);
    private long lastConfig = 0;
    private String queueDir = "";
    private String backupDir = "";
    private boolean enableDynamicSwitch = false;
    private boolean oSGenXML = false;
    private String rfcDestination = "";
    private long maxDuration = 0;

    @Autowired
    Helper helper;


    private void initConfig(long now) {
        if ((now - 60000L) >= lastConfig) {
            lastConfig = now;
            queueDir = "/data/xfer/nhp/HomeDepot";
            backupDir = "/data/keep/hdlogs/orderlog";
            enableDynamicSwitch = true;
            oSGenXML = false;
            rfcDestination = "HOMEDEPOT";
            maxDuration = 0;
            logger.info("initConfig() reloaded");
        }
    }

    private void localOrderStatus(OrderStatusRequest input, OrderStatusResponse output) {
        logger.info("System not available");
        output.setMfr_shp_nbr("0");
        output.setPo_nbr(input.getPo_nbr());
        output.setLoc_nbr(input.getLoc_nbr());
        output.setErr_msg_cd("200");
        output.setErr_msg_desc("System not Available");

    }


    public OrderStatusResponse callStatusfunction(OrderStatusRequest input, String rfcDestinationName) throws IOException {
        logger.info("entering order status RFC function");
        logger.info("rfcDestinationName = " + rfcDestinationName);
        OrderStatusResponse rtn = new OrderStatusResponse();
        try {
            JCoDestination destination = helper.getJCoDestination(rfcDestinationName);
            JCoFunction function = destination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCEL");

            JCoParameterList parameterList = function.getImportParameterList();
            parameterList.setValue("CALL_TYPE", "O");
            parameterList.setValue("PO_NBR", input.getPo_nbr());
            parameterList.setValue("LOC_NBR", input.getLoc_nbr());
            parameterList.setValue("DLVRY_STAT_CD", input.getDlvry_stat_cd());
            parameterList.setValue("DLVRY_STATS_TS", input.getDlvry_stat_ts());
            parameterList.setValue("PROD_STAT_CD", input.getProd_stat_cd());
            parameterList.setValue("PROD_STAT_TS", input.getProd_stat_ts());
            parameterList.setValue("CAN_ALLOW_FLG", input.getCan_allow_flg());
            parameterList.setValue("EXTNL_REF_NBR", input.getExtnlRefNbr());
            function.execute(destination);

            rtn.setMfr_shp_nbr(function.getExportParameterList().getString("MFR_SHP_NBR"));
            rtn.setPo_nbr(function.getExportParameterList().getString("PO_NBR_X"));
            rtn.setLoc_nbr(function.getExportParameterList().getString("LOC_NBR_X"));
            rtn.setCan_allow_flg(function.getExportParameterList().getString("CAN_ALLOW_FLG_X"));
            rtn.setRga_pfx_ind(function.getExportParameterList().getString("RGA_PFX_IND"));
            rtn.setProd_stat_cd(function.getExportParameterList().getString("PROD_STAT_CD_X"));
            rtn.setProd_stat_desc(function.getExportParameterList().getString("MFR_SHP_NBR"));
            rtn.setProd_stat_ts(function.getExportParameterList().getString("PROD_STAT_TS_X"));
            rtn.setErr_msg_cd(function.getExportParameterList().getString("ERR_MSG_CD"));
            rtn.setErr_msg_desc(function.getExportParameterList().getString("ERR_MSG_DESC"));
        } catch (Exception e) {
            throw new IOException(e.toString());

        }
        logger.info("Exiting order status RFC function");
        return rtn;
    }

    public OrderStatusResponse checkService(OrderStatusRequest request) throws IOException {
        logger.info("entering checkservice method");
        long startTime = System.currentTimeMillis();
        initConfig(startTime);
        OrderStatusResponse output;

        String refresh = request.getRefresh();
        if (refresh != null && refresh.length() > 0) {
            //refresh logic
        }


        String poNum = request.getPo_nbr();
        if (poNum != null && !poNum.equals("")) {
            // Now override content input with simulated XML
        }
        try {
            //
            // If OrderStatusCheck is in offline mode reply here with system not available
            //
            if (enableDynamicSwitch && oSGenXML) {
                OrderStatusResponse outputoffline = new OrderStatusResponse();
                localOrderStatus(request, outputoffline);

                // Continue processing next order status check
            }
            //
            // Now execute OrderStatusCheck in SAP
            //
            logger.info("Calling OrderStatusCheck RFC");
            long start = System.currentTimeMillis();
            output = callStatusfunction(request, getRfcDestination());
            long duration = System.currentTimeMillis() - start;
            logger.info("OrderStatusCheck RFC Executed in {} ms" , duration);
            if (output == null) {
                logger.info(" BAPI Function returned no output");
                output = new OrderStatusResponse();
                if (queueDir != null && queueDir.length() > 0) {
                    localOrderStatus(request, output);
                } else {
                    output.setErr_msg_cd("200");
                    output.setErr_msg_desc("System not Available");
                }
            }
            logger.info(" Mfr ship number: {}" , output.mfr_shp_nbr);
        } catch (Exception e) {

            logger.error(" Exception : " + e.toString());

            output = new OrderStatusResponse();
            if (queueDir != null && queueDir.length() > 0) {
                localOrderStatus(request, output);
            } else {
                output.err_msg_cd = "200";
                output.err_msg_desc = "System not Available";
            }
        }
        return output;

    }
}
