package com.whirlpool.order;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.whirlpool.order.controller.HDOrderController;
import com.whirlpool.order.dto.*;
import com.whirlpool.order.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
@WebMvcTest(HDOrderController.class)
public class HDOrderControllerTest {




    @Autowired
    public MockMvc mockMvc;


    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    HDOrderController hdOrderController;

    @MockBean
    OrderOnlineService orderOnlineService;

    @MockBean
    OrderStatusCheckService orderStatusCheckService;

    @MockBean
    OrderLineCancelCAService orderLineCancelCAService;

    @MockBean
    OrderLineCancelUSService orderLineCancelUSService;

    @MockBean
    OrderCancelService orderCancelService;

    @Autowired
    private ObjectMapper objectMapper;


   /* public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }*/



    @Test
    public void orderStatusControllerTest() throws Exception {
        String t= "<hd:OrderStatusRequests xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD ../schemas/OrderStatusResponse.xsd\">\n" +
                "   <OrderStatusRequest>\n" +
                "      <po_nbr>41500015</po_nbr>\n" +
                "      <loc_nbr>1241</loc_nbr>\n" +
                "      <dlvry_stat_cd>000</dlvry_stat_cd>\n" +
                "      <dlvry_stat_ts />\n" +
                "      <prod_stat_cd />\n" +
                "      <prod_stat_ts />\n" +
                "      <can_allow_flg>Y</can_allow_flg>\n" +
                "   </OrderStatusRequest>\n" +
                "</hd:OrderStatusRequests>";


        OrderStatusRequest input = new OrderStatusRequest();
       // OrderStatusRequests request = new OrderStatusRequests();


        input.setPo_nbr("41500015");
        input.setLoc_nbr("1241");
        input.setDlvry_stat_cd("000");
        input.setCan_allow_flg("Y");
        OrderStatusResponse response = new OrderStatusResponse();
        response.setPo_nbr("test");
        String jsonString=objectMapper.writeValueAsString(input);
        JSONObject jsonObject=new JSONObject(jsonString);
        //        JAXBContext jaxbContext=JAXBContext.newInstance(OrderStatusRequest.class);
//        Marshaller jaxbMarshaller=jaxbContext.createMarshaller();
//        StringWriter sw=new StringWriter();
       // String xml="<hd:OrderStatusRequests xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD ../schemas/OrderStatusResponse.xsd\">"+ XML.toString(jsonObject);
//        String xml=sw.toString();
//        jaxbMarshaller.marshal(input,sw);
//        System.out.println(sw.toString());
      //  Mockito.when(orderStatusCheckService.checkService(Mockito.any())).thenReturn(response);


      //  request.setOrderStatusRequest(input);
        mockMvc.perform(
                post("/HD/Order/OrderStatusCheck")
                        .contentType(MediaType.APPLICATION_XML_VALUE)
                        .content(t)

        ).andExpect(
                MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andDo(MockMvcResultHandlers.print());




    }



    @Test
    public void OrderLineCancelCAServiceTest() throws Exception {
        String t= "<hd:VendorCancelRequest xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \" xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "   <po_nbr>0000088888</po_nbr>\n" +
                "   <lines>\n" +
                "      <line>\n" +
                "         <item_no>00010</item_no>\n" +
                "         <qty>1</qty>\n" +
                "         <model>MAV208DAWW</model>\n" +
                "         <cost>81.01</cost>\n" +
                "         <prod_stat_cd>100</prod_stat_cd>\n" +
                "         <can_ts>2021-01-08T09:51:08</can_ts>\n" +
                "         <err_msg_cd>000</err_msg_cd>\n" +
                "      </line>\n" +
                "   </lines>\n" +
                "   <loc_nbr>1508</loc_nbr>\n" +
                "</hd:VendorCancelRequest>";



       CACancelLnRequestDTO request= new CACancelLnRequestDTO();
        CACancelLnResponseDTO response=new CACancelLnResponseDTO();
//        input.setPo_nbr("41500015");
//        input.setLoc_nbr("1241");
//        input.setDlvry_stat_cd("000");
//        input.setCan_allow_flg("Y");
//        OrderStatusResponse response = new OrderStatusResponse();
//        response.setPo_nbr("test");
        //String jsonString=objectMapper.writeValueAsString(input);
       // JSONObject jsonObject=new JSONObject(jsonString);
        //        JAXBContext jaxbContext=JAXBContext.newInstance(OrderStatusRequest.class);
//        Marshaller jaxbMarshaller=jaxbContext.createMarshaller();
//        StringWriter sw=new StringWriter();
        // String xml="<hd:OrderStatusRequests xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD ../schemas/OrderStatusResponse.xsd\">"+ XML.toString(jsonObject);
//        String xml=sw.toString();
//        jaxbMarshaller.marshal(input,sw);
//        System.out.println(sw.toString());

        RespCancelLnItemCA inputarray[] = new RespCancelLnItemCA[1];
        RespCancelLnItemCA input=new RespCancelLnItemCA();
        input.setLine("test");
        input.setCost("000");
        input.setQTY("0");
        input.setMaterial("material");
        ErrorDetail err=new ErrorDetail();
        err.setErrorCode(000);
        err.setDescription("error");
        input.setErr(err);
        inputarray[0]=input;
       response.setLines(inputarray);
       response.setPo_nbr("0909");
       response.setLoc_nbr("67890");
        System.out.println("len : "+response.getLines().length);
        Mockito.doReturn(response).when(orderLineCancelCAService).orderCancelCA(Mockito.any());
       // Mockito.when(orderLineCancelCAService.orderCancelCA(request)).thenReturn(response);
//        Mockito.when(response.getLines().length).thenReturn(1);
        //  request.setOrderStatusRequest(input);
        mockMvc.perform(
                        post("/HD/Order/OrderLnCancel")
                                .contentType(MediaType.APPLICATION_XML_VALUE)
                                .content(t)

                ).andExpect(
                        MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andDo(MockMvcResultHandlers.print());




    }


    @Test
    public void OrderLineCancelUSServicevalidTest() throws Exception {
        String jsonString= "{\n" +
                "    \"poNumber\": \"0019591258\",\n" +
                "    \"storeNumber\": \"6319\",\n" +
                "    \"vendorNumber\": \"\",\n" +
                "    \"msNumber\": \"1NTP083733\",\n" +
                "    \"lineItems\": [\n" +
                "        {\n" +
                "            \"modelNumber\": \"WDP540HAMZ\",\n" +
                "            \"orderQuantity\": 3,\n" +
                "            \"cancelQuantity\": 3\n" +
                "        },\n" +
                "\t\t\t\t{\n" +
                "            \"modelNumber\": \"YWFE515S0JB\",\n" +
                "            \"orderQuantity\": 3,\n" +
                "            \"cancelQuantity\": 3\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        USCancelLnRequestDTO input = new USCancelLnRequestDTO();
        USCancelLnResponseDTO output = new USCancelLnResponseDTO();
        Mockito.doReturn(output).when(orderLineCancelUSService).orderCancelUS(Mockito.any(),Mockito.anyString());

        mockMvc.perform(
                        post("/HD/Order/OrderLnCancelUS")
                                .header("X-Cancel-Request-Id", 12)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)

                ).andExpect(
                        MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andDo(MockMvcResultHandlers.print());

    }

}

