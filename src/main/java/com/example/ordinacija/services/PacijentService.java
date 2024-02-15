package com.example.ordinacija.services;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ordinacija.crud.KorisnikRepository;
import com.example.ordinacija.crud.ParametriRepository;
import com.example.ordinacija.crud.TerminRepository;
import com.example.ordinacija.model.Korisnik;
import com.example.ordinacija.model.Termin;

@Service
public class PacijentService {
	@Autowired
	private KorisnikRepository korisnikRepository;

	@Autowired
	private TerminRepository terminRepository;
	
	@Autowired
	private ParametriRepository parametriRepository;
	
	@Autowired
	private PrijavaService prijavaService;
	
	public List<Termin> pregledTerminaPacijent(String korisnikId) {
		List<Termin> termini;
		try {
			termini = terminRepository.findByKorisnikIdentifikacioniBroj(korisnikId);
		} catch (Exception e) {
			return null;
		}

		return termini;
	}
	
	public List<Termin> pregledNeisteklihTerminaPacijent(String korisnikId) {
		List<Termin> termini;
		try {
			termini = terminRepository.findByKorisnikIdentifikacioniBrojAndTerminAfter(korisnikId, new Date());
		} catch (Exception e) {
			return null;
		}

		return termini;
	}
	
	public String zakazivanjeTermina(String prijavljenKorisnikId, Termin nezakazanTermin) {
		if(nezakazanTermin.getKorisnik() == null 
				|| nezakazanTermin.getKorisnik().getIdentifikacioniBroj() == null
				|| nezakazanTermin.getKorisnik().getIdentifikacioniBroj().isEmpty()
				|| nezakazanTermin.getTipPregleda() == null
				|| nezakazanTermin.getTipPregleda().isEmpty()) {
			return "Neispravan unos termina!";
		}
		
		Korisnik prijavljenKorisnik = korisnikRepository.findByIdentifikacioniBroj(prijavljenKorisnikId);
		if (prijavljenKorisnik == null) {
			return "Niste registrovani u bazi korisnika!";
		} else if (!prijavljenKorisnikId.equals(nezakazanTermin.getKorisnik().getIdentifikacioniBroj())
				&& !prijavljenKorisnik.getTipKorisnika().equals("zubar")) {
			return "Niste ovlasceni da zakazujete termine za druga lica!";
		}

		Calendar nezakazanTerminPocetak = Calendar.getInstance();
		nezakazanTerminPocetak.setTime(nezakazanTermin.getTermin());

		Calendar nezakazanTerminKraj = Calendar.getInstance();
		nezakazanTerminKraj.setTime(nezakazanTermin.getTermin());
		nezakazanTerminKraj.add(Calendar.MINUTE, nezakazanTermin.getTrajanje());

		Calendar pocetakRadnogVremenaProsledjenDan = Calendar.getInstance();
		pocetakRadnogVremenaProsledjenDan.setTime(nezakazanTermin.getTermin());
		pocetakRadnogVremenaProsledjenDan.set(Calendar.HOUR_OF_DAY, 9);
		pocetakRadnogVremenaProsledjenDan.set(Calendar.MINUTE, 0);
		pocetakRadnogVremenaProsledjenDan.set(Calendar.SECOND, 0);

		Calendar krajRadnogVremenaProsledjenDan = Calendar.getInstance();
		krajRadnogVremenaProsledjenDan.setTime(nezakazanTermin.getTermin());
		krajRadnogVremenaProsledjenDan.set(Calendar.HOUR_OF_DAY, 17);
		krajRadnogVremenaProsledjenDan.set(Calendar.MINUTE, 0);
		krajRadnogVremenaProsledjenDan.set(Calendar.SECOND, 0);
		
		if (nezakazanTermin.getTrajanje() != 30 && nezakazanTermin.getTrajanje() != 60
				|| nezakazanTerminPocetak.get(Calendar.MINUTE) != 0 && nezakazanTerminPocetak.get(Calendar.MINUTE) != 30
				|| nezakazanTerminPocetak.compareTo(pocetakRadnogVremenaProsledjenDan) < 0
				|| nezakazanTerminPocetak.compareTo(krajRadnogVremenaProsledjenDan) > 0
				|| nezakazanTerminKraj.compareTo(krajRadnogVremenaProsledjenDan) > 0) {

			return "Odaberite ispravan termin!";
		}

		List<Termin> zauzetiTerminiProsledjenDan = terminRepository.findByTerminBetween(
				pocetakRadnogVremenaProsledjenDan.getTime(), krajRadnogVremenaProsledjenDan.getTime());
		if (zauzetiTerminiProsledjenDan.size() > 0) {
			Calendar zauzetTerminPocetak = Calendar.getInstance();
			Calendar zauzetTerminKraj = Calendar.getInstance();
			for (Termin zakazanTermin : zauzetiTerminiProsledjenDan) {
				zauzetTerminPocetak.setTime(zakazanTermin.getTermin());
				zauzetTerminKraj.setTime(zakazanTermin.getTermin());
				zauzetTerminKraj.add(Calendar.MINUTE, zakazanTermin.getTrajanje());

				if (nezakazanTerminPocetak.compareTo(zauzetTerminPocetak) >= 0
						&& nezakazanTerminPocetak.compareTo(zauzetTerminKraj) < 0
						|| nezakazanTerminKraj.compareTo(zauzetTerminPocetak) > 0
								&& nezakazanTerminKraj.compareTo(zauzetTerminKraj) <= 0) {
					return "Termin zauzet!";
				}
			}
		}

		Korisnik korisnikTermina;
		try {
			korisnikTermina = korisnikRepository.findByIdentifikacioniBroj(nezakazanTermin.getKorisnik().getIdentifikacioniBroj());
			if (korisnikTermina == null) {
				if(nezakazanTermin.getKorisnik().getIme() == null || nezakazanTermin.getKorisnik().getPrezime() == null 
						|| nezakazanTermin.getKorisnik().getIme().isEmpty() 
						|| nezakazanTermin.getKorisnik().getPrezime().isEmpty()) {
					return "Unesite ime i prezime novog korisnika";
					
				}
				prijavaService.korisnikUnos(nezakazanTermin.getKorisnik());
			}
			korisnikTermina = korisnikRepository.findByIdentifikacioniBroj(nezakazanTermin.getKorisnik().getIdentifikacioniBroj());
			nezakazanTermin.setKorisnik(korisnikTermina);
			nezakazanTermin.setStatus("zakazano");
			terminRepository.save(nezakazanTermin);
		} catch (Exception e) {
			return "Doslo je do greske!";
		}

		return "Termin uspesno zakazan!";
	}

	public String otkaziTermin(String korisnikId, String datum) {
		Termin nadjeniTermin;
		int rokOtkazivanja;
		Korisnik korisnik;
		
		try {
			nadjeniTermin = terminRepository.findByTermin(Date.from(OffsetDateTime.parse(datum).toInstant()));
			rokOtkazivanja = parametriRepository.nadjiRokOtkazivanja("rokOtkazivanja");
			korisnik = korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		} catch (Exception e) {
			return "Doslo je do greske!";
		}

		if (nadjeniTermin == null) {
			return "Prosledjeni termin nije zakazan!";
		}

		if (!nadjeniTermin.getKorisnik().getIdentifikacioniBroj().equals(korisnikId)
				&& korisnik != null && !korisnik.getTipKorisnika().equals("zubar")) {
			return "Nemate ovlascenje da otkazete termin!";
		}

		if (nadjeniTermin.getStatus().contains("otkazan"))
			return "Termin je vec otkazan!";

		Calendar tekuceVremePlusRok = Calendar.getInstance();
		tekuceVremePlusRok.add(Calendar.HOUR_OF_DAY, rokOtkazivanja);
		if (!nadjeniTermin.getTermin().after(tekuceVremePlusRok.getTime()))
			return "Termin se mora otkazati "+rokOtkazivanja+"h ranije!";

		try {
			nadjeniTermin.setStatus("otkazano");
			terminRepository.save(nadjeniTermin);
		} catch (Exception e) {
			return "Doslo je do greske!";
		}

		return "Termin uspesno otkazan!";
	}

}
