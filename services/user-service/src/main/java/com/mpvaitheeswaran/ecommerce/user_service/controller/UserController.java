package com.mpvaitheeswaran.ecommerce.user_service.controller;

import com.mpvaitheeswaran.ecommerce.user_service.dto.request.AddressRequest;
import com.mpvaitheeswaran.ecommerce.user_service.dto.request.UserProfileRequest;
import com.mpvaitheeswaran.ecommerce.user_service.dto.response.UserProfileResponse;
import com.mpvaitheeswaran.ecommerce.user_service.model.UserAddress;
import com.mpvaitheeswaran.ecommerce.user_service.model.UserProfile;
import com.mpvaitheeswaran.ecommerce.user_service.repository.UserAddressRepository;
import com.mpvaitheeswaran.ecommerce.user_service.repository.UserProfileRepository;
import com.mpvaitheeswaran.ecommerce.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserAddressRepository addressRepository;
    private final UserProfileRepository profileRepository;

    // ------------------------------------------------
    // GET ALL USERS (Admin Use)
    // ------------------------------------------------
    @GetMapping
    public List<UserProfileResponse> getUsers() {

        return profileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ------------------------------------------------
    // GET USER BY ID
    // ------------------------------------------------
    @GetMapping("/{id}")
    public UserProfileResponse getUserById(@PathVariable UUID id) {

        UserProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(profile);
    }

    // ------------------------------------------------
    // CREATE USER PROFILE
    // ------------------------------------------------
    @PostMapping
    public ResponseEntity<UserProfileResponse> createUser(
            @RequestParam UUID authUserId,
            @Valid @RequestBody UserProfileRequest request) {

        UserProfile profile = new UserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setPreferredLanguage(request.getPreferredLanguage());
        profile.setNewsletterSubscribed(request.getNewsletterSubscribed());

        UserProfile saved =
                userProfileService.createProfile(authUserId, profile);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    // ------------------------------------------------
    // UPDATE USER BY ID
    // ------------------------------------------------
    @PutMapping("/{id}")
    public UserProfileResponse updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserProfileRequest request) {

        UserProfile updated = new UserProfile();
        updated.setFirstName(request.getFirstName());
        updated.setLastName(request.getLastName());
        updated.setPhone(request.getPhone());
        updated.setPreferredLanguage(request.getPreferredLanguage());
        updated.setNewsletterSubscribed(request.getNewsletterSubscribed());

        return mapToResponse(
                userProfileService.updateProfile(id, updated)
        );
    }

    // ------------------------------------------------
    // GET USER ADDRESSES
    // ------------------------------------------------
    @GetMapping("/{id}/addresses")
    public List<UserAddress> getUserAddressesById(@PathVariable UUID id) {
        return addressRepository.findByUserId(id);
    }

    // ------------------------------------------------
    // ADD ADDRESS
    // ------------------------------------------------
    @PostMapping("/{id}/addresses")
    public UserAddress addAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request) {

        UserAddress address = new UserAddress();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setAddressType(request.getAddressType());

        return userProfileService.addAddress(id, address);
    }

    // ------------------------------------------------
    // GET CURRENT USER (FROM JWT)
    // ------------------------------------------------
    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(
            Authentication authentication) {

        UUID authUserId = UUID.fromString(authentication.getName());

        UserProfile profile = profileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile);
    }

    // ------------------------------------------------
    // UPDATE CURRENT USER
    // ------------------------------------------------
    @PutMapping("/me")
    public UserProfileResponse updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserProfileRequest request) {

        UUID authUserId = UUID.fromString(authentication.getName());

        UserProfile profile = profileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        UserProfile updated = new UserProfile();
        updated.setFirstName(request.getFirstName());
        updated.setLastName(request.getLastName());
        updated.setPhone(request.getPhone());
        updated.setPreferredLanguage(request.getPreferredLanguage());
        updated.setNewsletterSubscribed(request.getNewsletterSubscribed());

        return mapToResponse(
                userProfileService.updateProfile(profile.getId(), updated)
        );
    }

    // ------------------------------------------------
    // MAPPER
    // ------------------------------------------------
    private UserProfileResponse mapToResponse(UserProfile profile) {

        return UserProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .preferredLanguage(profile.getPreferredLanguage())
                .loyaltyPoints(profile.getLoyaltyPoints())
                .accountStatus(profile.getAccountStatus())
                .build();
    }

    @GetMapping("/me/addresses")
    public List<UserAddress> getCurrentUserAddresses(Authentication authentication) {

        UUID authUserId = UUID.fromString(authentication.getName());

        UserProfile profile = profileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return addressRepository.findByUserId(profile.getId());
    }

    @GetMapping("/me/addresses/default")
    public UserAddress getCurrentUserDefaultAddress(Authentication authentication) {

        UUID authUserId = UUID.fromString(authentication.getName());

        UserProfile profile = profileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return addressRepository
                .findByUserIdAndIsDefaultTrue(profile.getId())
                .orElseThrow(() -> new RuntimeException("Default address not found"));
    }


}
