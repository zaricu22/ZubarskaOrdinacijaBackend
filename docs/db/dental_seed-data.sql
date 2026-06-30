-- ZubarskaOrdinacija — sample seed data
-- Requires dental_init.sql to have been applied first.
-- 4 korisnici · 1 parametar · 12 termini
--
-- Target:  CockroachDB / PostgreSQL — uses three-part names (dental.public.table)
--          MySQL:  replace three-part names with two-part (dental.table).

-- ============================================================
-- WIPE ALL EXISTING DATA (uncomment if re-seeding)
-- ============================================================
-- TRUNCATE TABLE dental.public.termin,
--               dental.public.korisnik,
--               dental.public.parametri CASCADE;

-- ============================================================
-- Korisnici
-- tip_korisnika: 'pacijent' | 'zubar'
-- identifikacioni_broj: personal ID number (JMBG-like); may be empty string for anonymous
-- ============================================================

INSERT INTO dental.public.korisnik
    (identifikacioni_broj, ime, prezime, tip_korisnika)
VALUES
    ('',            'Paja',  'Pajic',     'pacijent'),
    ('0213456789',  'Janko', 'Jankovic',  'pacijent'),
    ('02144556677', 'Paja',  'Pajic',     'pacijent'),
    ('432543',      'Filip', 'Filipovic', 'zubar');

-- ============================================================
-- Parametri
-- rokOtkazivanja: number of days before an appointment within
--                 which cancellation is no longer allowed
-- ============================================================

INSERT INTO dental.public.parametri
    (parametar_naziv, parametar_vrednost)
VALUES
    ('rokOtkazivanja', 2);

-- ============================================================
-- Termini
-- id is GENERATED ALWAYS AS IDENTITY — omit it from INSERT
-- termin must be unique across the table
-- trajanje: duration in minutes
-- status:   'zakazano' | 'otkazano'
-- ============================================================

INSERT INTO dental.public.termin
    (termin, tip_pregleda, korisnik_id, status, trajanje)
VALUES
    ('2022-09-10 09:00:00', 'pregled',    '0213456789',  'zakazano', 30),
    ('2022-09-10 11:30:00', 'plombiranje','0213456789',  'zakazano', 30),
    ('2022-09-11 22:07:00', 'pregled',    '0213456789',  'zakazano', 60),
    ('2022-09-10 14:30:00', 'pregled',    '0213456789',  'zakazano', 60),
    ('2022-09-14 14:30:00', 'pregled',    '0213456789',  'zakazano', 60),
    ('2022-09-10 16:00:00', 'pregled',    '0213456789',  'zakazano', 60),
    ('2022-09-15 14:30:00', 'pregled',    '0213456789',  'zakazano', 60),
    ('2022-09-16 16:00:00', 'vadjenje',   '02144556677', 'zakazano', 30),
    ('2022-09-16 15:00:00', 'vadjenje',   '02144556677', 'otkazano', 30),
    ('2022-09-16 15:30:00', 'kontrola',   '02144556677', 'otkazano', 30),
    ('2022-09-16 11:30:00', 'operacija',  '0213456789',  'zakazano', 30),
    ('2022-09-16 16:30:00', 'ggg',        '0213456789',  'zakazano', 30);
