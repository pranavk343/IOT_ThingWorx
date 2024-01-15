package com.whirlpool.order.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.CACancelLnRequestDTO;
import com.whirlpool.order.dto.CACancelLnResponseDTO;
import com.whirlpool.order.dto.Line;
import com.whirlpool.order.dto.RespCancelLnItemCA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;

@Service
public class OrderLineCancelCAService {


    Logger logger = LoggerFactory.getLogger(OrderLineCancelCAService.class);


    private boolean enableDynamicSwitch = false;
    private boolean oSGenXML = false;
    private long lastConfig = 0;
    private boolean oCRgaCancelFlag = false;

    @Autowired
    Helper helper;


    private void initConfig(long now) {

        if ((now - 60000L) >= lastConfig) {

            oCRgaCancelFlag = false;
            lastConfig = now;
            enableDynamicSwitch = true;
            oSGenXML = false;
            logger.info("initConfig() reloaded");
        }
    }


    public CACancelLnResponseDTO callCancelFunction(CACancelLnRequestDTO input, String rfcDestinationName) throws IOException {
        CACancelLnResponseDTO rtn = new CACancelLnResponseDTO();
        try {
            logger.info("inside orderLineCancel CA RFC function");
            logger.info("rfcDestinationName = {}" , rfcDestinationName);
            JCoDestination destination = helper.getJCoDestination(rfcDestinationName);
            JCoFunction function = destination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCL_LN");
            function.getImportParameterList().setValue("CALL_TYPE", "C");
            function.getImportParameterList().setValue("PO_NBR", input.po_nbr);
            function.getImportParameterList().setValue("LOC_NBR", input.loc_nbr);
            function.getImportParameterList().setValue("DLVRY_STAT_CD", input.dlvry_stat_cd);
            function.getImportParameterList().setValue("DLVRY_STATS_TS", input.dlvry_stat_ts);
            function.getImportParameterList().setValue("PROD_STAT_CD", input.prod_stat_cd);
            function.getImportParameterList().setValue("PROD_STAT_TS", input.prod_stat_ts);
            function.getImportParameterList().setValue("CAN_ALLOW_FLG", input.can_allow_flg);
            function.getImportParameterList().setValue("EXTNL_REF_NBR", input.extnl_ref_nbr);
            JCoTable itemsIn = function.getTableParameterList().getTable("ITEMS_IN");
            itemsIn.appendRows(input.lines.size());
            logger.info("Number of items in list: {}" , input.lines.size());
            int count = 0;
            for (Line line : input.getLines()) {
                itemsIn.setRow(count);

                itemsIn.setValue("ITM_NUMBER", line.getItem_no());
                itemsIn.setValue("REQ_QTY", line.getQty());
                itemsIn.setValue("MATERIAL", line.getModel());
                itemsIn.setValue("COND_VALUE", line.getCost());
                count++;
            }

            function.execute(destination);
            rtn.po_nbr = function.getExportParameterList().getString("PO_NBR_X");
            rtn.loc_nbr = function.getExportParameterList().getString("LOC_NBR_X");
            rtn.prod_rga_nbr = function.getExportParameterList().getString("RGA_PFX_IND");
            rtn.prod_stat_cd = function.getExportParameterList().getString("PROD_STAT_CD_X");
            rtn.can_ts = function.getExportParameterList().getString("PROD_STAT_TS_X");
            rtn.err_msg_cd = function.getExportParameterList().getString("ERR_MSG_CD");
            rtn.err_msg_desc = function.getExportParameterList().getString("ERR_MSG_DESC");
            JCoTable itemsOut = function.getTableParameterList().getTable("ITEMS_IN");
            rtn.lines = new RespCancelLnItemCA[itemsOut.getNumRows()];
            for (int i = 0; i < itemsOut.getNumRows(); i++) {
                rtn.lines[i] = new RespCancelLnItemCA();
                String itemNo = (String) itemsOut.getValue("ITM_NUMBER");
                rtn.lines[i].Line = itemNo.substring(1, 6);
                String quantity = (String) itemsOut.getValue("REQ_QTY");
                rtn.lines[i].QTY = quantity.replaceFirst("^0*", "");
                rtn.lines[i].Material = (String) itemsOut.getValue("MATERIAL");
                rtn.lines[i].Cost = itemsOut.getValue("COND_VALUE").toString();
                rtn.lines[i].shorttext = (String) itemsOut.getValue("SHORT_TEXT");
                rtn.lines[i].shorttext1 = (String) itemsOut.getValue("SHORT_TEXT"); ////CHG0133705 -- 1/8/2021
                itemsOut.nextRow();
            }

        } catch (Exception e) {
            throw new IOException(e.toString());
        }
        logger.info("exiting orderLineCancel CA RFC function");
        return rtn;

    }

    public void localOrderCancel(CACancelLnRequestDTO input, CACancelLnResponseDTO output) {

        output.setPo_nbr(input.getPo_nbr());
        output.setLoc_nbr(input.getLoc_nbr());
        output.setProd_rga_nbr("");
        output.setProd_stat_cd("");

        output.setCan_ts("");

        int n = 0;
        for (Line lineInput : input.getLines()) {
            for (int i = 0; i < output.lines.length; i++) {
                output.setCancellationNbr(output.getProd_rga_nbr());
                output.lines[i].Line = lineInput.getItem_no();
                output.lines[i].QTY = lineInput.getQty();
                output.lines[i].Material = lineInput.getModel();
                output.lines[i].Cost = lineInput.getCost();
                output.lines[n].shorttext = "";
                output.lines[n].shorttext1 = "";
            }
            n++;
        }

    }


    public CACancelLnResponseDTO orderCancelCA(CACancelLnRequestDTO request) throws IOException {
        logger.info("Entering orderCancelCA method");
        long startTime = System.currentTimeMillis();
        initConfig(startTime);

        //refresh logic
        String poNum = request.getPo_nbr();
        if (poNum != null && !poNum.equals("")) {
            // Now override content input with simulated XML
        }
        CACancelLnResponseDTO output = new CACancelLnResponseDTO();
        logger.error(" inside OCrgaCancel Flag  {}" , oCRgaCancelFlag);
        try {
            //
            // If OrderStatusCheck is in offline mode reply here with system not available
            //
            if (enableDynamicSwitch && oSGenXML) {

                localOrderCancel(request, output);
                output.setErr_msg_cd("100");
                output.setErr_msg_desc("POs Not Found");

                // Continue processing next order cancel
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long strt = System.currentTimeMillis();
        output = callCancelFunction(request, getRfcDestination());
        long dur = System.currentTimeMillis() - strt;
        logger.info(" OrderLnCancelCA RFC Executed in {} ms" , dur);

//
        // If no reply from SAP we have an error
        //
        if (output == null) {

            output = new CACancelLnResponseDTO();
            localOrderCancel(request, output);
            output.err_msg_cd = "200";
            output.err_msg_desc = "System not Available";

        }
        logger.info("Exiting orderCancelCA method");
        return output;


    }


}
