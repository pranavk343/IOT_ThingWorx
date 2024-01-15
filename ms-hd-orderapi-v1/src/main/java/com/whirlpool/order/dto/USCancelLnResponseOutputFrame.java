package com.whirlpool.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class USCancelLnResponseOutputFrame {


   public List<USCancelLnResponseDTO> CancelResponse;

   public String error ;
}
