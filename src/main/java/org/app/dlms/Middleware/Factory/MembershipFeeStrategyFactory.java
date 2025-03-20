package org.app.dlms.Middleware.Factory;

import org.app.dlms.Middleware.Enums.MembershipType;
import org.app.dlms.Middleware.Strategy.*;

/**
 * Factory class to create the appropriate strategy based on membership type
 */
public class MembershipFeeStrategyFactory {
    public static MembershipFeeStrategy createStrategy(MembershipType membershipType) {
        switch (membershipType) {
            case Bronze:
                return new BronzeMembershipFeeStrategy();
            case Silver:
                return new SilverMembershipFeeStrategy();
            case Gold:
                return new GoldMembershipFeeStrategy();
            case Platinum:
                return new PlatinumMembershipFeeStrategy();
            default:
                // Default to Bronze if no match
                return new BronzeMembershipFeeStrategy();
        }
    }
}