-- CREATE DATABASE gestion_produits;
USE gestion_produits;

/*
CREATE TABLE IF NOT EXISTS produit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    libelle VARCHAR(100),
    type VARCHAR(50),
    quantite_stock INT,
    disponibilite BOOLEAN
);

CREATE TABLE IF NOT EXISTS utilisateur (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL,
    nom_complet VARCHAR(100),
    role        VARCHAR(20)  DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS action_log (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    action          VARCHAR(50)  NOT NULL,
    produit_code    VARCHAR(50),
    produit_libelle VARCHAR(100),
    utilisateur     VARCHAR(50)  NOT NULL,
    date_action     DATETIME     NOT NULL,
    details         VARCHAR(500)
);



INSERT INTO produit (code, libelle, type, quantite_stock, disponibilite) VALUES
('P001', 'Ordinateur Portable Dell',  'Informatique',  15, TRUE),
('P002', 'Souris Sans Fil Logitech',  'Informatique',   8, TRUE),
('P003', 'Imprimante HP LaserJet',    'Bureautique',    3, FALSE),
('P004', 'Écran Samsung 24"',         'Électronique',   6, TRUE),
('P005', 'Clavier Mécanique RGB',     'Informatique',  12, TRUE);


INSERT INTO utilisateur (username, password, nom_complet, role) VALUES 
('user1', 'user123', 'Utilisateur', 'USER'),
('admin', 'admin123', 'Administrateur', 'ADMIN');

*/


SELECT * FROM produit;
SELECT * FROM utilisateur;
SELECT * FROM ACTION_LOG
