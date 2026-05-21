package com.evia.portal.userportal.web.mapper;

import com.evia.portal.userportal.core.domain.PaymentData;
import com.evia.portal.userportal.core.dto.PaymentDataDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDataMapper {

    PaymentData toPaymentData(PaymentDataDTO paymentDataDTO);

    PaymentDataDTO toPaymentDataDTO(PaymentData paymentData);
}
