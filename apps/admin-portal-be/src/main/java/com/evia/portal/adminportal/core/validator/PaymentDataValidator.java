package com.evia.portal.adminportal.core.validator;

import com.evia.portal.adminportal.core.domain.PaymentData;
import com.evia.portal.adminportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PaymentDataValidator {

  private static final int IBAN_LENGTH = 22;
  private static final int BIC_LENGTH = 11;
  private static final int TAX_ID_MIN_LENGTH = 8;
  private static final int TAX_ID_MAX_LENGTH = 11;
  private PaymentDataValidator() {

  }

  public static List<String> validatePaymentData(PaymentData paymentData) {

    List<String> errors = new ArrayList<>();

    if (!isPaymentDataEmpty(paymentData)) {

      validateAccountOwner(paymentData.getAccountOwner(), errors);
      validateIban(paymentData.getIban(), errors);
      validateBic(paymentData.getBic(), errors);
      validateTaxId(paymentData.getTaxId(), errors);
    }
    return errors;
  }

  private static void validateAccountOwner(String accountOwner, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(accountOwner) || accountOwner.length() > 255) {
      errors.add("Please fill in a valid account owner");
    }
  }

  private static void validateIban(String iban, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(iban) || iban.length() != IBAN_LENGTH) {
      errors.add("Please fill in a valid IBAN");
    }
  }

  private static void validateBic(String bic, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(bic) || bic.length() != BIC_LENGTH) {
      errors.add("Please fill in a valid bic");
    }
  }

  private static void validateTaxId(String taxId, List<String> errors) {

    if (MethodUtil.isNullOrEmpty(taxId) || taxId.length() < TAX_ID_MIN_LENGTH || taxId.length() > TAX_ID_MAX_LENGTH) {
      errors.add("Please fill in a valid tax id");
    }
  }

  private static boolean isPaymentDataEmpty(PaymentData paymentData) {

    if (paymentData == null) {
      return true;
    }

    return Stream.of(paymentData.getAccountOwner(),
      paymentData.getIban(),
      paymentData.getBic(),
      paymentData.getTaxId()).allMatch(Objects::isNull);
  }
}
