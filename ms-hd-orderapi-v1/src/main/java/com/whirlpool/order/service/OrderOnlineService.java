package com.whirlpool.order.service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.OrderItem;
import com.whirlpool.order.dto.OrderRequestDto;
import com.whirlpool.order.dto.OrderResponseDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;

@Component
public class OrderOnlineService {

    Logger logger = LoggerFactory.getLogger(OrderOnlineService.class);

    @Autowired
    private Helper helper;

    private JCoDestination destination;
    private JCoFunction function;
    private static boolean enableDynamicSwitch;
    private static boolean oEGenXML;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
    private static final String WPLFLAG = "WPLFLAG";
    private static final String NEXTDAYFLAG = "NEXTDAYFLAG";
    private static final String AGNTID = "AGNTID";
    private static final String CACUSTBASE = "28216";
    private static final String USCUSTBASE = "27302";

    public OrderResponseDto callOrderFunction(OrderRequestDto input, String rfcDestinationName) throws IOException {
        logger.info("calling order RFC function");
        OrderResponseDto rtn = new OrderResponseDto();

        String PoChange = input.getPoChange();
        if (PoChange.equalsIgnoreCase("Y")) {
            processOrderRequest(input, rfcDestinationName, rtn, "Z_NI9_SALESORDER_CHANGE_CCSC2");
        }
////////CHG0152038---------------------------------------------->
        else {
            logger.info(" Exception from OFC 35: ");
            processOrderRequest(input, rfcDestinationName, rtn, "Z_NI9_SALESORDER_CREATE_CCSC2");
        }
////////CHG0152038---------------------------------------------->
        return rtn;
    }

    public String bldOrderResponse(OrderResponseDto res) {
        StringBuilder sb = new StringBuilder();
        sb.append("R-Code=");
        sb.append(res.getRCode());
        sb.append("&ReqResults=");
        sb.append(res.getReqResults());
        sb.append("&MYGOrderNo=");
        sb.append(res.getMygOrderNo());
        sb.append("&PoNo=");
        sb.append(res.getPoNo());
        sb.append("&XDockName=");
        sb.append(res.getXDockName());
        sb.append("&XDockAddr=");
        sb.append(res.getXDockAddr());
        sb.append("&XDockCity=");
        sb.append(res.getXDockCity());
        sb.append("&XdockSt=");
        sb.append(res.getXDockSt());
        sb.append("&XdockZip=");
        sb.append(res.getXDockZip());
        sb.append("&XDockPhone=");
        sb.append(res.getXDockPhone());
        return sb.toString();
    }

    public String orderEntryService(String[] custSer, Map<String, String> searchParams) {

        logger.info("Inside orderEntry Method");
        OrderRequestDto input = new OrderRequestDto();
        populateOrderRequest(input, searchParams);

        // START : CHG0031001 - Canada Box Truck
        String dvname = nullCheck(searchParams.get("DVNAME"));
        String sfid = nullCheck(searchParams.get("SFID"));

        if (dvname.length() > 0) {
            //START : CHG0031001 - Canada Box Truck
            input.setDvid("00");
            input.setMsno(dvname);
            if (sfid.length() > 0) {
                input.setAgntid(sfid.substring(1));
            }
            //END : CHG0031001 - Canada Box Truck
        } else {
            //START : CHG0017537 - Home Depot Box Project
            input.setDvid(nullCheck(searchParams.get("DVID")));
            input.setAgntid(nullCheck(searchParams.get(AGNTID)));
            input.setMsno(nullCheck(searchParams.get("MSNO")));
            //END : CHG0017537 - Home Depot Box Project
        }
        //END : CHG0031001 - Canada Box Truck
        List<OrderItem> orderItems = getOrderItems(searchParams);
        input.setItems(new OrderItem[orderItems.size()]);
        orderItems.toArray(input.getItems());
        if (Objects.nonNull(custSer)) {
            for (int j = 0; j < input.getItems().length; j++) {
                if (j >= custSer.length) {
                    break;
                }
                input.getItems()[j].setCustSer(custSer[j]);
            }
        }

        // START CHG0014786 - Order Source/Expected Price
        String orderSrc = nullCheck(searchParams.get("ORDERSRC")).toUpperCase();
        String outCustBase = "HD"; // Default result to HD
        String inCustBase = input.getCustBase().trim();
        outCustBase = updateOutCustBase(orderSrc, outCustBase, inCustBase);
        input.setCustBase(outCustBase);
        // END CHG0014786 - Order Source/Expected Price

        OrderResponseDto output;
        try {
            // OrderEntry is in offline mode. Save a local order
            if ((enableDynamicSwitch) && oEGenXML) {
                //TODO call offline service for order create
            }
            // OrderEntry is in ONLINE mode. Create an order in SAP
            logger.info(" Calling OrderEntry RFC");
            long strt = System.currentTimeMillis();
            output = callOrderFunction(input, getRfcDestination());
            long dur = System.currentTimeMillis() - strt;
            logger.info(" OrderEntry RFC Executed in {} ms", dur);

            if (Objects.isNull(output) || Objects.isNull(output.getRCode()) || (output.getRCode().trim().length() == 0)) {
                logger.error(" BAPI Function returned no output");
                output = new OrderResponseDto();
                //TODO call offline service for order create
                output.setRCode("5");
                output.setReqResults("System not Available");
            }

        } catch (IOException e) {
            // If we got an exception then save the order locally if possible
            logger.error(" Exception Order Entry: {}", e.toString());
            output = new OrderResponseDto();
            //TODO call offline service for order create
            output.setRCode("5");
            output.setReqResults("System not Available");
        }
        return bldOrderResponse(output);
    }

    @NotNull
    private List<OrderItem> getOrderItems(Map<String, String> searchParams) {
        List<OrderItem> orderItems = new ArrayList<>();
        int i = 1;
        while (true) {
            String suf = dig2(i);
            String s = searchParams.get("QTY" + suf);
            if ((s == null) || (s.trim().length() == 0)) {
                break;
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setQty(s);
            orderItem.setModel(searchParams.get("MODEL" + suf));
            // START CHG0014786 - Order Source/Expected Price
            orderItem.setCost(searchParams.get("COST" + suf));
            // END CHG0014786 - Order Source/Expected Price
            orderItems.add(orderItem);
            i++;
        }
        return orderItems;
    }

    private static String updateOutCustBase(String orderSrc, String outCustBase, String inCustBase) {
        if (inCustBase.equals("10720")) {
            // No matter on ORDERSRC, always return E for 10720
            outCustBase = "E";
        } else if (orderSrc.equals("O")) {
            // ORDERSRC = O, check if we need pass HDO
            if (inCustBase.equals(USCUSTBASE) || inCustBase.equals(CACUSTBASE)) {
                outCustBase = "HDO";
            }
        } else if (orderSrc.equals("S") && (inCustBase.equals(USCUSTBASE) || inCustBase.equals(CACUSTBASE))) {
            // ORDERSRC = S, check if we need pass HDS
            outCustBase = "HDS";
        }
        return outCustBase;
    }

    private String dig2(int n) {
        String ret = Integer.toString(n);
        if (ret.length() == 1) {
            ret = "0" + ret;
        }
        return ret;
    }

    private String nullCheck(String in) {
        if (in == null) {
            return "";
        }
        return in;
    }

    private void populateOrderRequest(OrderRequestDto orderRequest, Map<String, String> searchParams) {
        orderRequest.setCustBase(nullCheck(searchParams.get("CUSTBASE")));
        orderRequest.setCustSuf(nullCheck(searchParams.get("CUSTSUF")));
        orderRequest.setUserId(nullCheck(searchParams.get("USERID")));
        orderRequest.setGemsNo(nullCheck(searchParams.get("GEMSNo")));
        orderRequest.setCustName(nullCheck(searchParams.get("CustName")));
        orderRequest.setPoNo(nullCheck(searchParams.get("PONO")));
        orderRequest.setShName(nullCheck(searchParams.get("SHNAME")));
        orderRequest.setShAddr1(nullCheck(searchParams.get("SHADDR1")));
        orderRequest.setShAddr2(nullCheck(searchParams.get("SHADDR2")));
        orderRequest.setShCit(nullCheck(searchParams.get("SHCIT")));
        orderRequest.setShSt(nullCheck(searchParams.get("SHST")));
        orderRequest.setShZip(nullCheck(searchParams.get("SHZIP")));
        orderRequest.setShPh1(nullCheck(searchParams.get("SHPH1")));
        orderRequest.setShPh2(nullCheck(searchParams.get("SHPH2")));
        orderRequest.setConPh3(nullCheck(searchParams.get("SHPH3")));
        orderRequest.setConFirst(nullCheck(searchParams.get("CONFIRST")));
        orderRequest.setConLast(nullCheck(searchParams.get("CONLAST")));
        orderRequest.setConAddr1(nullCheck(searchParams.get("CONADDR1")));
        orderRequest.setConAddr2(nullCheck(searchParams.get("CONADDR2")));
        orderRequest.setConCit(nullCheck(searchParams.get("CONCIT")));
        orderRequest.setConSt(nullCheck(searchParams.get("CONST")));
        orderRequest.setConZip(nullCheck(searchParams.get("CONZIP")));
        orderRequest.setConPh1(nullCheck(searchParams.get("CONPH1")));
        orderRequest.setConPh2(nullCheck(searchParams.get("CONPH2")));
        orderRequest.setConPh3(nullCheck(searchParams.get("CONPH3")));
        orderRequest.setRqDte(nullCheck(searchParams.get("RQDTE")));
        orderRequest.setCarrier(nullCheck(searchParams.get("CARRIER")));
        orderRequest.setVndrFlg(nullCheck(searchParams.get("VNDRFLG")));
        orderRequest.setWplFlag(nullCheck(searchParams.get(WPLFLAG)));
        orderRequest.setNextdayFlag(nullCheck(searchParams.get(NEXTDAYFLAG)));
        orderRequest.setPoChange(nullCheck(searchParams.get("POCHANGE")));
    }

    private void processOrderRequest(OrderRequestDto input, String rfcDestinationName, OrderResponseDto response, String val) throws IOException {
        try {
            destination = helper.getJCoDestination(rfcDestinationName);
            function = destination.getRepository().getFunction(val);
            populateImportParameterList(input);
            if (input.getRqDte() != null && input.getRqDte().trim().length() > 0) {
                try {
                    Date date = sdf.parse(input.getRqDte());
                    java.sql.Date sd = new java.sql.Date(date.getTime());
                    logger.info("The REQ_DEL_DATE >>>>" + sd);
                    function.getImportParameterList().setValue("REQ_DEL_DATE", sd);
                } catch (Exception e) {
                    logger.info(" Exception converting Requested date");
                }
            }
            // populate address structure with Delivery Agent Address
            JCoStructure hdaAdr = function.getImportParameterList().getStructure("HDA_ADDRESS_IN");
            String TEL1_NUMBR = input.getShPh1() + input.getShPh2() + input.getShPh3();
            populateAddress(hdaAdr, input.getShName(), input.getShAddr1(), input.getShAddr2(), input.getShCit(), input.getShSt(), input.getShZip(), TEL1_NUMBR);

            // populate address structure with consumer address
            JCoStructure conAdr = function.getImportParameterList().getStructure("CON_ADDRESS_IN");
            String consumerName = input.getConFirst() + " " + input.getConLast();
            TEL1_NUMBR = input.getConPh1() + input.getConPh2() + input.getConPh3();
            populateAddress(conAdr, consumerName, input.getConAddr1(), input.getConAddr2(), input.getConCit(), input.getConSt(), input.getConZip(), TEL1_NUMBR);

            // populate table with requested model list
            JCoTable itemsIn = function.getTableParameterList().getTable("ITEMS_IN");
            JCoTable textIn = function.getTableParameterList().getTable("ITM_DELVRY_INS_IN");
            itemsIn.appendRows(input.getItems().length);
            textIn.appendRows(input.getItems().length);

            for (int i = 0; i < input.getItems().length; i++) {
                itemsIn.setRow(i);
                textIn.setRow(i);
                itemsIn.setValue("ITM_NUMBER", Integer.toString(i + 1));
                itemsIn.setValue("TARGET_QTY", input.getItems()[i].getQty());
                // Modified to add MR code in Short Text field
                String modelNum = input.getItems()[i].getModel();
                String mrCodePart = modelNum.substring(0, 2);
                if (mrCodePart.equalsIgnoreCase("MR")) {
                    String dash = modelNum.substring(4, 5);
                    if (dash.equals("-")) {
                        itemsIn.setValue("SHORT_TEXT", modelNum);
                        itemsIn.setValue("MATERIAL", "");
                    } else {
                        itemsIn.setValue("MATERIAL", input.getItems()[i].getModel());
                    }
                } else {
                    itemsIn.setValue("MATERIAL", input.getItems()[i].getModel());
                }
                //START CHG0014786 - Order Source/Expected Price
                //COST is only passed here if present in the request and switch useCostAndCustser is true
                String cost = input.getItems()[i].getCost();
                if (cost != null && !cost.equals("")) {
                    itemsIn.setValue("COND_TYPE", "EDI1");
                    itemsIn.setValue("COND_VALUE", input.getItems()[i].getCost());
                }
                //END CHG0014786 - Order Source/Expected Price
                textIn.setValue("ITM_NUMBER", Integer.toString(i + 1));
                textIn.setValue("TEXT_LINE", input.getItems()[i].getCustSer());
            }
            function.execute(destination);
            populateOrderResponse(response);

        } catch (Exception e) {
            logger.error(" Exception: " + e.toString());
            throw new IOException(e.toString());
        }
    }

    private void populateOrderResponse(OrderResponseDto response) {
        // populate the response object from the RFC output object
        response.setRCode(function.getExportParameterList().getString("RETURN_CODE"));
        response.setReqResults(function.getExportParameterList().getString("RETURN_MSG"));
        response.setMygOrderNo(function.getExportParameterList().getString("ORDER_NO"));
        response.setPoNo(function.getExportParameterList().getString("PO_NUMBER_OUT"));
        JCoStructure ba = function.getExportParameterList().getStructure("HDA_ADDRESS_OUT");
        response.setXDockName(ba.getString("NAME"));
        response.setXDockAddr(ba.getString("STREET"));
        response.setXDockCity(ba.getString("CITY"));
        response.setXDockSt(ba.getString("REGION"));
        response.setXDockZip(ba.getString("POSTL_COD1"));
        response.setXDockPhone(ba.getString("TEL1_NUMBR"));
    }

    private void populateImportParameterList(OrderRequestDto input) {
        function.getImportParameterList().setValue("GEMSNO", input.getGemsNo());
        function.getImportParameterList().setValue("PO_NUMBER", input.getPoNo());
        function.getImportParameterList().setValue("STORE_NO", input.getCustSuf());
        function.getImportParameterList().setValue("STORE_TYPE", input.getCustBase());
        function.getImportParameterList().setValue("VENDOR_FLAG", input.getVndrFlg());
        function.getImportParameterList().setValue(WPLFLAG, input.getWplFlag());
        function.getImportParameterList().setValue(NEXTDAYFLAG, input.getNextdayFlag());
        function.getImportParameterList().setValue("EXT_SALESDOC_NUM", input.getExtSalesdocNum());
        //START : CHG0017537 - Home Depot Box Project
        function.getImportParameterList().setValue(AGNTID, input.getAgntid());
        function.getImportParameterList().setValue("MSNO", input.getMsno());
        function.getImportParameterList().setValue("DVID", input.getDvid());
        //END : CHG0017537 - Home Depot Box Project
    }

    private void populateAddress(JCoStructure address, String name, String street, String strSuppl1, String city, String region, String postalCode, String telNo) {
        address.setValue("NAME", name);
        address.setValue("STREET", street);
        address.setValue("STR_SUPPL1", strSuppl1);
        address.setValue("CITY", city);
        address.setValue("REGION", region);
        address.setValue("POSTL_COD1", postalCode);
        address.setValue("TEL1_NUMBR", telNo);
    }
}
