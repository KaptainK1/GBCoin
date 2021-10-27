package com.uwgb.GBCoin.API.Repositories;

import com.uwgb.GBCoin.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}