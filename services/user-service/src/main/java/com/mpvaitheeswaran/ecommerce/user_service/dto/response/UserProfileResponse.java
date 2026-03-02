package com.mpvaitheeswaran.ecommerce.user_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserProfileResponse {

    private UUID id;
    private UUID authUserId;
    private String firstName;
    private String lastName;
    private String phone;
    private String preferredLanguage;
    private Long loyaltyPoints;
    private String accountStatus;
}
