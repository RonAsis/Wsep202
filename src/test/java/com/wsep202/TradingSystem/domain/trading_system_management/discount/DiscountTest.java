package com.wsep202.TradingSystem.domain.trading_system_management.discount;

import com.wsep202.TradingSystem.domain.exception.*;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.CompositeOperator;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.ConditionalStoreDiscount;
import com.wsep202.TradingSystem.domain.trading_system_management.discount.Discount;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.Purchase;
import com.wsep202.TradingSystem.domain.trading_system_management.policy_purchase.PurchasePolicy;
import com.wsep202.TradingSystem.domain.trading_system_management.purchase.BillingAddress;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class DiscountTest {

    //the discounts types
    VisibleDiscount visibleClass = new VisibleDiscount();


    private Discount visDiscount;
    //conditional discounts
    private Discount condStoreDiscount;
    private Discount condProductDiscount;
    private Discount composedDiscount;

    @AfterEach
    void tearDown() {
    }



    @Nested
    public class DiscountTestUnit{
        @BeforeEach
        void setUp() {
            //far end time
            Calendar endTime = Calendar.getInstance();
            endTime.set(3000,1,1);

            //visible discount
            visDiscount = Discount.builder()
                    .endTime(endTime)
                    .discountPercentage(100)
                    .isStoreDiscount(false)
                    .build();
        }



        @Test
        void applyDiscountVisiblePositive() {

        }

        @Test
        void isApprovedProducts() {
        }

        @Test
        void applyConditionalDiscount() {
        }

        @Test
        void editDiscount() {
        }

        @Test
        void getDiscountIdAcc() {
        }

        @Test
        void setNewId() {
        }

        @Test
        void isExpired() {
        }
    }

    @Nested
    public class DiscountTestIntegration {

        @BeforeEach
        void setUp() {

        }



        @Test
        void applyDiscount() {
        }

        @Test
        void isApprovedProducts() {
        }

        @Test
        void applyConditionalDiscount() {
        }

        @Test
        void editDiscount() {
        }

        @Test
        void getDiscountIdAcc() {
        }

        @Test
        void setNewId() {
        }

        @Test
        void isExpired() {
        }
    }

}
