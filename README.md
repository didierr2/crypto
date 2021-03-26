# Crypto Project
Utilitaires autour des cotations crypto.
Permet de lire un fichier excel contenant des cryptos et d'aller récupérer les cours correspondants en euros.

## Lancement
2 paramètres : 
- le fichier excel des cryptos à coter
- le fichier de propriétés pour configurer le programme par rapport au fichier excel

## Prérequis 
* Java 11 

## Fonctionnement 
Le programme doit disposer pour chaque crypto de l'url pour récupérer la cotation.

Les sites supportés pour l'instant pour le web scraping sont courscryptomonnaies.com et coinmarketcap.com.

Pour chaque crypto, le programme récupère l'url de mise à jour, scrappe le site et met a jour la cote de la crypto, sa variation sur 24h (si la colonne est configurée dans le properties), le rang de la crypto (capitalisation boursiere de la crypto par rapport aux autres, si la colonne est configurée dans le properties) et la date de mise à jour.

Le fichier properties permet de donnes les noms des colonnes correspondant au nom de la crypto, l'url de maj, la date de maj, le rang, la variation 24h, le nom de la feuille excel, etc. Ceci permet de conserver une indépendance entre la structure du fichier excel et le programme.


