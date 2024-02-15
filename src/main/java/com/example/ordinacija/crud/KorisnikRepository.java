package com.example.ordinacija.crud;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ordinacija.model.Korisnik;

public interface KorisnikRepository extends CrudRepository<Korisnik, Integer> {
	List<Korisnik> findAll();
	Korisnik findByIdentifikacioniBroj(String id);
	Korisnik findByIdentifikacioniBrojAndTipKorisnika(String id, String tipKorisnika);
}
