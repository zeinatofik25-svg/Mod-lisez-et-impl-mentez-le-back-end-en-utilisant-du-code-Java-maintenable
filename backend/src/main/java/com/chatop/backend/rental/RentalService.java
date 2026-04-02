package com.chatop.backend.rental;

import com.chatop.backend.common.dto.MessageResponse;
import com.chatop.backend.exception.ResourceNotFoundException;
import com.chatop.backend.exception.UnauthorizedException;
import com.chatop.backend.rental.dto.RentalResponse;
import com.chatop.backend.rental.dto.RentalsResponse;
import com.chatop.backend.user.User;
import com.chatop.backend.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public RentalsResponse all() {
        List<RentalResponse> rentals = rentalRepository.findAll().stream().map(this::toDto).toList();
        return new RentalsResponse(rentals);
    }

    public RentalResponse detail(Integer id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental", id));
        return toDto(rental);
    }

    public MessageResponse create(String name, String surface, String price, String description, MultipartFile picture) {
        User currentUser = getCurrentUser();
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(new BigDecimal(surface));
        rental.setPrice(new BigDecimal(price));
        rental.setDescription(description);
        rental.setPicture(resolvePictureUrl(picture));
        rental.setOwner(currentUser);
        LocalDateTime now = LocalDateTime.now();
        rental.setCreatedAt(now);
        rental.setUpdatedAt(now);
        rentalRepository.save(rental);
        return new MessageResponse("Rental created !");
    }

    public MessageResponse update(Integer id, String name, String surface, String price, String description) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental", id));
        User currentUser = getCurrentUser();
        if (!rental.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException();
        }

        rental.setName(name);
        rental.setSurface(new BigDecimal(surface));
        rental.setPrice(new BigDecimal(price));
        rental.setDescription(description);
        rental.setUpdatedAt(LocalDateTime.now());
        rentalRepository.save(rental);
        return new MessageResponse("Rental updated !");
    }

    private RentalResponse toDto(Rental rental) {
        return new RentalResponse(
            rental.getId(),
            rental.getName(),
            rental.getSurface().doubleValue(),
            rental.getPrice().doubleValue(),
            rental.getPicture(),
            rental.getDescription(),
            rental.getOwner().getId(),
            rental.getCreatedAt(),
            rental.getUpdatedAt()
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException();
        }
        return userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);
    }

    private String resolvePictureUrl(MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            return "https://images.unsplash.com/photo-1564013799919-ab600027ffc6";
        }
        return "uploads/" + picture.getOriginalFilename();
    }
}
