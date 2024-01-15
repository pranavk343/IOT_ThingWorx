package com.whirlpool.order;


import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultParameterList;
import com.sap.conn.jco.rt.DefaultTable;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.USCancelLnItem;
import com.whirlpool.order.dto.USCancelLnRequestDTO;
import com.whirlpool.order.dto.USCancelLnResponseDTO;
import com.whirlpool.order.dto.USRespCancelLnItem;
import com.whirlpool.order.service.OrderLineCancelUSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderLineCancelUSServiceTest extends OrderConfigLoaderTest{
    @Spy
    @InjectMocks
    private OrderLineCancelUSService orderLineCancelUSService;

    @Mock
    private Helper mockHelper;
    @Mock
    private JCoTable mockGetMockjCoTable;

    private JCoDestination mockJcoDestination;
    private JCoFunction mockJcoFunction;
    private JCoRepository mockRepository;
    private DefaultParameterList mockExportParameterListMetaData;
    private DefaultTable mockTableParameterListMetaData;
    private JCoTable mockjCoTable;
    private JCoParameterList mockjCoParameterList;


    USCancelLnRequestDTO input = new USCancelLnRequestDTO();
    USCancelLnResponseDTO output = new USCancelLnResponseDTO();

    USCancelLnResponseDTO test = new USCancelLnResponseDTO();
    USRespCancelLnItem itemListresponse[] = new USRespCancelLnItem[1];
    USRespCancelLnItem itemresponse = new USRespCancelLnItem();

    @BeforeEach
    void setUp() throws JCoException {

        input.setPoNumber("111");
        input.setCustSuf("000");
        input.setCanAllowFlg("Y");
        input.setDlvryStatCd("Y");
        input.setCustBase("US");
        input.setMsNumber("098");

        USCancelLnItem itemList[] = new USCancelLnItem[1];
        USCancelLnItem item = new USCancelLnItem();
        item.setCancelled(true);
        item.setHeaderValue("header");
        item.setOrderQuantity("8");
        item.setModelNumber("AWE45678");
        item.setCancelQuantity("8");
        itemList[0] = item;
        input.setLineItems(itemList);
        itemresponse.isCancelled=true;
        itemresponse.setModelNumber("09uGY0");
        itemresponse.setRgaNumber("78767");
        itemListresponse[0] = itemresponse;
        output.setLineItems(itemListresponse);
        mockJcoDestination = mock(JCoDestination.class);
        mockJcoFunction = mock(JCoFunction.class);
        mockRepository = mock(JCoRepository.class);
        mockExportParameterListMetaData = mock(DefaultParameterList.class);
        mockTableParameterListMetaData = mock(DefaultTable.class);
        // jCoTable = mock(JCoTable.class);
        mockjCoTable = new JCoTable() {
            @Override
            public JCoRecordMetaData getRecordMetaData() {
                return null;
            }

            @Override
            public void ensureBufferCapacity(int i) {

            }

            @Override
            public void trimToRows() {

            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean isFirstRow() {
                return false;
            }

            @Override
            public boolean isLastRow() {
                return false;
            }

            @Override
            public int getNumRows() {
                return 2;
            }

            @Override
            public int getNumColumns() {
                return 0;
            }

            @Override
            public void clear() {

            }

            @Override
            public void deleteAllRows() {

            }

            @Override
            public void firstRow() {

            }

            @Override
            public void lastRow() {

            }

            @Override
            public boolean nextRow() {
                return false;
            }

            @Override
            public boolean previousRow() {
                return false;
            }

            @Override
            public int getRow() {
                return 0;
            }

            @Override
            public void setRow(int i) {

            }

            @Override
            public void appendRow() {

            }

            @Override
            public void appendRows(int i) {

            }

            @Override
            public void insertRow(int i) {

            }

            @Override
            public void deleteRow() {

            }

            @Override
            public void deleteRow(int i) {

            }

            @Override
            public JCoRecordFieldIterator getRecordFieldIterator() {
                return null;
            }

            @Override
            public String getString() {
                return null;
            }

            @Override
            public void setString(String s) {

            }

            @Override
            public JCoMetaData getMetaData() {
                return null;
            }

            @Override
            public int copyFrom(JCoRecord jCoRecord) {
                return 0;
            }

            @Override
            public int getFieldCount() {
                return 0;
            }

            @Override
            public JCoField getField(int i) {
                return null;
            }

            @Override
            public JCoField getField(String s) {
                return null;
            }

            @Override
            public Object getValue(int i) {
                return null;
            }

            @Override
            public Object getValue(String s) {
                if (s == "MATERIAL") {
                    return "testmaterial";
                }
                if (s == "SHORT_TEXT") {
                    return "testtext";
                } else {
                    return "dummy";
                }
            }

            @Override
            public String getString(int i) {
                return null;
            }

            @Override
            public String getString(String s) {
                return null;
            }

            @Override
            public char getChar(int i) {
                return 0;
            }

            @Override
            public char getChar(String s) {
                return 0;
            }

            @Override
            public char[] getCharArray(int i) {
                return new char[0];
            }

            @Override
            public char[] getCharArray(String s) {
                return new char[0];
            }

            @Override
            public byte getByte(int i) {
                return 0;
            }

            @Override
            public byte getByte(String s) {
                return 0;
            }

            @Override
            public byte[] getByteArray(int i) {
                return new byte[0];
            }

            @Override
            public byte[] getByteArray(String s) {
                return new byte[0];
            }

            @Override
            public short getShort(int i) {
                return 0;
            }

            @Override
            public short getShort(String s) {
                return 0;
            }

            @Override
            public int getInt(int i) {
                return 0;
            }

            @Override
            public int getInt(String s) {
                return 0;
            }

            @Override
            public long getLong(int i) {
                return 0;
            }

            @Override
            public long getLong(String s) {
                return 0;
            }

            @Override
            public float getFloat(int i) {
                return 0;
            }

            @Override
            public float getFloat(String s) {
                return 0;
            }

            @Override
            public double getDouble(int i) {
                return 0;
            }

            @Override
            public double getDouble(String s) {
                return 0;
            }

            @Override
            public BigInteger getBigInteger(int i) {
                return null;
            }

            @Override
            public BigInteger getBigInteger(String s) {
                return null;
            }

            @Override
            public BigDecimal getBigDecimal(int i) {
                return null;
            }

            @Override
            public BigDecimal getBigDecimal(String s) {
                return null;
            }

            @Override
            public Date getDate(int i) {
                return null;
            }

            @Override
            public Date getDate(String s) {
                return null;
            }

            @Override
            public Date getTime(int i) {
                return null;
            }

            @Override
            public Date getTime(String s) {
                return null;
            }

            @Override
            public InputStream getBinaryStream(int i) {
                return null;
            }

            @Override
            public InputStream getBinaryStream(String s) {
                return null;
            }

            @Override
            public Reader getCharacterStream(int i) {
                return null;
            }

            @Override
            public Reader getCharacterStream(String s) {
                return null;
            }

            @Override
            public JCoStructure getStructure(int i) {
                return null;
            }

            @Override
            public JCoStructure getStructure(String s) {
                return null;
            }

            @Override
            public JCoTable getTable(int i) {
                return null;
            }

            @Override
            public JCoTable getTable(String s) {
                return null;
            }

            @Override
            public JCoAbapObject getAbapObject(int i) {
                return null;
            }

            @Override
            public JCoAbapObject getAbapObject(String s) {
                return null;
            }

            @Override
            public String getClassNameOfValue(String s) {
                return null;
            }

            @Override
            public void setValue(int i, String s) {

            }

            @Override
            public void setValue(String s, String s1) {

            }

            @Override
            public void setValue(int i, char c) {

            }

            @Override
            public void setValue(String s, char c) {

            }

            @Override
            public void setValue(int i, char[] chars) {

            }

            @Override
            public void setValue(String s, char[] chars) {

            }

            @Override
            public void setValue(int i, char[] chars, int i1, int i2) {

            }

            @Override
            public void setValue(String s, char[] chars, int i, int i1) {

            }

            @Override
            public void setValue(int i, short i1) {

            }

            @Override
            public void setValue(String s, short i) {

            }

            @Override
            public void setValue(int i, int i1) {

            }

            @Override
            public void setValue(String s, int i) {

            }

            @Override
            public void setValue(int i, long l) {

            }

            @Override
            public void setValue(String s, long l) {

            }

            @Override
            public void setValue(int i, float v) {

            }

            @Override
            public void setValue(String s, float v) {

            }

            @Override
            public void setValue(int i, double v) {

            }

            @Override
            public void setValue(String s, double v) {

            }

            @Override
            public void setValue(int i, byte b) {

            }

            @Override
            public void setValue(String s, byte b) {

            }

            @Override
            public void setValue(int i, byte[] bytes) {

            }

            @Override
            public void setValue(String s, byte[] bytes) {

            }

            @Override
            public void setValue(int i, BigDecimal bigDecimal) {

            }

            @Override
            public void setValue(String s, BigDecimal bigDecimal) {

            }

            @Override
            public void setValue(int i, JCoStructure jCoStructure) {

            }

            @Override
            public void setValue(String s, JCoStructure jCoStructure) {

            }

            @Override
            public void setValue(int i, JCoTable jCoTable) {

            }

            @Override
            public void setValue(String s, JCoTable jCoTable) {

            }

            @Override
            public void setValue(int i, JCoAbapObject jCoAbapObject) {

            }

            @Override
            public void setValue(String s, JCoAbapObject jCoAbapObject) {

            }

            @Override
            public void setValue(int i, Object o) {

            }

            @Override
            public void setValue(String s, Object o) {

            }

            @Override
            public boolean isInitialized(int i) {
                return false;
            }

            @Override
            public boolean isInitialized(String s) {
                return false;
            }

            @Override
            public String toXML(int i) {
                return null;
            }

            @Override
            public String toXML(String s) {
                return null;
            }

            @Override
            public String toXML() {
                return null;
            }

            @Override
            public String toJSON() {
                return null;
            }

            @Override
            public void toJSON(Writer writer) throws IOException {

            }

            @Override
            public void fromJSON(Reader reader) {

            }

            @Override
            public void fromJSON(String s) {

            }

            @Override
            public Writer write(int i, Writer writer) throws IOException {
                return null;
            }

            @Override
            public Writer write(String s, Writer writer) throws IOException {
                return null;
            }

            @Override
            public Iterator<JCoField> iterator() {
                return null;
            }

            @Override
            public JCoFieldIterator getFieldIterator() {
                return null;
            }

            @Override
            public Object clone() {
                return null;
            }
        };
        mockjCoParameterList = mock(JCoParameterList.class);
        when(mockHelper.getJCoDestination("ABAP_AS1")).thenReturn(mockJcoDestination);
        when(mockJcoDestination.getRepository()).thenReturn(mockRepository);
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCL_LN")).thenReturn(mockJcoFunction);
        when(mockJcoFunction.getExportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getString("PO_NBR_X")).thenReturn("000");
        when(mockExportParameterListMetaData.getString("LOC_NBR_X")).thenReturn("001");
        when(mockExportParameterListMetaData.getString("PROD_STAT_DESC_X")).thenReturn("003");
        when(mockExportParameterListMetaData.getString("CAN_ALLOW_FLG_X")).thenReturn("004");
        when(mockExportParameterListMetaData.getString("ERR_MSG_CD")).thenReturn("test error code");
        when(mockJcoFunction.getTableParameterList()).thenReturn(mockjCoParameterList);
        when(mockjCoParameterList.getTable("ITEMS_IN")).thenReturn(mockjCoTable);

    }


    @Test
    public void orderCancelUSTest() throws IOException {
        test.setPoNumber("000");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        output = orderLineCancelUSService.orderCancelUS(input, "header");
        assertEquals(test.getErr().getDescription(), output.getErr().getDescription());
        assertEquals(test.getPoNumber(), output.getPoNumber());
    }

    @Test
    public void orderCancelUSTest_whenPoNumberIsNull() throws IOException {
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getString("PO_NBR_X")).thenReturn(null);
        output = orderLineCancelUSService.orderCancelUS(input, "header");
        assertNull(output.getPoNumber());
    }

    @Test
    public void orderCancelUSTest_whenPROD_DESC_IN_TRANSIT() throws IOException {
        test.getErr().setDescription("Missing Model");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockjCoParameterList.getTable("ITEMS_IN")).thenReturn(mockGetMockjCoTable);
        when(mockGetMockjCoTable.getValue("MATERIAL")).thenReturn(null);
        when(mockGetMockjCoTable.getNumRows()).thenReturn(1);
        when(mockExportParameterListMetaData.getString("PROD_STAT_DESC_X")).thenReturn("IN TRANSIT");
        output = orderLineCancelUSService.orderCancelUS(input, "header");
        assertEquals(test.getErr().getDescription(), output.getErr().getDescription());
    }

    @Test
    public void orderCancelUSTest_whenThrowsException() throws IOException {
        test.setPoNumber("000");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        doThrow(new IOException()).when(orderLineCancelUSService).callCancelFunction(any(USCancelLnRequestDTO.class), anyString());
        assertThrows(NullPointerException.class, () -> orderLineCancelUSService.orderCancelUS(input, "header"));
    }

    @Test
    public void localOrderCancelTest() {
        USCancelLnResponseDTO output = new USCancelLnResponseDTO();
        output.setLineItems(itemListresponse);
        orderLineCancelUSService.localOrderCancel(input, output);
        assertEquals(input.getPoNumber(), output.getPoNumber());
    }

    @ParameterizedTest
    @CsvSource({"'Previously Cancelled', 1201", "'Not Found on Order',1202", "'PO Not Found',1203", "'currently being processed',1204", "'Matching Quantity not Found',1101"})
    public void testOrderCancelUSParamaterized(String errMsgDesc, int expectedErrorCode) throws IOException {
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn(errMsgDesc);
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        output = orderLineCancelUSService.orderCancelUS(input, "header");
        assertEquals(expectedErrorCode, output.getErr().getErrorCode());
    }
}
