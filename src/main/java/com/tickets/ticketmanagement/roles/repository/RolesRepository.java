package com.tickets.ticketmanagement.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.roles.entity.RolesEntity;

@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Long>{

}
