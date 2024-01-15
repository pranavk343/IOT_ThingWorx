package com.whirlpool.order;


import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultParameterList;
import com.sap.conn.jco.rt.DefaultStructure;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.*;
import com.whirlpool.order.service.OrderOnlineService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderOnlineServiceTest extends OrderConfigLoaderTest{

    @Spy
    @InjectMocks
    private OrderOnlineService orderOnlineService;

    private JCoDestination mockJcoDestination;
    private JCoFunction mockJcoFunction;
    private JCoRepository mockRepository;
    private DefaultParameterList mockExportParameterListMetaData;

    private DefaultStructure jCoStructure;

    private JCoTable jCoTable;

    private JCoParameterList jCoParameterList;

    @Mock
    Helper mockHelper;

    OrderResponseDto test = new OrderResponseDto();
    OrderRequestDto input = new OrderRequestDto();

    @BeforeEach
    void setUp() throws JCoException {
        mockJcoDestination = mock(JCoDestination.class);
        mockJcoFunction = mock(JCoFunction.class);
        mockRepository = mock(JCoRepository.class);
        mockExportParameterListMetaData = mock(DefaultParameterList.class);
        jCoTable = mock(JCoTable.class);
        jCoParameterList = mock(JCoParameterList.class);
        jCoStructure = mock(DefaultStructure.class);

        OrderItem[] itemList = new OrderItem[1];
        OrderItem item = new OrderItem();
        item.setCost("99");
        item.setQty("6");
        item.setModel("MR46-5445");
        item.setCustSer("44");
        itemList[0] = item;
        input.setItems(itemList);
        input.setRqDte("04302023");
        System.out.println(item.getModel().substring(4, 5));

        when(mockHelper.getJCoDestination("ABAP_AS1")).thenReturn(mockJcoDestination);
        when(mockJcoDestination.getRepository()).thenReturn(mockRepository);
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_SALESORDER_CHANGE_CCSC2")).thenReturn(mockJcoFunction);
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_SALESORDER_CREATE_CCSC2")).thenReturn(mockJcoFunction);
        when(mockJcoFunction.getExportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getStructure("HDA_ADDRESS_IN")).thenReturn(jCoStructure);
        when(mockExportParameterListMetaData.getStructure("CON_ADDRESS_IN")).thenReturn(jCoStructure);
        when(mockJcoFunction.getTableParameterList()).thenReturn(jCoParameterList);
        when(jCoParameterList.getTable("ITEMS_IN")).thenReturn(jCoTable);
        when(jCoParameterList.getTable("ITM_DELVRY_INS_IN")).thenReturn(jCoTable);
        when(mockExportParameterListMetaData.getString("RETURN_CODE")).thenReturn("000");
        when(mockExportParameterListMetaData.getString("RETURN_MSG")).thenReturn("001");
        when(mockExportParameterListMetaData.getString("ORDER_NO")).thenReturn("002");
        when(mockExportParameterListMetaData.getString("PO_NUMBER_OUT")).thenReturn("003");
        when(mockExportParameterListMetaData.getStructure("HDA_ADDRESS_OUT")).thenReturn(jCoStructure);
        when(jCoStructure.getString("NAME")).thenReturn("004");
        when(jCoStructure.getString("STREET")).thenReturn("005");
        when(jCoStructure.getString("CITY")).thenReturn("006");
        when(jCoStructure.getString("REGION")).thenReturn("007");
        when(jCoStructure.getString("POSTL_COD1")).thenReturn("008");
        when(jCoStructure.getString("TEL1_NUMBR")).thenReturn("009");
//        JCoStructure hdaAdr = function.getImportParameterList().getStructure("HDA_ADDRESS_IN");
//        JCoTable itemsIn = function.getTableParameterList().getTable("ITEMS_IN");
    }


    @Test
    public void callOrderCreateFunctiontest() throws IOException {
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        OrderResponseDto output = orderOnlineService.callOrderFunction(input, "ABAP_AS1");
        System.out.println(output.toString());
        assertEquals("003", output.getPoNo());
    }

    @Test
    public void testCallOrderCreateFunction_whenThrowException() throws JCoException, IOException {
        input.setPoChange("Y");
        input.setRqDte("&&&");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        orderOnlineService.callOrderFunction(input, "ABAP_AS1");
        verify(mockJcoFunction, times(13)).getImportParameterList();
        input.setPoChange("N");
        orderOnlineService.callOrderFunction(input, "ABAP_AS1");
        verify(mockJcoFunction, times(26)).getImportParameterList();
    }

    @Test
    public void testCallOrderCreateFunction_whenThrowException2() throws JCoException, IOException {
        input.setPoChange("Y");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        doThrow(new RuntimeException()).when(mockHelper).getJCoDestination(any());
        assertThrows(IOException.class, () -> orderOnlineService.callOrderFunction(input, "ABAP_AS1"));
        input.setPoChange("N");
        assertThrows(IOException.class, () -> orderOnlineService.callOrderFunction(input, "ABAP_AS1"));
    }

    @Test
    public void callOrderChangeFunctiontest() throws IOException {
        input.setPoChange("Y");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        OrderResponseDto output = orderOnlineService.callOrderFunction(input, "ABAP_AS1");
        System.out.println(output.toString());
        assertEquals("003", output.getPoNo());
    }

    @Test
    public void bldOrderResponseTest() {
        String expected = "R-Code=&ReqResults=&MYGOrderNo=&PoNo=&XDockName=&XDockAddr=&XDockCity=&XdockSt=&XdockZip=&XDockPhone=";
        String output = orderOnlineService.bldOrderResponse(test);
        assertEquals(expected, output);
    }


    @Test
    public void orderEntryMethodTest() throws Exception {
        OrderRequestDto input = new OrderRequestDto();
        OrderResponseDto response = new OrderResponseDto();
        String[] CUSTSER = new String[1];
        CUSTSER[0] = "test";
        List<String> list = new ArrayList<>();
        list.add("list1");

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("CUSTBASE", "64012");
        searchParams.put("ORDERSRC", "O");
        searchParams.put("CUSTSUF", "6319");
        searchParams.put("USERID", "Hunt");
        searchParams.put("GEMSNo", "1NTP083733");
        searchParams.put("PONO", "0019591258");
        searchParams.put("SHNAME", "RAI");
        searchParams.put("SHADDR1", "3791 INTERCHANGE ROAD");
        searchParams.put("SHCIT", "COLUMBUS");
        searchParams.put("SHST", "OH");
        searchParams.put("SHZIP", "43204");
        searchParams.put("SHPH1", "614");
        searchParams.put("QTY01", "3");
        searchParams.put("MODEL01", "WDP540HAMZ");
        searchParams.put("COST01", "123");
        searchParams.put("CUSTSER", "45514982");
        searchParams.put("POCHANGE", "N");
        searchParams.put("DVNAME", "N");
        searchParams.put("SFID", "N");
        String expected = "R-Code=000&ReqResults=001&MYGOrderNo=002&PoNo=003&XDockName=004&XDockAddr=005&XDockCity=006&XdockSt=007&XdockZip=008&XDockPhone=009";
        String output = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output);
    }

    @Test
    public void testOrderEntryMethod_whenCallOrderFunction_returnNull() throws Exception {
        OrderRequestDto input = new OrderRequestDto();
        OrderResponseDto response = new OrderResponseDto();
        String[] CUSTSER = new String[1];
        CUSTSER[0] = "test";
        List<String> list = new ArrayList<>();
        list.add("list1");

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("CUSTBASE", "64012");
        searchParams.put("ORDERSRC", "O");
        searchParams.put("CUSTSUF", "6319");
        searchParams.put("USERID", "Hunt");
        searchParams.put("GEMSNo", "1NTP083733");
        searchParams.put("PONO", "0019591258");
        searchParams.put("SHNAME", "RAI");
        searchParams.put("SHADDR1", "3791 INTERCHANGE ROAD");
        searchParams.put("SHCIT", "COLUMBUS");
        searchParams.put("SHST", "OH");
        searchParams.put("SHZIP", "43204");
        searchParams.put("SHPH1", "614");
        searchParams.put("QTY01", "3");
        searchParams.put("MODEL01", "WDP540HAMZ");
        searchParams.put("COST01", "123");
        searchParams.put("CUSTSER", "45514982");
        searchParams.put("POCHANGE", "N");
        searchParams.put("DVNAME", "N");
        searchParams.put("SFID", "N");
        String expected = "R-Code=5&ReqResults=System not Available&MYGOrderNo=&PoNo=&XDockName=&XDockAddr=&XDockCity=&XdockSt=&XdockZip=&XDockPhone=";
        doReturn(null).when(orderOnlineService).callOrderFunction(any(OrderRequestDto.class),anyString());
        String output = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output);
    }

    @Test
    public void testOrderEntryMethod_whenCallOrderFunction_throwsIOException() throws Exception {
        OrderRequestDto input = new OrderRequestDto();
        OrderResponseDto response = new OrderResponseDto();
        String[] CUSTSER = new String[1];
        CUSTSER[0] = "test";
        List<String> list = new ArrayList<>();
        list.add("list1");

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("CUSTBASE", "64012");
        searchParams.put("ORDERSRC", "O");
        searchParams.put("CUSTSUF", "6319");
        searchParams.put("USERID", "Hunt");
        searchParams.put("GEMSNo", "1NTP083733");
        searchParams.put("PONO", "0019591258");
        searchParams.put("SHNAME", "RAI");
        searchParams.put("SHADDR1", "3791 INTERCHANGE ROAD");
        searchParams.put("SHCIT", "COLUMBUS");
        searchParams.put("SHST", "OH");
        searchParams.put("SHZIP", "43204");
        searchParams.put("SHPH1", "614");
        searchParams.put("QTY01", "3");
        searchParams.put("MODEL01", "WDP540HAMZ");
        searchParams.put("COST01", "123");
        searchParams.put("CUSTSER", "45514982");
        searchParams.put("POCHANGE", "N");
        searchParams.put("DVNAME", "N");
        searchParams.put("SFID", "N");
        String expected = "R-Code=5&ReqResults=System not Available&MYGOrderNo=&PoNo=&XDockName=&XDockAddr=&XDockCity=&XdockSt=&XdockZip=&XDockPhone=";
        doThrow(new IOException()).when(orderOnlineService).callOrderFunction(any(OrderRequestDto.class),anyString());
        String output = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output);
    }

    @Test
    public void testOrderEntryMethod_whenDiffCustBase()  {
        OrderRequestDto input = new OrderRequestDto();
        OrderResponseDto response = new OrderResponseDto();
        String[] CUSTSER = new String[1];
        CUSTSER[0] = "test";
        List<String> list = new ArrayList<>();
        list.add("list1");

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("CUSTBASE", "10720");
        searchParams.put("ORDERSRC", "O");
        searchParams.put("CUSTSUF", "6319");
        searchParams.put("USERID", "Hunt");
        searchParams.put("GEMSNo", "1NTP083733");
        searchParams.put("PONO", "0019591258");
        searchParams.put("SHNAME", "RAI");
        searchParams.put("SHADDR1", "3791 INTERCHANGE ROAD");
        searchParams.put("SHCIT", "COLUMBUS");
        searchParams.put("SHST", "OH");
        searchParams.put("SHZIP", "43204");
        searchParams.put("SHPH1", "614");
        searchParams.put("QTY01", "3");
        searchParams.put("MODEL01", "WDP540HAMZ");
        searchParams.put("COST01", "123");
        searchParams.put("CUSTSER", "45514982");
        searchParams.put("POCHANGE", "N");
        searchParams.put("DVNAME", "N");
        searchParams.put("SFID", "N");
        String expected = "R-Code=000&ReqResults=001&MYGOrderNo=002&PoNo=003&XDockName=004&XDockAddr=005&XDockCity=006&XdockSt=007&XdockZip=008&XDockPhone=009";
        String output = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output);

        //when custBase is 27302
        searchParams.put("CUSTBASE","27302");
        String output2 = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output2);

        //when ORDERSRC is S
        searchParams.put("ORDERSRC", "S");
        String output3 = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output3);

        //when DVNAME is null
        searchParams.put("DVNAME", null);
        String output4 = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output4);

        //when QTY01 is null
        searchParams.put("QTY01", "");
        String output5 = orderOnlineService.orderEntryService(CUSTSER, searchParams);
        assertEquals(expected, output5);
    }

}
