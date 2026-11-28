package edu.jsp.ecommerce.Request;

import edu.jsp.ecommerce.enums.OrderStatus;
import lombok.Data;

@Data
public class SellerOrderStatusRequest {

    private OrderStatus status;

    private String trackingNumber;
}