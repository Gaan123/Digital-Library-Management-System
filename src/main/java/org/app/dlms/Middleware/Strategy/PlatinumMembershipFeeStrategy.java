package org.app.dlms.Middleware.Strategy;

/**
 * Concrete implementation for Platinum membership fee
 */
public class PlatinumMembershipFeeStrategy implements MembershipFeeStrategy {
    @Override
    public double calculateFee() {
        return 350.00; // Platinum membership fee
    }
}