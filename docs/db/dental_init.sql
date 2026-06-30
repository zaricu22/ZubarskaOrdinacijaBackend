-- Dental (ZubarskaOrdinacija) — database initialisation script
-- Creates the database, schema, tables, and constraints from scratch.
--
-- Target:  CockroachDB / PostgreSQL — uses three-part names (dental.public.table).
--          MySQL:  replace three-part names with two-part (dental.table),
--                  replace GENERATED ALWAYS AS IDENTITY with AUTO_INCREMENT.
--
-- Run in two steps separately to avoid errors:
--   1. CREATE DATABASE (connect as superuser)
--   2. Everything else (connect to the new database)

-- ============================================================
-- Database
-- ============================================================

CREATE DATABASE IF NOT EXISTS dental;

-- ============================================================
-- Schema
-- ============================================================

CREATE SCHEMA IF NOT EXISTS public;

-- ============================================================
-- Safe drop order (children first)
-- ============================================================

DROP TABLE IF EXISTS dental.public.termin;
DROP TABLE IF EXISTS dental.public.parametri;
DROP TABLE IF EXISTS dental.public.korisnik;

-- ============================================================
-- Tables
-- ============================================================

CREATE TABLE IF NOT EXISTS dental.public.korisnik (
    identifikacioni_broj  VARCHAR(15)  NOT NULL,
    ime                   VARCHAR(15)  NOT NULL,
    prezime               VARCHAR(15)  NOT NULL,
    tip_korisnika         VARCHAR(15)  NOT NULL,  -- e.g. 'pacijent', 'zubar'

    CONSTRAINT pk_korisnik PRIMARY KEY (identifikacioni_broj)
);

CREATE TABLE IF NOT EXISTS dental.public.parametri (
    parametar_naziv     VARCHAR(45)  NOT NULL,
    parametar_vrednost  INT          DEFAULT NULL,

    CONSTRAINT pk_parametri PRIMARY KEY (parametar_naziv)
);

CREATE TABLE IF NOT EXISTS dental.public.termin (
    id           INT          NOT NULL GENERATED ALWAYS AS IDENTITY,  -- MySQL: AUTO_INCREMENT
    termin       TIMESTAMP    NOT NULL,
    tip_pregleda TEXT         NOT NULL,  -- e.g. 'pregled', 'plombiranje', 'vadjenje', 'kontrola', 'operacija'
    korisnik_id  VARCHAR(15)  NOT NULL,
    status       VARCHAR(15)  NOT NULL,  -- e.g. 'zakazano', 'otkazano'
    trajanje     INT          NOT NULL,  -- duration in minutes

    CONSTRAINT pk_termin      PRIMARY KEY (id),
    CONSTRAINT uq_termin_time UNIQUE      (termin),
    CONSTRAINT fk_korisnik
        FOREIGN KEY (korisnik_id)
        REFERENCES dental.public.korisnik (identifikacioni_broj)
        ON DELETE RESTRICT
);

-- ============================================================
-- Constraints
-- ============================================================

ALTER TABLE dental.public.korisnik
    ADD CONSTRAINT chk_tip_korisnika
    CHECK (tip_korisnika IN ('pacijent', 'zubar'));

ALTER TABLE dental.public.termin
    ADD CONSTRAINT chk_status
    CHECK (status IN ('zakazano', 'otkazano'));

ALTER TABLE dental.public.termin
    ADD CONSTRAINT chk_trajanje_positive
    CHECK (trajanje > 0);
