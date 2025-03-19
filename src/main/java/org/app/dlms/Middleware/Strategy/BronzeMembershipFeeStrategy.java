package org.app.dlms.Middleware.Strategy;

/**
 * Concrete implementation for Bronze membership fee
 */
public class BronzeMembershipFeeStrategy implements MembershipFeeStrategy {
    @Override
    public double calculateFee() {
        return 50.00; // Bronze membership fee
    }
}
