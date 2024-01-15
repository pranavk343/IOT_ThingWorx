package com.whirlpool.order.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;

@Service
public class OrderLineCancelUSService {

    Logger logger = LoggerFactory.getLogger(OrderLineCancelUSService.class);

    @Autowired
    private Helper helper;

    private String queueDir;
    private String backupDir;
    private boolean enableDynamicSwitch;
    private boolean oSGenXML;
    private String rfcDestination;
    private long lastConfig;
    private long maxDuration;

    private static final String MATERIAL = "MATERIAL";

    public void localOrderCancel(final USCancelLnRequestDTO input, final USCancelLnResponseDTO output) {
        output.poNumber = input.getPoNumber();
        output.storeNumber = input.getStoreNumber();
        output.vendorNumber = input.getVendorNumber();
        output.msNumber = input.getMsNumber();
        output.err.description = "";
        output.err.errorCode = 0;
        for (int n = 0; n < input.getLineItems().length; ++n) {
            for (int i = 0; i < output.lineItems.length; ++i) {
                output.lineItems[i].modelNumber = input.getLineItems()[n].modelNumber;
                output.lineItems[i].isCancelled = input.getLineItems()[n].isCancelled;
                input.getLineItems()[n].headerValue = "";
                output.lineItems[i].rgaNumber = "";
            }

        }
    }

    public USCancelLnResponseDTO orderCancelUS(USCancelLnRequestDTO request, String headerValue) throws IOException {
        long startTime = System.currentTimeMillis();
        initConfig(startTime);
        USCancelLnResponseDTO output = new USCancelLnResponseDTO();
        logger.info("item count : {}", request.getLineItems().length);

        if (StringUtils.isEmpty(request.getPoNumber())) {
            output.getErr().setErrorCode(400);
        } else if (isHasEmptyModelNumber(request)) {
            output.getErr().setErrorCode(400);
        }

        updateUSCancelLnItemHeaderValue(request, headerValue);
        try {
            if (this.enableDynamicSwitch && this.oSGenXML) {
                output = new USCancelLnResponseDTO();
                this.localOrderCancel(request, output);
            }
            output = callCancelFunction(request,  getRfcDestination());
        } catch (IOException e) {
            logger.error(" Exception: " + e.toString());
            output = new USCancelLnResponseDTO();
            if (!StringUtils.isEmpty(this.queueDir)) {
                this.localOrderCancel(request, output);
            }
        }
        return output;
    }

    public USCancelLnResponseDTO callCancelFunction(USCancelLnRequestDTO input, String rfcDestinationName) throws IOException {
        logger.info("inside orderlinecancel US RFC function");
        logger.info("rfcDestinationName : {}", rfcDestinationName);
        USCancelLnResponseDTO rtn = new USCancelLnResponseDTO();
        try {
            JCoDestination destination = helper.getJCoDestination(rfcDestinationName);
            JCoFunction function = destination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCL_LN");

            populateImportParameterList(input, function);
            JCoTable itemsIn = function.getTableParameterList().getTable("ITEMS_IN");
            itemsIn.appendRows(input.getLineItems().length);
            logger.info("input items length {}", input.getLineItems().length);

            populateItemsIn(input, itemsIn);
            function.execute(destination);
            rtn.poNumber = function.getExportParameterList().getString("PO_NBR_X");
            rtn.storeNumber = function.getExportParameterList().getString("LOC_NBR_X");
            rtn.vendorNumber = input.getVendorNumber();
            rtn.msNumber = input.getMsNumber();
            String errMsgCd = function.getExportParameterList().getString("ERR_MSG_CD");
            String prodDesc = function.getExportParameterList().getString("PROD_STAT_DESC_X");
            logger.info("errMsgCd response : {}", errMsgCd);
            String errMsgDesc = function.getExportParameterList().getString("ERR_MSG_DESC");
            logger.info("err_msg_desc response : {}", errMsgDesc);
            JCoTable itemsOut = function.getTableParameterList().getTable("ITEMS_IN");

            if (!StringUtils.isEmpty(rtn.poNumber)) {
                checkErrorMsgDescription(rtn, prodDesc, errMsgDesc);
            } else {
                setErrorCodeAndErroDescription(rtn, 4000, "Missing PO");
            }

            rtn.lineItems = new USRespCancelLnItem[itemsOut.getNumRows()];
            for (int i = 0; i < itemsOut.getNumRows(); i++) {
                rtn.lineItems[i] = new USRespCancelLnItem();
                String model = (String) itemsOut.getValue(MATERIAL);
                validateModel(rtn, errMsgDesc, model);
                rtn.lineItems[i].modelNumber = (String) itemsOut.getValue(MATERIAL);
                setLineItemCancellation(rtn, errMsgCd, prodDesc, i);
                rtn.lineItems[i].rgaNumber = (String) itemsOut.getValue("SHORT_TEXT");
                itemsOut.nextRow();
            }

        } catch (Exception e) {
            throw new IOException(e.toString());
        } finally {
            logger.debug(" SapConnectionReleased");
        }
        logger.info("Exiting Orderlinecancel US RFC function");
        return rtn;
    }

    private void initConfig(final long now) {
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

    private static boolean isHasEmptyModelNumber(USCancelLnRequestDTO request) {
        return Arrays.stream(request.getLineItems())
                .map(item -> item.modelNumber)
                .anyMatch(StringUtils::isEmpty);
    }

    private static void updateUSCancelLnItemHeaderValue(USCancelLnRequestDTO request, String headerValue) {
        for (USCancelLnItem item : request.getLineItems()) {
            if (!StringUtils.isEmpty(headerValue)) {
                headerValue = headerValue.trim();
                item.setHeaderValue(headerValue.length() >= 36 ? headerValue.substring(0, 35) : headerValue);
            }
        }
    }

    private static void setLineItemCancellation(USCancelLnResponseDTO rtn, String errMsgCd, String prodDesc, int i) {
        if (errMsgCd.equals("000")) {
            if (!prodDesc.equals("IN TRANSIT")) {
                rtn.lineItems[i].isCancelled =true;
            }
        } else {
            rtn.lineItems[i].isCancelled = false;
        }
    }

    private void validateModel(USCancelLnResponseDTO rtn, String errMsgDesc, String model) {
        if (!StringUtils.isEmpty(model)) {
            if (errMsgDesc.contains("Not Found on Order")) {
                USRespCancelLnItem.err.errorCode = 1202;
                USRespCancelLnItem.err.description = "Material " + model + " Not Found on Order";
            } else {
                rtn.setError("null");
            }
        } else {
            setErrorCodeAndErroDescription(rtn, 4000, "Missing Model");
            logger.error("rtn.error Missing Model {}", model);
        }
    }

    private static void checkErrorMsgDescription(USCancelLnResponseDTO rtn, String prodDesc, String errMsgDesc) {
        if (errMsgDesc.contains("Previously Cancelled")) {
            setErrorCodeAndErroDescription(rtn, 1201, errMsgDesc);
        } else if (errMsgDesc.contains("Not Found on Order")) {
            setErrorCodeAndErroDescription(rtn, 1202, errMsgDesc);
        } else if (errMsgDesc.contains("PO Not Found")) {
            setErrorCodeAndErroDescription(rtn, 1203, errMsgDesc);
        } else if (errMsgDesc.contains("currently being processed")) {
            setErrorCodeAndErroDescription(rtn, 1204, errMsgDesc);
        } else if (errMsgDesc.contains("Matching Quantity not Found")) {
            setErrorCodeAndErroDescription(rtn, 1101, errMsgDesc);
        } else if (prodDesc.equals("IN TRANSIT")) {
            setErrorCodeAndErroDescription(rtn, 2001, "PO In Transit");
        } else {
            rtn.setError("null");
        }
    }

    private static void populateItemsIn(USCancelLnRequestDTO input, JCoTable itemsIn) {
        for (int i = 0; i < input.getLineItems().length; i++) {
            itemsIn.setRow(i);
            itemsIn.setValue("REQ_QTY", input.getLineItems()[i].cancelQuantity);
            itemsIn.setValue(MATERIAL, input.getLineItems()[i].modelNumber);
            itemsIn.setValue("PURCH_NO_S", input.getLineItems()[i].headerValue);
        }
    }

    private static void populateImportParameterList(USCancelLnRequestDTO input, JCoFunction function) {
        function.getImportParameterList().setValue("CALL_TYPE", "C");
        function.getImportParameterList().setValue("PO_NBR", input.getPoNumber());
        function.getImportParameterList().setValue("LOC_NBR", input.getStoreNumber());
        function.getImportParameterList().setValue("DLVRY_STAT_CD", input.getDlvryStatCd());
        function.getImportParameterList().setValue("DLVRY_STATS_TS", input.getDlvryStatTs());
        function.getImportParameterList().setValue("PROD_STAT_CD", input.getProdStatCd());
        function.getImportParameterList().setValue("PROD_STAT_TS", input.getProdStatTs());
        function.getImportParameterList().setValue("CAN_ALLOW_FLG", input.getCanAllowFlg());
        function.getImportParameterList().setValue("EXTNL_REF_NBR", input.getExtnlRefNbr());
    }

    private static void setErrorCodeAndErroDescription(USCancelLnResponseDTO rtn, Integer errorCode, String errMsgDesc) {
        rtn.err.errorCode = errorCode;
        rtn.err.description = errMsgDesc;
        System.out.println("Errorcode --> " + rtn.err.errorCode);
        System.out.println("Error description --> "+ rtn.err.description);
/*
        if(rtn.getLineItems()!=null && rtn.getLineItems().length>0){
            rtn.getLineItems()[0].error=new ErrorDetail();
            rtn.getLineItems()[0].error.errorCode=errorCode;
            rtn.getLineItems()[0].error.description=errMsgDesc;
        }

 */
    }
}
