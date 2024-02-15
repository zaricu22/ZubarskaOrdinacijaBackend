package com.example.ordinacija.crud;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ordinacija.model.Termin;

public interface TerminRepository extends CrudRepository<Termin, String> {
	List<Termin> findAll();
	
	Termin findByTermin(Date termin);
	
	List<Termin> findByTerminBetween(Date datumPocetak, Date datumKraj);
	
	List<Termin> findByKorisnikIdentifikacioniBroj(String id);
	
	List<Termin> findByKorisnikIdentifikacioniBrojAndTerminAfter(String id, Date datum);
	
}
