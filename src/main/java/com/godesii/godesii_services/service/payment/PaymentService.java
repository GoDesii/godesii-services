package com.godesii.godesii_services.service.payment;

import com.godesii.godesii_services.entity.order.Order;

/**
 * Payment service interface for payment gateway integration
 */
public interface PaymentService {

    /**
     * Initiate payment for an order
     * 
     * @param order         Order to process payment for
     * @param paymentMethod Payment method (UPI, CARD, WALLET, etc.)
     * @return Payment response with payment ID and URL
     */
    PaymentResponse initiatePayment(Order order, String paymentMethod);

    /**
     * Verify payment signature from the payment gateway
     * 
     * @param orderId   Order ID
     * @param paymentId Payment ID from gateway
     * @param signature Payment signature
     * @return true if signature is valid
     */
    boolean verifyPaymentSignature(String orderId, String paymentId, String signature);

    /**
     * Initiate refund for cancelled order
     * 
     * @param order  Order to refund
     * @param reason Refund reason
     * @return Refund ID from payment gateway
     */
    String initiateRefund(Order order, String reason);

    /**
     * Check payment status from gateway
     * 
     * @param paymentId Payment ID
     * @return Payment status (SUCCESS, FAILED, PENDING)
     */
    String checkPaymentStatus(String paymentId);
}
