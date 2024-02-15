package com.example.ordinacija.crud;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.ordinacija.model.Parametri;

public interface ParametriRepository  extends CrudRepository<Parametri, Integer>  {
	@Query("select p.parametarVrednost from Parametri p where p.parametarNaziv = :parametarNaziv")
	Integer nadjiRokOtkazivanja(String parametarNaziv);
} 
