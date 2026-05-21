package com.evia.portal.userportal.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentData implements Serializable {

    @Column(name = "account_owner")
    private String accountOwner;

    @Column(name = "iban")
    @Size(max = 22)
    private String iban;

    @Column(name = "bic")
    @Size(max = 11)
    private String bic;

    @Column(name = "tax_id")
    @Size(max = 11)
    private String taxId;
}
