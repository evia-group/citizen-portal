package com.evia.portal.userportal.core.validator;

import com.evia.portal.userportal.core.domain.PaymentData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentDataValidatorTest {

    private static final String ACCOUNT_OWNER = "John Doe";
    private static final String IBAN = "1234567890123456789012";
    private static final String BIC = "12345678901";
    private static final String TAX_ID = "12345678901";
    private static final String WRONG_TO_SHORT_IBAN = "123456789012345678901";
    private static final String WRONG_TO_LONG_IBAN = "12345678901234567890123";
    private static final String WRONG_TO_SHORT_BIC = "1234567890";
    private static final String WRONG_TO_LONG_BIC = "123456789012";
    private static final String WRONG_TO_SHORT_TAX_ID = "1234567";
    private static final String WRONG_TO_LONG_TAX_ID = "123456789012";
    private static final String ERROR_ACCOUNT_OWNER = "Please fill in a valid account owner";
    private static final String ERROR_IBAN = "Please fill in a valid IBAN";
    private static final String ERROR_BIC = "Please fill in a valid bic";
    private static final String ERROR_TAXID = "Please fill in a valid tax id";

    @Test
    void validatePaymentData_ReturnCorrectValidation() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isEmpty();
    }

    @Test
    void validateWrongAccountOwnerEmpty_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner("")
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_ACCOUNT_OWNER);
    }

    @Test
    void validateWrongAccountOwnerNull_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(null)
            .iban(IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_ACCOUNT_OWNER);
    }

    @Test
    void validateWrongIbanToShort_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(WRONG_TO_SHORT_IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_IBAN);
    }

    @Test
    void validateWrongIbanToLong_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(WRONG_TO_LONG_IBAN)
            .bic(BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_IBAN);
    }

    @Test
    void validateWrongBicToShort_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(WRONG_TO_SHORT_BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_BIC);
    }

    @Test
    void validateWrongBicToLong_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(WRONG_TO_LONG_BIC)
            .taxId(TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_BIC);
    }

    @Test
    void validateWrongTaxIdToLong_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(WRONG_TO_LONG_TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_TAXID);
    }

    @Test
    void validateWrongTaxIdToShort_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(ACCOUNT_OWNER)
            .iban(IBAN)
            .bic(BIC)
            .taxId(WRONG_TO_SHORT_TAX_ID)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isNotEmpty();
        assertThat(errors.getFirst()).isEqualTo(ERROR_TAXID);
    }

    @Test
    void validateAllEmpty_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner("")
            .iban("")
            .bic("")
            .taxId("")
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).hasSize(4);
    }

    @Test
    void validateOneNull_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner("")
            .iban("")
            .bic("")
            .taxId(null)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).hasSize(4);
    }

    @Test
    void validateAllButOneNull_ReturnValidationNegative() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(null)
            .iban(null)
            .bic("")
            .taxId(null)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).hasSize(4);
    }

    @Test
    void validateAllNull_ReturnValidationPositive() {

        final PaymentData paymentData = PaymentData.builder()
            .accountOwner(null)
            .iban(null)
            .bic(null)
            .taxId(null)
            .build();

        final List<String> errors = PaymentDataValidator.validatePaymentData(paymentData);

        assertThat(errors).isEmpty();
    }

}
