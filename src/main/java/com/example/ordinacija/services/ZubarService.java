package com.example.ordinacija.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ordinacija.crud.KorisnikRepository;
import com.example.ordinacija.crud.ParametriRepository;
import com.example.ordinacija.crud.TerminRepository;
import com.example.ordinacija.model.Korisnik;
import com.example.ordinacija.model.Parametri;
import com.example.ordinacija.model.Termin;

@Service
public class ZubarService {
	@Autowired
	private KorisnikRepository korisnikRepository;

	@Autowired
	private TerminRepository terminRepository;
	
	@Autowired
	private ParametriRepository parametriRepository;
	
	public List<Termin> pregledTerminaZubarDan(String korisnikId, String datum) {
		Korisnik nadjeniKorisnik = korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		if (!nadjeniKorisnik.getTipKorisnika().equals("zubar")) {
			return null;
		}
		
		Calendar pocetakRadnogVremenaProsledjenDan = Calendar.getInstance();
		Calendar krajRadnogVremenaProsledjenDan = Calendar.getInstance();
		Date prosledjenDan = new Date();
		List<Termin> termini;
		
		try {
			prosledjenDan = new SimpleDateFormat("yyyy-MM-dd").parse(datum);

			pocetakRadnogVremenaProsledjenDan.setTime(prosledjenDan);
			pocetakRadnogVremenaProsledjenDan.set(Calendar.HOUR_OF_DAY, 9);
			pocetakRadnogVremenaProsledjenDan.set(Calendar.MINUTE, 0);
			pocetakRadnogVremenaProsledjenDan.set(Calendar.SECOND, 0);

			krajRadnogVremenaProsledjenDan.setTime(prosledjenDan);
			krajRadnogVremenaProsledjenDan.set(Calendar.HOUR_OF_DAY, 17);
			krajRadnogVremenaProsledjenDan.set(Calendar.MINUTE, 0);
			krajRadnogVremenaProsledjenDan.set(Calendar.SECOND, 0);

			termini = terminRepository.findByTerminBetween(pocetakRadnogVremenaProsledjenDan.getTime(),
					krajRadnogVremenaProsledjenDan.getTime());
			
			Date tekuciDatum = new Date();
			for (int i = 0; i < termini.size(); i++) {
				if(termini.get(i).getStatus().equals("zakazano") && termini.get(i).getTermin().before(tekuciDatum)) {
					termini.get(i).setStatus("istekao");
				}
				
			}

		} catch (Exception e) {
			return null;
		}

		return termini;
	}
	
	public List<Termin> pregledTerminaZubarPeriod(String korisnikId, String datumPocetak, String datumKraj) {
		Korisnik nadjeniKorisnik = korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		if (!nadjeniKorisnik.getTipKorisnika().equals("zubar")) {
			return null;
		}
		
		Date prosledjenDatumPocetak;
		Date prosledjenDatumKraj;
		List<Termin> termini;
		try {
			prosledjenDatumPocetak = new SimpleDateFormat("yyyy-MM-dd").parse(datumPocetak);
			prosledjenDatumKraj = new SimpleDateFormat("yyyy-MM-dd").parse(datumKraj);
			
			termini = terminRepository.findByTerminBetween(prosledjenDatumPocetak, prosledjenDatumKraj);
			
			Date tekuciDatum = new Date();
			for (int i = 0; i < termini.size(); i++) {
				if(termini.get(i).getStatus().equals("zakazano") && termini.get(i).getTermin().before(tekuciDatum)) {
					termini.get(i).setStatus("istekao");
				}
				
			}
			
			return termini;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String zakaziTermin(String korisnikId, Termin nezakazanTermin) {
		Korisnik nadjeniKorisnik = korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		if (nadjeniKorisnik == null) {
			return "Niste registrovani u bazi korisnika!";
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

		try {
			nezakazanTermin.setKorisnik(nadjeniKorisnik);
			nezakazanTermin.setStatus("zakazano");
			terminRepository.save(nezakazanTermin);
		} catch (Exception e) {
			return "Doslo je do greske!";
		}

		return "Termin uspesno zakazan!";
	}

	public String otkaziTermin(String korisnikId, String datum) {
		Termin nadjeniTermin;
		try {
			nadjeniTermin = terminRepository.findByTermin(new SimpleDateFormat("yyyy-MM-dd").parse(datum));
		} catch (ParseException e1) {
			return "Doslo je do greske!";
		}
		
		int rokOtkazivanja = parametriRepository.nadjiRokOtkazivanja("rokOtkazivanja");

		if (nadjeniTermin == null) {
			return "Prosledjeni termin nije zakazan!";
		}

		if (!(nadjeniTermin.getKorisnik().getIdentifikacioniBroj().equals(korisnikId)
				|| nadjeniTermin.getKorisnik().getTipKorisnika().equals("zubar")))
			return "Nemate ovlascenje da otkazete termin!";

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

	public String promeniRokOtkazivanja(String korisnikId, int noviRok) {
		Korisnik nadjeniKorisnik = korisnikRepository.findByIdentifikacioniBroj(korisnikId);
		if (nadjeniKorisnik == null) {
			return "Niste registrovani u bazi korisnika!";
		} else if (!nadjeniKorisnik.getTipKorisnika().equals("zubar")) {
			return "Samo zubar moze promeniti rok otkazivanja!";
		}

		try {
			parametriRepository.save(new Parametri("rokOtkazivanja", noviRok));
		} catch (Exception e) {
			return "Doslo je do greske!";
		}

		return "Rok otkazivanja uspesno promenjen!";
	}

}
