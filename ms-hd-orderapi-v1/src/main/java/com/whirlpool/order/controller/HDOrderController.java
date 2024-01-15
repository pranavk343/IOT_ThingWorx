package com.whirlpool.order.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.whirlpool.order.dto.*;
import com.whirlpool.order.service.*;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class HDOrderController {
    Logger logger = LoggerFactory.getLogger(HDOrderController.class);

    @Autowired
    private OrderOnlineService orderOnlineService;

    @Autowired
    private OrderStatusCheckService orderStatusCheckService;

    @Autowired
    private OrderLineCancelCAService orderLineCancelCAService;

    @Autowired
    private OrderLineCancelUSService orderLineCancelUSService;

    @Autowired
    private OrderCancelService orderCancelService;

    @RequestMapping(value = "/HD/Order/OrderStatusCheck", method = RequestMethod.POST, produces = {MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<OrderStatusResponses> orderStatusController(@RequestBody OrderStatusRequests request) throws IOException {
        OrderStatusResponses orderStatusResponses = new OrderStatusResponses();
        orderStatusResponses.setOrderStatusResponse(orderStatusCheckService.checkService(request.getOrderStatusRequest()));
        return new ResponseEntity<>(orderStatusResponses, HttpStatus.OK);
    }


    @RequestMapping(value = "/HD/Order/OrderLnCancel", method = RequestMethod.POST, produces = {MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<MappingJacksonValue> orderLineCancelCAController(@RequestBody CACancelLnRequestDTO request) throws IOException {
        CACancelLnResponseDTO output = orderLineCancelCAService.orderCancelCA(request);
        LineCancelDTO response = new LineCancelDTO();
        CancelLnItemCAResponse items[] = new CancelLnItemCAResponse[output.getLines().length];
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = null;
        if (output.getErr_msg_desc().length() == 0) {
            simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("err_msg_desc");
            logger.info("No error message description; filtering err_msg_desc field");
        } else {
            simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("");
            logger.info("error message description: " + output.getErr_msg_desc());
        }

        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("msgdesc", simpleBeanPropertyFilter);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(response);
        mappingJacksonValue.setFilters(filterProvider);

        int itemCount = 0;
        response.setPo_nbr(output.getPo_nbr());
        response.setLoc_nbr(output.getLoc_nbr());
        logger.info("number of line item: {}", output.getLines().length);
        for (RespCancelLnItemCA i : output.getLines()) {
            CancelLnItemCAResponse item = new CancelLnItemCAResponse();
            item.setCost(i.getCost());
            item.setItem_no(i.getLine());
            item.setQty(i.getQTY());
            item.setModel(i.getMaterial());
            item.setCancellation_nbr(i.getShorttext1());
            item.setProd_stat_cd(output.getProd_stat_cd());
            item.setCan_ts(output.getCan_ts());
            item.setErr_msg_cd(output.getErr_msg_cd());
            item.setErr_msg_desc(output.getErr_msg_desc());
            items[itemCount] = item;
            itemCount++;
        }
        response.setLines(items);
        return new ResponseEntity<>(mappingJacksonValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/HD/Order/OrderCancel", method = RequestMethod.POST, produces = {MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<String> orderCancel(@RequestBody OrderCancel request) {
        logger.info("Order cancel Payload {}", request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_XML);
        String result = Encode.forCDATA(orderCancelService.invokeOrderCancel(request.getOrderCancel()));
        return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/HD/Order/OrderLnCancelUS", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MappingJacksonValue> orderLineCancelUSController(@RequestBody USCancelLnRequestDTO request, @RequestHeader("X-Cancel-Request-Id") String header) throws IOException {
        USCancelLnResponseOutputFrame frame = new USCancelLnResponseOutputFrame();
        USCancelLnResponseDTO output = orderLineCancelUSService.orderCancelUS(request, header);
        List<USCancelLnResponseDTO> usCancelLnResponseDTOS=new ArrayList<>();
        output.setError(output.getError());
        usCancelLnResponseDTOS.add(output);
        frame.setCancelResponse(usCancelLnResponseDTOS);
        frame.setError(output.getError());
        SimpleBeanPropertyFilter simpleBeanPropertyFilter;
        if (output.getErr().getErrorCode() != 0) {
            simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("error");
            logger.info("filtering error field");
        } else {
            simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("error");
            logger.info("filtering errorDetail field");
        }
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("errorFilter", simpleBeanPropertyFilter);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(frame);
        mappingJacksonValue.setFilters(filterProvider);

        if (output.getErr().getErrorCode() == 1203 || output.getErr().getErrorCode() == 1202 || output.getErr().getErrorCode() == 1204) {

            return new ResponseEntity<>(mappingJacksonValue, HttpStatus.NOT_FOUND);
        } else if (output.getErr().getErrorCode() == 4000) {

            return new ResponseEntity<>(mappingJacksonValue, HttpStatus.BAD_REQUEST);
        } else if (output.getErr().getErrorCode() == 1101) {

            return new ResponseEntity<>(mappingJacksonValue, HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (output.getErr().getErrorCode() == 2001) {
            return new ResponseEntity<>(mappingJacksonValue, HttpStatus.MULTI_STATUS);
        } else {
            return new ResponseEntity<>(mappingJacksonValue, HttpStatus.OK);
        }


    }


    @GetMapping("/HD/Order/OrderEntry")
    public ResponseEntity<String> orderEntry(@RequestParam String[] CUSTSER,
                                             @RequestParam Map<String, String> searchParams) {
        String output = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
