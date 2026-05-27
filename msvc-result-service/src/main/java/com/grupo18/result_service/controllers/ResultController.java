package com.grupo18.result_service.controllers;

import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.services.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    public ResponseEntity<Result> save(@Valid @RequestBody Result result) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultService.save(result));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Result> findById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.findById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Result> update(@PathVariable Long id, @Valid @RequestBody Result result) {
        return ResponseEntity.ok(resultService.updateById(id, result));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/match/{matchId}")
    public ResponseEntity<Result> findByMatchId(@PathVariable Long matchId) {
        return ResponseEntity.ok(resultService.findByMatchId(matchId));
    }


    @GetMapping("/winner/{teamId}")
    public ResponseEntity<List<Result>> findByWinnerTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(resultService.findByWinnerTeamId(teamId));
    }
}