package com.whirlpool.order.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.OrderCancelRequest;
import com.whirlpool.order.dto.OrderCancelResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;

import java.io.IOException;
import java.util.Objects;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;

@Service
public class OrderCancelService {
    Logger logger = LoggerFactory.getLogger(OrderCancelService.class);

    @Autowired
    private Helper helper;
    private boolean enableDynamicSwitch = true;
    private boolean oSGenXML;


    public OrderCancelResponseDto callCancelFunction(OrderCancelRequest input, String rfcDestinationName) throws IOException {
        logger.info("callCancelFunction");
        OrderCancelResponseDto rtn = new OrderCancelResponseDto();
        try {
            JCoDestination destination = helper.getJCoDestination(rfcDestinationName);
            JCoFunction function = destination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCEL");
            populateImportParameterList(function, "O", input);
            function.execute(destination);
            if (function.getExportParameterList().getString("CAN_ALLOW_FLG_X").equals("Y")) {
                //Can Cancel this order, so let't do it
                populateImportParameterList(function, "C", input);
                function.execute(destination);
                populateOrderCancelResponse(rtn, function);
                setErrorCodeAndDescription(rtn, function.getExportParameterList().getString("ERR_MSG_CD"), function.getExportParameterList().getString("ERR_MSG_DESC"));
            } else {
                //Cannot Cancel the order. Return an error
                populateOrderCancelResponse(rtn, function);
                setErrorCodeAndDescription(rtn, "400", "Invalid Status");
            }
        } catch (Exception e) {
            logger.error(" Exception: " + e.toString());
            throw new IOException(e.toString());
        } finally {
            //LOCATION.debugT(" SapConnectionReleased");
        }
        logger.info(" Exiting callCancelFunction");
        return rtn;
    }


    public String invokeOrderCancel(OrderCancelRequest input) {
        String xmlResponseRequest;
        // call the OrderCancel rfc
        OrderCancelResponseDto output;
        try {
            // If offline mode, return PO Not Found error for a gently response to Kiosk
            if (enableDynamicSwitch && oSGenXML) {
                output = new OrderCancelResponseDto();
                return getXmlResponseException(input, output, "100", "PO Not Found");
            }

            // If online mode, call SAP
            logger.info(" Calling OrderCancel RFC");
            long strt = System.currentTimeMillis();
            output = callCancelFunction(input, getRfcDestination());
            long dur = System.currentTimeMillis() - strt;
            logger.info(" OrderCancel RFC Executed in " + dur + "ms");

            // If no reply from SAP we have an error
            if (Objects.isNull(output)) {
                logger.error(" BAPI Function returned no output");
                output = new OrderCancelResponseDto();
                return getXmlResponseException(input, output, "200", "System not Available");
            }

        } catch (Exception e) {
            // we have an error while executing RFC - return code is 100 or 200
            logger.error(" Exception: " + e.toString());
            output = new OrderCancelResponseDto();
            return getXmlResponseException(input, output, "200", "System not Available");
        }

        // ok check from BAPI and build response based on that.
        xmlResponseRequest = bldResponse(output);
        logger.info("Exiting invokeOrderCancel");
        return xmlResponseRequest;
    }

    /**
     * bldResponseException
     * <p>
     * This method constructs the plain/text response string depending on content of the RFC call response object.
     *
     * @param output
     * @return String
     */
    public String bldResponseException(OrderCancelResponseDto output) {
        StringBuilder sb = createResponseHeader();
        sb.append("<po_nbr>");
        sb.append(Encode.forXml(output.getPo_nbr()));
        sb.append("</po_nbr>");
        sb.append("<loc_nbr>");
        sb.append(Encode.forXml(output.getLoc_nbr()));
        sb.append("</loc_nbr>");
        sb.append("<err_msg_cd>");
        sb.append(Encode.forXml(output.getErr_msg_cd()));
        sb.append("</err_msg_cd>");
        sb.append("<err_msg_desc>");
        sb.append(Encode.forXml(output.getErr_msg_desc()));
        sb.append("</err_msg_desc>");
        sb = createResponseFooter(sb);
        return sb.toString();
    }

    public StringBuilder createResponseHeader() {
        StringBuilder xmlResponseHeader = new StringBuilder();
        xmlResponseHeader.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlResponseHeader.append("<hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\"");
        xmlResponseHeader.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        xmlResponseHeader.append(" xsi:schemaLocation=\"http://www.homedepot.com/HD");
        xmlResponseHeader.append(Encode.forXmlAttribute("../Schema/VendorCancelResponse.xsd"));
        xmlResponseHeader.append(" \">");
        return xmlResponseHeader;
    }


    public StringBuilder createResponseFooter(StringBuilder sb) {
        sb.append("</hd:VendorCancelResponse>");
        return sb;
    }

    /**
     * bldResponse
     * <p>
     * This method constructs the plain/text response string depending on content of the RFC call response object.
     *
     * @param res OrderResponse
     * @return String
     */
    public String bldResponse(OrderCancelResponseDto res) {
        StringBuilder sb = createResponseHeader();
        sb.append("<po_nbr>");
        sb.append(Encode.forXml(res.getPo_nbr()));
        sb.append("</po_nbr>");
        sb.append("<loc_nbr>");
        sb.append(Encode.forXml(res.getLoc_nbr()));
        sb.append("</loc_nbr>");
        sb.append("<prod_rga_nbr>");
        sb.append(Encode.forXml(res.getProd_rga_nbr()));
        sb.append("</prod_rga_nbr>");
        sb.append("<prod_stat_cd>");
        sb.append(Encode.forXml(res.getProd_stat_cd()));
        sb.append("</prod_stat_cd>");
        sb.append("<can_ts>");
        sb.append(Encode.forXml(checkTimeStamp(res.getCan_ts())));
        sb.append("</can_ts>");
        if (res.getErr_msg_cd() != null && res.getErr_msg_cd().length() > 0) {
            sb.append("<err_msg_cd>");
            sb.append(Encode.forXml(res.getErr_msg_cd()));
            sb.append("</err_msg_cd>");
        }
        if (res.getErr_msg_desc() != null && res.getErr_msg_desc().length() > 0) {
            sb.append("<err_msg_desc>");
            sb.append(Encode.forXml(res.getErr_msg_desc()));
            sb.append("</err_msg_desc>");
        }
        sb = createResponseFooter(sb);
        return sb.toString();
    }


    public static String checkTimeStamp(String ts)
        /*
         * Check to make sure the timestamp returned by the RFC is in the format YYYY-MM-DDThh:mm:ss
         */ {
        if (ts.length() < 19 && ts.indexOf('-') == -1 && ts.indexOf(':') == -1 && ts.length() > 0 && ts != null) {
            String year = ts.substring(0, 4);
            String month = ts.substring(4, 6);
            String day = ts.substring(6, 8);
            String hour = ts.substring(9, 11);
            String minute = ts.substring(11, 13);
            String second = ts.substring(13, 15);
            String newtimestamp = year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
            return newtimestamp;
        } else
            return ts;
    }

    private static void populateImportParameterList(JCoFunction function, String callType, OrderCancelRequest input) {
        function.getImportParameterList().setValue("CALL_TYPE", callType);
        function.getImportParameterList().setValue("PO_NBR", input.getPo_nbr());
        function.getImportParameterList().setValue("LOC_NBR", input.getLoc_nbr());
        function.getImportParameterList().setValue("DLVRY_STAT_CD", input.getDlvry_stat_cd());
        function.getImportParameterList().setValue("DLVRY_STATS_TS", input.getDlvry_stat_ts());
        function.getImportParameterList().setValue("PROD_STAT_CD", input.getProd_stat_cd());
        function.getImportParameterList().setValue("PROD_STAT_TS", input.getProd_stat_ts());
        function.getImportParameterList().setValue("CAN_ALLOW_FLG", input.getCan_allow_flg());
        function.getImportParameterList().setValue("EXTNL_REF_NBR", input.getExtnl_ref_nbr());
    }

    private static void populateOrderCancelResponse(OrderCancelResponseDto rtn, JCoFunction function) {
        rtn.setPo_nbr(function.getExportParameterList().getString("PO_NBR_X"));
        rtn.setLoc_nbr(function.getExportParameterList().getString("LOC_NBR_X"));
        // check with Frank for Return RGA number
        rtn.setProd_rga_nbr(function.getExportParameterList().getString("RGA_PFX_IND"));
        rtn.setProd_stat_cd(function.getExportParameterList().getString("PROD_STAT_CD_X"));
        rtn.setCan_ts(function.getExportParameterList().getString("PROD_STAT_TS_X"));
    }

    private String getXmlResponseException(OrderCancelRequest input, OrderCancelResponseDto output, String errorCode, String errorDesc) {
        localOrderCancel(input, output);
        setErrorCodeAndDescription(output, errorCode, errorDesc);
        String xmlResponseException = bldResponseException(output);
        logger.info("Exiting invokeOrderCancel");
        return xmlResponseException;
    }

    private static void setErrorCodeAndDescription(OrderCancelResponseDto output, String errorCode, String description) {
        output.setErr_msg_cd(errorCode);
        output.setErr_msg_desc(description);
    }

    private void localOrderCancel(OrderCancelRequest input, OrderCancelResponseDto output) {
        logger.debug(" Processing Order Cancel Locally");
        output.setPo_nbr(input.getPo_nbr());
        output.setLoc_nbr(input.getLoc_nbr());
        output.setProd_rga_nbr("");
        output.setProd_stat_cd("");
        output.setCan_ts("");
    }
}
