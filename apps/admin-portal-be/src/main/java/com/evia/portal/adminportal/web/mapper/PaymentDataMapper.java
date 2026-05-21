package com.evia.portal.adminportal.web.mapper;

import com.evia.portal.adminportal.core.domain.PaymentData;
import com.evia.portal.adminportal.core.dto.PaymentDataDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDataMapper {

  PaymentData toPaymentData(PaymentDataDTO paymentDataDTO);

  PaymentDataDTO toPaymentDataDTO(PaymentData paymentData);
}
