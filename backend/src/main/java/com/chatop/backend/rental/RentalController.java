package com.chatop.backend.rental;

import com.chatop.backend.common.dto.MessageResponse;
import com.chatop.backend.rental.dto.RentalResponse;
import com.chatop.backend.rental.dto.RentalsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "Gestion des locations")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    @Operation(summary = "Lister les locations", description = "Retourne la liste des locations")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des locations", content = @Content(schema = @Schema(implementation = RentalsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    public ResponseEntity<RentalsResponse> all() {
        return ResponseEntity.ok(rentalService.all());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une location", description = "Retourne le détail d'une location par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location trouvée", content = @Content(schema = @Schema(implementation = RentalResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "404", description = "Location introuvable")
    })
    public ResponseEntity<RentalResponse> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(rentalService.detail(id));
    }

    @PostMapping
    @Operation(summary = "Créer une location", description = "Crée une location à partir d'un formulaire multipart")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location créée", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    public ResponseEntity<MessageResponse> create(
        @RequestParam String name,
        @RequestParam String surface,
        @RequestParam String price,
        @RequestParam String description,
        @RequestParam(required = false) MultipartFile picture
    ) {
        return ResponseEntity.ok(rentalService.create(name, surface, price, description, picture));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une location", description = "Met à jour une location existante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location mise à jour", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "404", description = "Location introuvable")
    })
    public ResponseEntity<MessageResponse> update(
        @PathVariable Integer id,
        @RequestParam String name,
        @RequestParam String surface,
        @RequestParam String price,
        @RequestParam String description
    ) {
        return ResponseEntity.ok(rentalService.update(id, name, surface, price, description));
    }
}
