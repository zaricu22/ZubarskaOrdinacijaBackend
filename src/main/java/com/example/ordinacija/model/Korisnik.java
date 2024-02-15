package com.example.ordinacija.model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the korisnik database table.
 * 
 */
@Entity
@Table(name = "korisnik", schema = "zubarska_ordinacija")
@NamedQuery(name="Korisnik.findAll", query="SELECT k FROM Korisnik k")
public class Korisnik implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String identifikacioniBroj;

	private String ime;

	private String prezime;

	private String tipKorisnika;

	//bi-directional many-to-one association to Pregled
	@OneToMany(mappedBy="korisnik")
	@JsonIgnore
	private List<Termin> pregleds;

	public Korisnik() {
	}

	public Korisnik(String identifikacioniBroj, String ime, String prezime, String tipKorisnika) {
		super();
		this.identifikacioniBroj = identifikacioniBroj;
		this.ime = ime;
		this.prezime = prezime;
		this.tipKorisnika = tipKorisnika;
	}



	public String getIdentifikacioniBroj() {
		return identifikacioniBroj;
	}

	public void setIdentifikacioniBroj(String identifikacioniBroj) {
		this.identifikacioniBroj = identifikacioniBroj;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getTipKorisnika() {
		return tipKorisnika;
	}

	public void setTipKorisnika(String tipKorisnika) {
		this.tipKorisnika = tipKorisnika;
	}

	public List<Termin> getPregleds() {
		return pregleds;
	}

	public void setPregleds(List<Termin> pregleds) {
		this.pregleds = pregleds;
	}

	@Override
	public String toString() {
		return "Korisnik [identifikacioniBroj=" + identifikacioniBroj + ", ime=" + ime + ", prezime=" + prezime
				+ ", tipKorisnika=" + tipKorisnika + "]";
	}

}
