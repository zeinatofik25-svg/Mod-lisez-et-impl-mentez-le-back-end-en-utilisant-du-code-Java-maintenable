package com.chatop.backend.message;

import com.chatop.backend.common.dto.MessageResponse;
import com.chatop.backend.exception.ResourceNotFoundException;
import com.chatop.backend.message.dto.CreateMessageRequest;
import com.chatop.backend.rental.Rental;
import com.chatop.backend.rental.RentalRepository;
import com.chatop.backend.user.User;
import com.chatop.backend.user.UserRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse send(CreateMessageRequest request) {
        Rental rental = rentalRepository.findById(request.rentalId())
            .orElseThrow(() -> new ResourceNotFoundException("Rental", request.rentalId()));
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));

        Message message = new Message();
        message.setRental(rental);
        message.setUser(user);
        message.setMessage(request.message());
        LocalDateTime now = LocalDateTime.now();
        message.setCreatedAt(now);
        message.setUpdatedAt(now);
        messageRepository.save(message);

        return new MessageResponse("Message send with success");
    }
}
