package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.PaymentData;
import com.evia.portal.userportal.core.dto.PaymentDataDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentDataMapperTest {

    private static final String ACCOUNT_OWNER = "John Doe";
    private static final String IBAN = "1234567890123456789012";
    private static final String BIC = "12345678901";
    private static final String TAX_ID = "12345678901";
    @Autowired
    private PaymentDataMapper paymentDataMapper;

    @Test
    void toPaymentData() {

        final PaymentDataDTO paymentDataDTO = PaymentDataDTO.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final PaymentData expextedPaymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();


        final PaymentData paymentData = paymentDataMapper.toPaymentData(paymentDataDTO);

        assertThat(paymentData).usingRecursiveComparison().isEqualTo(expextedPaymentData);
    }

    @Test
    void toPaymentDataDTO() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final PaymentDataDTO expextedPaymentDataDTO = PaymentDataDTO.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();


        final PaymentDataDTO paymentDataDTO = paymentDataMapper.toPaymentDataDTO(paymentData);

        assertThat(paymentDataDTO).usingRecursiveComparison().isEqualTo(expextedPaymentDataDTO);
    }
}
