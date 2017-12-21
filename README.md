# Liens utiles :

* Lien du client : http://tinytwitter-189016.appspot.com/
Pour se connecter : renseigner le champ "Login" avec un nom d'utilisateur valide (possibilité d'en créer un nouveau via "Pas encore membre ?"). Les champs "Password" sont inutiles.

* Lien de l'API explorer : https://apis-explorer.appspot.com/apis-explorer/?base=https://tinytwitter-189016.appspot.com/_ah/api#p/


# Méthodes de l'API REST :

## addFollow(Long userId, Long followId)
Permet au user identifié par **userId** de follow le user identifié par **followId** (rétroactif sur les tweets précédemment publiés).

## connectUser(Long userId)
Connecte le user identifié par **userId** (pas de système de password ou de sécurisation OAuth par manque de temps).

## createXUsers(Long nbUsers, String usernameRange)
Crée **nbUsers** users dans le datastore avec pour username **usernameRange** + leur numéro de création. 
**Attention : nbUsers ne doit pas être trop grand pour éviter les timeout (optimal : paquets de 500)**

## followUserRange(Long userId, Long userRangeBegin, Long userRangeEnd)
Permet au user identifié par **userId** de se faire follow par un grand nombre de personne (entre userRangeBegin et **userRangeEnd**).

**Attention : ne pas dépasser 250 follows en même temps.**

## followXUserRange(Long userId, Long userRangeBegin, Long userRangeEnd)
Permet au user identifié par **userId** de se faire follow par un grand nombre de personne (entre userRangeBegin et **userRangeEnd**).

**Attention : ne pas dépasser 250 follows en même temps pour des gros volumes de user (5000 par exemple).**

## getTimeline(Long userId, Long messageLimitBegin, Long messageLimitEnd)
Retourne la timeline du user identifié par **userId** (y compris ses propres messages). Le nombre de tweets récupérés est paramétré par **messageLimitBegin** et **messageLimitEnd**.

## insertNewMesssage(String message, Long userId, String username)
Insert un nouveau message par l'utilisateur identifié par **userId**. Son username est également passé par le client pour éviter les requêtes inutiles par la suite (gain de temps, perte d'espace).

## insertNewUser(String username)
Ajoute un nouveau user dans le datastore (**username** est unique).

## listUsers(Long usersLimitBegin, Long usersLimitEnd)
Retourne la liste de tous les utilisateurs (limités par **usersLimitBegin** et **usersLimitEnd**).


# Tests de performance :

* Tests réalisés pour la plupart côté serveur en ommetant les valeurs non représentatives.

## Test 1 : datastore contenant plus de 5000 users :

### Test d'envoi de message :

* Conditions du test : un user follow par 4485 personnes
* Moyenne des temps d'execution : 707 ms
* Variance : 41509

### Test de récupération de timeline :

* Conditions du test : un user qui follow 2800 personnes

* Moyenne des temps d'execution pour 100 messages : 1780 ms
* Variance : 268891

* Moyenne des temps d'execution pour 50 messages : 1550 ms
* Variance : 37030

* Moyenne des temps d'execution pour 10 messages : 297 ms
* Variance : 14804


## Test 2 : datastore contenant 1000 users 

### Test d'envoi de message :

* Conditions du test : un user follow par 1000 personnes
* Moyenne des temps d'execution : 442 ms
* Variance : 7121

### Test de récupération de timeline :

* Conditions du test : un user qui follow 1000 personnes

* Moyenne des temps d'execution pour 100 messages : 1136 ms
* Variance : 217630

* Moyenne des temps d'execution pour 50 messages : 765 ms
* Variance : 78401

* Moyenne des temps d'execution pour 10 messages : 398 ms
* Variance : 10568


## Test 3 : datastore contenant 100 users 

### Test d'envoi de message :

* Conditions du test : un user follow par 100 personnes
* Moyenne des temps d'execution : 462 ms
* Variance : 14401

### Test de récupération de timeline :

* Conditions du test : un user qui follow 100 personnes

* Moyenne des temps d'execution pour 100 messages : 270 ms
* Variance : 3470

* Moyenne des temps d'execution pour 50 messages : 251 ms
* Variance : 2589

* Moyenne des temps d'execution pour 10 messages : 241 ms
* Variance : 1935

