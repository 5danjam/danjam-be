package com.danjam.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.booking.room.dorm.id = :dormId")
    Double findAverageRatingByDormId(Long dormId);
}
