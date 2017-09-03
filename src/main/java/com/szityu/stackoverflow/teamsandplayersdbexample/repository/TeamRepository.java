package com.szityu.stackoverflow.teamsandplayersdbexample.repository;

import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long>{
}
