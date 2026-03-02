package com.mpvaitheeswaran.ecommerce.user_service.service;

import com.mpvaitheeswaran.ecommerce.user_service.model.UserAddress;
import com.mpvaitheeswaran.ecommerce.user_service.model.UserProfile;
import com.mpvaitheeswaran.ecommerce.user_service.repository.OrderCheckDao;
import com.mpvaitheeswaran.ecommerce.user_service.repository.UserAddressRepository;
import com.mpvaitheeswaran.ecommerce.user_service.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserProfileRepository profileRepo;
    private final UserAddressRepository addressRepo;
    private final OrderCheckDao orderCheckDao;

    // -----------------------------
    // CREATE PROFILE
    // -----------------------------
    public UserProfile createProfile(UUID authUserId, UserProfile request) {

        if (profileRepo.findByAuthUserId(authUserId).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        request.setId(UUID.randomUUID());
        request.setAuthUserId(authUserId);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        return profileRepo.save(request);
    }

    // -----------------------------
    // UPDATE PROFILE
    // -----------------------------
    public UserProfile updateProfile(UUID profileId, UserProfile updated) {

        UserProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFirstName(updated.getFirstName());
        profile.setLastName(updated.getLastName());
        profile.setPhone(updated.getPhone());
        profile.setPreferredLanguage(updated.getPreferredLanguage());
        profile.setNewsletterSubscribed(updated.isNewsletterSubscribed());
        profile.setUpdatedAt(LocalDateTime.now());

        return profileRepo.save(profile);
    }

    // -----------------------------
    // ADD ADDRESS
    // -----------------------------
    public UserAddress addAddress(UUID userId, UserAddress address) {

        validateAddress(address);

        address.setId(UUID.randomUUID());
        address.setUserId(userId);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        return addressRepo.save(address);
    }

    // -----------------------------
    // SOFT DELETE ACCOUNT
    // -----------------------------
    public void softDeleteAccount(UUID profileId) {

        UserProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long activeOrders = orderCheckDao.countActiveOrders(profile.getId());

        if (activeOrders > 0) {
            throw new RuntimeException("Cannot delete account with active orders");
        }

        profile.setAccountStatus("DELETED");
        profile.setUpdatedAt(LocalDateTime.now());

        profileRepo.save(profile);
    }

    // -----------------------------
    // VALIDATION
    // -----------------------------
    private void validateAddress(UserAddress address) {

        if (address.getStreet() == null ||
                address.getCity() == null ||
                address.getState() == null ||
                address.getCountry() == null ||
                address.getPostalCode() == null) {

            throw new RuntimeException("Mandatory address fields missing");
        }
    }

}
