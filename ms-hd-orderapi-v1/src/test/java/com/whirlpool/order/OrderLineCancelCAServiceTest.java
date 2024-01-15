package com.whirlpool.order;


import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultParameterList;
import com.sap.conn.jco.rt.DefaultTable;
import com.whirlpool.order.common.Helper;
import com.whirlpool.order.dto.CACancelLnRequestDTO;
import com.whirlpool.order.dto.CACancelLnResponseDTO;
import com.whirlpool.order.dto.Line;
import com.whirlpool.order.dto.RespCancelLnItemCA;
import com.whirlpool.order.service.OrderLineCancelCAService;
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
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderLineCancelCAServiceTest extends OrderConfigLoaderTest{

    @Spy
    @InjectMocks
    private OrderLineCancelCAService orderLineCancelCAService;

    private JCoDestination mockJcoDestination;
    private JCoFunction mockJcoFunction;
    private JCoRepository mockRepository;
    private DefaultParameterList mockExportParameterListMetaData;

    private DefaultTable mockTableParameterListMetaData;

    private JCoTable mockjCoTable;

    private JCoParameterList mockjCoParameterList;

    @Mock
    Helper mockHelper;

    CACancelLnRequestDTO input = new CACancelLnRequestDTO();
    CACancelLnResponseDTO test = new CACancelLnResponseDTO();

    @BeforeEach
    void setUp() throws JCoException {
        input.setPo_nbr("41500015");
        input.setLoc_nbr("1241");
        input.setDlvry_stat_cd("000");
        input.setCan_allow_flg("Y");
        List<Line> lists = new ArrayList<>();
        Line lines = new Line();
        lines.setCost("111");
        lines.setModel("123");
        lines.setQty("7");
        lines.setItem_no("000");
        lines.setCan_ts("22");
        lines.setProd_stat_cd("99");
        lines.setErr_msg_cd("987");
        lists.add(lines);
        input.setLines(lists);
        test.setErr_msg_cd("test error code");
        test.setErr_msg_desc("test error desc");

        mockJcoDestination = mock(JCoDestination.class);
        mockJcoFunction = mock(JCoFunction.class);
        mockRepository = mock(JCoRepository.class);
        mockExportParameterListMetaData = mock(DefaultParameterList.class);
        mockTableParameterListMetaData = mock(DefaultTable.class);
        // jCoTable = mock(JCoTable.class);
        mockjCoParameterList = mock(JCoParameterList.class);
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
                if (s == "ITM_NUMBER") {
                    return "item no";
                }
                if (s == "SHORT_TEXT") {
                    return "testtext";
                }
                if (s == "REQ_QTY") {
                    return "quantity";
                }
                if (s == "MATERIAL") {
                    return "material";
                }
                if (s == "COND_VALUE") {
                    return "value";
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

        when(mockHelper.getJCoDestination("ABAP_AS1")).thenReturn(mockJcoDestination);
        when(mockJcoDestination.getRepository()).thenReturn(mockRepository);
        when(mockJcoDestination.getRepository().getFunction("Z_NI9_HDK_STATUS_AUTO_CANCL_LN")).thenReturn(mockJcoFunction);
        when(mockJcoFunction.getExportParameterList()).thenReturn(mockExportParameterListMetaData);
        when(mockExportParameterListMetaData.getString("PO_NBR_X")).thenReturn("000");
        when(mockExportParameterListMetaData.getString("LOC_NBR_X")).thenReturn("001");
        when(mockExportParameterListMetaData.getString("RGA_PFX_IND")).thenReturn("002");
        when(mockExportParameterListMetaData.getString("PROD_STAT_CD_X")).thenReturn("003");
        when(mockExportParameterListMetaData.getString("PROD_STAT_TS_X")).thenReturn("004");
        when(mockExportParameterListMetaData.getString("ERR_MSG_CD")).thenReturn("test error code");
        when(mockExportParameterListMetaData.getString("ERR_MSG_DESC")).thenReturn("test error desc");
        when(mockJcoFunction.getTableParameterList()).thenReturn(mockjCoParameterList);
        when(mockjCoParameterList.getTable("ITEMS_IN")).thenReturn(mockjCoTable);
    }

    @Test
    public void orderCancelCATest() throws IOException {
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        CACancelLnResponseDTO output = orderLineCancelCAService.orderCancelCA(input);
        System.out.println(output.toString());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
    }

    @Test
    public void testOrderCancelCA_whenCallCancelFunction_returnNull() throws IOException {
        test.setErr_msg_desc("System not Available");
        test.setErr_msg_cd("200");
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        doReturn(null).when(orderLineCancelCAService).callCancelFunction(any(CACancelLnRequestDTO.class), anyString());
        doNothing().when(orderLineCancelCAService).localOrderCancel(any(CACancelLnRequestDTO.class), any(CACancelLnResponseDTO.class));
        CACancelLnResponseDTO output = orderLineCancelCAService.orderCancelCA(input);
        assertEquals(output.getErr_msg_desc(), test.getErr_msg_desc());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
    }

    @Test
    public void callCAFunctiontest() throws IOException {
        CACancelLnRequestDTO input = new CACancelLnRequestDTO();
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        CACancelLnResponseDTO output = orderLineCancelCAService.callCancelFunction(input, "ABAP_AS1");
        assertEquals(test.getErr_msg_cd(), output.getErr_msg_cd());
        assertEquals(test.getErr_msg_desc(), output.getErr_msg_desc());

    }

    @Test
    public void testCallCAFunction_whenThrowsException() throws IOException, JCoException {
        CACancelLnRequestDTO input = new CACancelLnRequestDTO();
        when(mockJcoFunction.getImportParameterList()).thenReturn(mockExportParameterListMetaData);
        doThrow(new RuntimeException()).when(mockHelper).getJCoDestination(any());
        assertThrows(IOException.class, () -> orderLineCancelCAService.callCancelFunction(input, "ABAP_AS1"));
    }

    @Test
    public void localOrderCancelTest() {
        CACancelLnResponseDTO output = new CACancelLnResponseDTO();
        RespCancelLnItemCA itemList[] = new RespCancelLnItemCA[1];
        RespCancelLnItemCA item = new RespCancelLnItemCA();
        item.setLine("00");
        item.setMaterial("00");
        item.setQTY("00");
        item.setShorttext("00");
        item.setCost("00");
        item.setShorttext1("00");
        itemList[0] = item;
        output.setLines(itemList);
        orderLineCancelCAService.localOrderCancel(input, output);
        assertEquals(input.getPo_nbr(), output.getPo_nbr());
    }
}
