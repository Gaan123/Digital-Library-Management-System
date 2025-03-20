package org.app.dlms.Middleware.Strategy;

/**
 * Concrete implementation for Silver membership fee
 */
public class SilverMembershipFeeStrategy implements MembershipFeeStrategy {
    @Override
    public double calculateFee() {
        return 100.00; // Silver membership fee
    }
}



