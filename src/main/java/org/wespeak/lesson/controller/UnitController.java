package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.dto.UnitDetailDto;
import org.wespeak.lesson.service.UnitService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "Unit management endpoints")
public class UnitController {

    private final UnitService unitService;

    @GetMapping("/{unitId}")
    @Operation(summary = "Get unit details", description = "Get a unit with its lessons and user progress")
    public ResponseEntity<UnitDetailDto> getUnit(
            @PathVariable String unitId,
            Principal principal) {
        
        String userId = principal != null ? principal.getName() : null;
        UnitDetailDto response = unitService.findUnitById(unitId, userId);
        return ResponseEntity.ok(response);
    }
}
