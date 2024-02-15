package com.example.ordinacija.controller;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ordinacija.model.Korisnik;
import com.example.ordinacija.model.Termin;
import com.example.ordinacija.services.PacijentService;
import com.example.ordinacija.services.PrijavaService;
import com.example.ordinacija.services.ZubarService;

@CrossOrigin
@RestController
public class OrdinacijaController {
	@Autowired
	ZubarService zubarService;
	
	@Autowired
	PacijentService pacijentService;
	
	@Autowired
	PrijavaService prijavaService;
	
	@GetMapping(value = "/korisnikProvera/{korisnikId}")
	public Korisnik korisnikPrijava(@PathVariable String korisnikId, HttpServletResponse response) throws IOException {
		
		Korisnik korisnik = prijavaService.korisnikPrijava(korisnikId);
		if(korisnik != null) {
		    return korisnik;
		} else {
			response.sendError(401, "Korisnik nije pronadjen!");
			return null; 
		}
		
	}
	
	@GetMapping(value = "/proveraTipaKorisnika/{korisnikId}/{tipKorisnika}")
	public boolean proveraTipaKorisnika(@PathVariable String korisnikId, @PathVariable String tipKorisnika, HttpServletResponse response) throws IOException {
		
		Korisnik korisnik = prijavaService.proveraTipaKorisnika(korisnikId, tipKorisnika);
		if(korisnik != null) 
		    return true;
		else 
			return false; 
		
	}
	
	@PostMapping(value = "/korisnikUnos")
	public void korisnikUnos(@RequestBody Korisnik korisnik, HttpServletResponse response) throws IOException {
		if(korisnik.getIdentifikacioniBroj().equals("")) {
			response.sendError(400, "Neispravan unos korisnika!");
			return;
		}
		
		String poruka = prijavaService.korisnikUnos(korisnik);
		
		if(poruka.contains("gresk")) {
			response.sendError(500, poruka);
		}
		else if(!poruka.contains("uspesn")) {
			response.sendError(400, poruka);
		}
		else {
			Cookie cookie = new Cookie("id", korisnik.getIdentifikacioniBroj());
		    response.addCookie(cookie);
		}
	} 
	
	@GetMapping(value = "/pregledSvihTerminaPacijent") 
	public List<Termin> pregledTerminaPacijent(@RequestHeader("id") String korisnikId, HttpServletResponse response) {		
		
		List<Termin> listaTermina = pacijentService.pregledTerminaPacijent(korisnikId);
		if(listaTermina == null)
			response.setStatus(204);
		
		return listaTermina;
	}
	
	@GetMapping(value = "/pregledNeisteklihTerminaPacijent") 
	public List<Termin> pregledNeisteklihTerminaPacijent(@RequestHeader("id") String korisnikId, HttpServletResponse response) {		
		
		List<Termin> listaTermina = pacijentService.pregledNeisteklihTerminaPacijent(korisnikId);
		if(listaTermina == null)
			response.setStatus(204);
		
		return listaTermina;
	}
	
	@GetMapping(value = "/pregledTerminaZubarDan/{datum}")
	public List<Termin> pregledTerminaZubarDan(@RequestHeader("id") String korisnikId, @PathVariable String datum, HttpServletResponse response) {
		
		List<Termin> listaTermina = zubarService.pregledTerminaZubarDan(korisnikId, datum); // yyyy-MM-dd
		if(listaTermina.isEmpty())
			response.setStatus(204);
		
		return listaTermina;
	}
	
	@GetMapping(value = "/pregledTerminaZubarPeriod")
	public List<Termin> pregledTerminaZubarPeriod(@RequestHeader("id") String korisnikId, @RequestParam("datumPocetak") String datumPocetak, @RequestParam("datumKraj") String datumKraj, HttpServletResponse response) {
		List<Termin> listaTermina = zubarService.pregledTerminaZubarPeriod(korisnikId, datumPocetak, datumKraj);

		if(listaTermina.isEmpty())
			response.setStatus(204);
		
		return listaTermina;
	}

	@PostMapping(value = "/zakazivanjeTermina")
	public String zakaziTermin(@RequestHeader("id") String prijavljenKorisnikId, @RequestBody Termin nezakazanPregled, HttpServletResponse response) {
		String poruka = pacijentService.zakazivanjeTermina(prijavljenKorisnikId, nezakazanPregled);
		if(!poruka.contains("uspesn"))
			response.setStatus(400);
		if(poruka.contains("gresk"))
			response.setStatus(500);
		
		return poruka;
	}

	@PutMapping(value = "/otkazivanjeTermina/{datum}")
	public String otkazivanjeTermina(@RequestHeader("id") String korisnikId, @PathVariable String datum, HttpServletResponse response) {

		String poruka = pacijentService.otkaziTermin(korisnikId, datum);
		if(!poruka.contains("uspesn"))
			response.setStatus(400);
		if(poruka.contains("gresk"))
			response.setStatus(500);
		
		return poruka;
	}

	@PutMapping(value = "/promeniRokOtkazivanja/{noviRok}")
	public String promeniRokOtkazivanja(@RequestHeader("id") String korisnikId, @PathVariable int noviRok, HttpServletResponse response) {
		String poruka = zubarService.promeniRokOtkazivanja(korisnikId, noviRok);
		if(!poruka.contains("uspesn")) {
			response.setStatus(400);
		}
		if(poruka.contains("gresk"))
			response.setStatus(500);
		
		return poruka;
	}

}
