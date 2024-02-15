package com.example.ordinacija.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ordinacija.crud.KorisnikRepository;
import com.example.ordinacija.model.Korisnik;

@Service
public class PrijavaService {
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	public Korisnik korisnikPrijava(String korisnikId) {
		try {
			return korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Korisnik proveraTipaKorisnika(String korisnikId, String tipKorisnika) {
		try {
			return korisnikRepository.findByIdentifikacioniBrojAndTipKorisnika(korisnikId, tipKorisnika);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String korisnikUnos(Korisnik korisnik) {
		if(korisnik == null || 
				korisnik.getIdentifikacioniBroj() == null || 
				korisnik.getIme() == null || 
				korisnik.getPrezime() == null || 
				korisnik.getIme().isEmpty() || 
				korisnik.getPrezime().isEmpty()) 
			return "Neispravan unos korisnika!";
		
		try {
			if(korisnikRepository.findByIdentifikacioniBroj(korisnik.getIdentifikacioniBroj()) != null)
				return "Korisnik vec postoji u bazi!";
			korisnik.setTipKorisnika("pacijent");
			korisnikRepository.save(korisnik);
			return "Korisnik uspesno unesen u bazu!";
		} catch (Exception e) {
			return "Doslo je do greske!";
		}
		
	}
}
