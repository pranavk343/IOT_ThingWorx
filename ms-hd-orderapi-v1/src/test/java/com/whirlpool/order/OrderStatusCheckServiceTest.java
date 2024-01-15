package com.whirlpool.order;

import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultParameterList;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.OrderStatusRequest;
import com.whirlpool.order.dto.OrderStatusResponse;
import com.whirlpool.order.service.OrderStatusCheckService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderStatusCheckServiceTest extends OrderConfigLoaderTest{

    @Spy
    @InjectMocks
    private OrderStatusCheckService orderStatusCheckService;
    private JCoDestination mockJcoDestination;
    @Mock
    private JCoFunction mockJcoFunction;
    @Mock
    private JCoParameterList parameterList;
    @Mock
    private Helper mockHelper;

    private JCoRepository mockRepository;
    private DefaultParameterList mockExportParameterListMetaData;


    @BeforeEach
    void setUp() throws JCoException {
        mockJcoDestination = mock(JCoDestination.class);
        mockJcoFunction = mock(JCoFunction.class);
        mockRepository = mock(JCoRepository.class);
        mockExportParameterListMetaData = mock(DefaultParameterList.class);
        when(mockHelper.getJCoDestination("ABAP_AS1")).thenReturn(mockJcoDestination);
        when(mockJcoDestination.getRepository()).thenReturn(mockRepository);
        doNothing().when(mockJcoFunction).execute(any(JCoDestination.class));
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCEL")).thenReturn(mockJcoFunction);
        when(mockJcoFunction.getExportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getString("MFR_SHP_NBR")).thenReturn("000");
        when(mockExportParameterListMetaData.getString("PO_NBR_X")).thenReturn("001");
        when(mockExportParameterListMetaData.getString("LOC_NBR_X")).thenReturn("002");
        when(mockExportParameterListMetaData.getString("CAN_ALLOW_FLG_X")).thenReturn("Y");
        when(mockExportParameterListMetaData.getString("RGA_PFX_IND")).thenReturn("003");
        when(mockExportParameterListMetaData.getString("PROD_STAT_CD_X")).thenReturn("004");
        when(mockExportParameterListMetaData.getString("PROD_STAT_TS_X")).thenReturn("005");
        when(mockExportParameterListMetaData.getString("ERR_MSG_CD")).thenReturn("test error code");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
    }

    public OrderStatusRequest initializeOrderStatusRequest(){
        OrderStatusRequest input = new OrderStatusRequest();
        input.setPo_nbr("41500015");
        input.setLoc_nbr("1241");
        input.setDlvry_stat_cd("000");
        input.setCan_allow_flg("Y");

        return input;
    }


    @Test
    public void testCheckServiceSuccess() throws IOException {
        OrderStatusResponse test = new OrderStatusResponse();
        test.setErr_msg_cd("test error code");
        test.setErr_msg_desc("test error desc");

        when(mockJcoFunction.getImportParameterList()).thenReturn(parameterList);
        doNothing().when(parameterList).setValue(anyString(), anyString());
        OrderStatusResponse output = orderStatusCheckService.checkService(initializeOrderStatusRequest());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
    }

    @Test
    public void testCheckServiceSuccess_whenNullPointerException_queueDirLengthGreaterThan_0() throws IOException {
        OrderStatusResponse test = new OrderStatusResponse();
        test.setErr_msg_cd("200");
        test.setErr_msg_desc("System not Available");

        OrderStatusResponse output = orderStatusCheckService.checkService(initializeOrderStatusRequest());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
    }

    @Test
    public void testCheckServiceSuccess_whenOutputIsNull() throws IOException {
        OrderStatusResponse test = new OrderStatusResponse();
        test.setErr_msg_cd("200");
        test.setErr_msg_desc("System not Available");
        doReturn(null).when(orderStatusCheckService).callStatusfunction(any(OrderStatusRequest.class), anyString());
        OrderStatusResponse output = orderStatusCheckService.checkService(initializeOrderStatusRequest());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
    }


    @Test
    public void callStatusfunctiontest() throws IOException {
        OrderStatusResponse test = new OrderStatusResponse();
        OrderStatusRequest input = new OrderStatusRequest();
        test.setErr_msg_desc("test error desc");
        test.setErr_msg_cd("test error code");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        OrderStatusResponse output = orderStatusCheckService.callStatusfunction(input, "ABAP_AS1");
        System.out.println(output.toString());
        assertEquals(output.getErr_msg_cd(), test.getErr_msg_cd());
        assertEquals(output.getErr_msg_desc(), test.getErr_msg_desc());
    }


}



