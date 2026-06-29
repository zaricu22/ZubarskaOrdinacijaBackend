package com.example.ordinacija.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "termin", schema = "public")
@NamedQuery(name="Termin.findAll", query="SELECT p FROM Termin p")
public class Termin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String tipPregleda;

	private String status;

	private Integer trajanje;

	@Temporal(TemporalType.TIMESTAMP)
	private Date termin;

	@ManyToOne
	@JoinColumn(name="korisnik_id", referencedColumnName="identifikacioniBroj")
	private Korisnik korisnik;

	public Termin() {
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getTipPregleda() { return tipPregleda; }
	public void setTipPregleda(String tipPregleda) { this.tipPregleda = tipPregleda; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public Integer getTrajanje() { return trajanje; }
	public void setTrajanje(Integer trajanje) { this.trajanje = trajanje; }
	public Date getTermin() { return termin; }
	public void setTermin(Date termin) { this.termin = termin; }
	public Korisnik getKorisnik() { return korisnik; }
	public void setKorisnik(Korisnik korisnik) { this.korisnik = korisnik; }

	@Override
	public String toString() {
		return "Termin [id=" + id + ", tipPregleda=" + tipPregleda + ", status=" + status + ", trajanje=" + trajanje + ", termin=" + termin + ", korisnik=" + korisnik + "]";
	}
}