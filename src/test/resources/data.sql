INSERT INTO INGREDIENTS (nom,quantite) VALUES
('Tomate',1),
('Mozza',3),
('Jambon blanc',2);

INSERT INTO CLIENT (id, nom,email) VALUES
(1,'Gigi','gigi@gmail.com'),
(2,'Lolo','lolo@gmail.com'),
(3,'Marco','marco@gmail.com');

INSERT INTO PIZZAS (nom) VALUES
('Regina'),
('Chèvre Miel');

INSERT INTO PRIX_PIZZA (pizza_id, taille, prix) VALUES
(1, 0, 8.5),
(1, 1, 10.5),
(1, 2, 12.5);

INSERT INTO PIZZA_INGREDIENT (pizza_id, ingredient_id) VALUES
(1, 1),
(1, 2),
(1, 3);

