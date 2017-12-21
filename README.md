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

##

# Tests de performance :

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




1000,692,625,611,477,583,842,1005,796,384,634,516,1036


((1000-707)^2+(692-707)^2+(625-707)^2+(611-707)^2+(477-707)^2+(583-707)^2+(842-707)^2+(1005-707)^2+(796-707)^2+(384-707)^2+(634-707)^2+(516-707)^2+(1036-707)^2)/13



1316,1254,1377,1464,2389,2521,1376,1330,2462,2210 --> 100

1311, 1675, 1800, 1436 --> 50

216,580,183,266,176,341,221,475,277,321,212