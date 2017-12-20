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

**Attention : ne pas dépasser 250 follows en même temps.**

##

