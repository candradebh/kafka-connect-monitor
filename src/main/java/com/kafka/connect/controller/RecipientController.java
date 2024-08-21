package com.kafka.connect.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.RecipientEntity;
import com.kafka.connect.repository.RecipientRepository;

@RestController
@RequestMapping("/recipients")
public class RecipientController
{

    @Autowired
    private RecipientRepository recipientRepository;

    @GetMapping
    public List<RecipientEntity> getAllRecipients()
    {
        return recipientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientEntity> getRecipientById(@PathVariable Long id)
    {
        Optional<RecipientEntity> entityOptional = recipientRepository.findById(id);
        if (entityOptional.isPresent())
        {
            return ResponseEntity.ok(entityOptional.get());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public RecipientEntity createRecipient(@RequestBody RecipientEntity recipient)
    {
        return recipientRepository.save(recipient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipientEntity> updateRecipient(@PathVariable Long id, @RequestBody RecipientEntity recipientDetails)
    {
        Optional<RecipientEntity> optionalRecipient = recipientRepository.findById(id);
        if (optionalRecipient.isPresent())
        {
            RecipientEntity recipient = optionalRecipient.get();
            recipient.setName(recipientDetails.getName());
            recipient.setEmail(recipientDetails.getEmail());
            recipient.setIsActive(recipientDetails.getIsActive());

            final RecipientEntity updatedRecipient = recipientRepository.save(recipient);
            return ResponseEntity.ok(updatedRecipient);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id)
    {
        Optional<RecipientEntity> recipient = recipientRepository.findById(id);
        if (recipient.isPresent())
        {
            recipientRepository.delete(recipient.get());
            return ResponseEntity.ok().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
