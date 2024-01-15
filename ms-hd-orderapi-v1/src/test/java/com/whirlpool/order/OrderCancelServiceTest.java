package com.whirlpool.order;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.rt.DefaultParameterList;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.OrderCancelRequest;
import com.whirlpool.order.dto.OrderCancelResponseDto;
import com.whirlpool.order.service.OrderCancelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderCancelServiceTest extends OrderConfigLoaderTest{
    @Spy
    @InjectMocks
    private OrderCancelService orderCancelService;

    @Mock
    private Helper mockHelper;

    private JCoDestination mockJcoDestination;
    private JCoFunction mockJcoFunction;
    private JCoRepository mockRepository;
    private DefaultParameterList mockExportParameterListMetaData;

    OrderCancelRequest input = new OrderCancelRequest();
    OrderCancelResponseDto test = new OrderCancelResponseDto();

    @BeforeEach
    void setUp() throws JCoException {
        mockJcoDestination = mock(JCoDestination.class);
        mockJcoFunction = mock(JCoFunction.class);
        mockRepository = mock(JCoRepository.class);
        mockExportParameterListMetaData = mock(DefaultParameterList.class);

        when(mockHelper.getJCoDestination("ABAP_AS1")).thenReturn(mockJcoDestination);
        when(mockJcoDestination.getRepository()).thenReturn(mockRepository);
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCEL")).thenReturn(mockJcoFunction);
        when(mockJcoFunction.getExportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getString("CAN_ALLOW_FLG_X")).thenReturn("Y");
        when(mockExportParameterListMetaData.getString("PO_NBR_X")).thenReturn("001");
        when(mockExportParameterListMetaData.getString("LOC_NBR_X")).thenReturn("002");
        when(mockExportParameterListMetaData.getString("RGA_PFX_IND")).thenReturn("003");
        when(mockExportParameterListMetaData.getString("PROD_STAT_CD_X")).thenReturn("004");
        when(mockExportParameterListMetaData.getString("PROD_STAT_TS_X")).thenReturn("005");
        when(mockExportParameterListMetaData.getString("ERR_MSG_CD")).thenReturn("test error code");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test description");
    }


    @Test
    public void callCancelFunctionValidTest() throws IOException {
        when(mockExportParameterListMetaData.getString("CAN_ALLOW_FLG_X")).thenReturn("Y");

        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        OrderCancelResponseDto output = orderCancelService.callCancelFunction(input, "ABAP_AS1");
        assertEquals("test description", output.getErr_msg_desc());
        assertEquals("test error code", output.getErr_msg_cd());
    }


    @Test
    public void callCancelFunctionInvalidTest() throws IOException {
        when(mockExportParameterListMetaData.getString("CAN_ALLOW_FLG_X")).thenReturn("N");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        OrderCancelResponseDto output = orderCancelService.callCancelFunction(input, "ABAP_AS1");
        assertEquals("Invalid Status", output.getErr_msg_desc());
    }


    @Test
    public void invokeOrderCancelTest() {
        String output = orderCancelService.invokeOrderCancel(input);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \"><po_nbr></po_nbr><loc_nbr></loc_nbr><err_msg_cd>200</err_msg_cd><err_msg_desc>System not Available</err_msg_desc></hd:VendorCancelResponse>";
        assertEquals(expected, output);
    }

    @Test
    public void invokeOrderCancelTest_whenCallCancelFunctionReturnNull() throws IOException {
        doReturn(null).when(orderCancelService).callCancelFunction(any(OrderCancelRequest.class), anyString());
        String output = orderCancelService.invokeOrderCancel(input);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \"><po_nbr></po_nbr><loc_nbr></loc_nbr><err_msg_cd>200</err_msg_cd><err_msg_desc>System not Available</err_msg_desc></hd:VendorCancelResponse>";
        assertEquals(expected, output);
    }

    @Test
    public void bldResponseTest() {
        test.setErr_msg_desc("error");
        test.setErr_msg_cd("000");
        String output = orderCancelService.bldResponse(test);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \"><po_nbr></po_nbr><loc_nbr></loc_nbr><prod_rga_nbr></prod_rga_nbr><prod_stat_cd></prod_stat_cd><can_ts></can_ts><err_msg_cd>000</err_msg_cd><err_msg_desc>error</err_msg_desc></hd:VendorCancelResponse>";
        assertEquals(expected.toString(), output.toString());
    }


    @Test
    public void bldResponseExceptionTest() {
        String output = orderCancelService.bldResponseException(test);
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \"><po_nbr></po_nbr><loc_nbr></loc_nbr><err_msg_cd></err_msg_cd><err_msg_desc></err_msg_desc></hd:VendorCancelResponse>";
        assertEquals(expected.toString(), output.toString());
    }

    @Test
    public void createResponseFooterTest() {
        StringBuilder sb = new StringBuilder();
        StringBuilder output = orderCancelService.createResponseFooter(sb);
        StringBuffer expected = new StringBuffer();
        expected.append("</hd:VendorCancelResponse>");
        assertEquals(expected.toString(), output.toString());
    }

    @Test
    public void createResponseHeaderTest() {
        StringBuilder output = orderCancelService.createResponseHeader();
        StringBuffer expected = new StringBuffer();
        expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><hd:VendorCancelResponse xmlns:hd=\"http://www.homedepot.com/HD\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.homedepot.com/HD../Schema/VendorCancelResponse.xsd \">");
        assertEquals(expected.toString(), output.toString());
    }


    @Test
    public void checkTimeStampTest() {
        String ts = "19980326T014200";
        String output = OrderCancelService.checkTimeStamp(ts);
        assertEquals("1998-03-26T01:42:00", output.toString());
    }


}
