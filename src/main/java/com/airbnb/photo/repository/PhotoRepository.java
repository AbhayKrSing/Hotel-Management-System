package com.airbnb.photo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airbnb.photo.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
   
}
