# Jeu d'encerclement de pions

## Comment jouer ?

### Règles

Dans ce jeu, chaque client est un joueur, dans une des deux équipes possible. Le but du jeu est de capturer tous les pions de l'adversaire.

- Chaque pion peut se déplacer vers la droite, la gauche, le haut ou le bas sur le terrain grâce aux flèches
- Si un pion est entouré horizontalement ou verticalement par deux pions de l'équipe opposé, il est éliminé et change de couleur
- La partie se termine quand toute une équipe est éliminée

### Lancer une partie

#### **Côté serveur :**

Pour compiler le serveur, exécutez ces commandes, en partant du dossier `src\serveur`

```bash
javac -d ../build/Serveur *.java
cd ../build/Serveur
jar -cvfm Serveur.jar MANIFEST.MF *.class
```

Pour lancer le serveur, effectuez la commande

```
java -jar Serveur.jar
```

#### **Côté client :**

Pour compiler le serveur, exécutez ces commandes, en partant du dossier `src\client`

```bash
javac -d ../build/Client *.java
cd ../build/Client
jar -cvfm Client.jar MANIFEST.MF *.class
```

Pour lancer le serveur, effectuez la commande

```
java -jar TPClient.jar <idJoueur> <equipeJoueur> <positionX> <positionY>
```

`idJoueur` : L’identifiant du joueur pendant la partie \
`equipeJoueur` : Obligatoirement 1 ou 2. Représente l’équipe dont le joueur veut faire partie \
`positionX` : La position en X où le joueur commencera sa partie \
`positionY` : La position en Y où le joueur commencera sa partie

## Bugs connus

- Après avoir bloqué une personne, il arrive qu’un joueur ne puisse plus aller où elle veut sur la carte, de manière aléatoire
- Lorsqu’une personne se bloque elle-même en allant entre deux joueurs de l’équipe
  opposée, le pion devient invisible sur le client des autres joueurs, qui peuvent désormais prendre sa place
- Lorsqu’un joueur se déconnecte, son pion disparaît mais n’est visiblement pas retiré de la liste des joueurs, ce qui peut empêcher de finir correctement la partie
- La disponibilité des ID n’est pas vérifiée, pouvant causer problème si deux personnes avec le même identifiant sont dans la même équipe
