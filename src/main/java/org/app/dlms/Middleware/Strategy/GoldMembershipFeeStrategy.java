package org.app.dlms.Middleware.Strategy;

/**
 * Concrete implementation for Gold membership fee
 */
public class GoldMembershipFeeStrategy implements MembershipFeeStrategy {
    @Override
    public double calculateFee() {
        return 200.00; // Gold membership fee
    }
}