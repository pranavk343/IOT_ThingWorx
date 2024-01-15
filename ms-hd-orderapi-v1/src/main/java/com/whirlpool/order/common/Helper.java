package com.whirlpool.order.common;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import org.springframework.stereotype.Component;

@Component
public class Helper {


    public JCoDestination getJCoDestination(String rfcDestinationName) throws JCoException {
        return JCoDestinationManager.getDestination(rfcDestinationName);
    }

}
