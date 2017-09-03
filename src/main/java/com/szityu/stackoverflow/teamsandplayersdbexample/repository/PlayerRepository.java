package com.szityu.stackoverflow.teamsandplayersdbexample.repository;

import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long>{
}
