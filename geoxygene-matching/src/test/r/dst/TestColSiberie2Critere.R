#
# TODO: Add comment, Add licence
# 
# Author: Marie-Dominique Van Damme
# 23/10/2014
# version 1.0-SNAPSHOT
###############################################################################
# Appariement "Appriou" avec 2 critères : 
#   - le critère de distance 
#   - le critère toponymique
#
###############################################################################

library(maptools)
library(EvCombR)
library(sp)

distanceTopo <- function (VecteurMot2) {
  mot2 <- VecteurMot2[1]
  d <- stringdist(mot1, mot2)
  d <- 1 - d / max (nchar(mot1), nchar(mot2))
  return (d)
}

# Définition des connaissances
connaissanceDistEucli <- function (type, dist) {
  retour <- 0
  if (type == 'AppC') {
    if (0 <= dist & dist < 800) {
      retour <- -0.9/800*dist + 1
    } else if (dist >= 800) {
      retour <- 0.1
    }
  } else if (type == 'NonAppC') {
    if (0<= dist & dist < 400) {
      retour <- 0
    } else if (400 <= dist & dist < 800) {
      retour <- 0.8/400*dist - 0.8
    } else if (dist >= 800) {
      retour <- 0.8
    }
  } else if (type == 'Tout') {
    if (0 <= dist & dist < 400) {
      retour <- 0.45/400 * dist
    } else if (400 <= dist & dist < 800) {
      retour <- -0.35/400*dist + 0.8
    } else if (dist >= 800) {
      retour <- 0.1
    }
  }
  return (retour)
}
connaissanceDistTopo <- function (type, dist) {
  retour <- 0
  if (type == 'AppC') {
    if (0 <= dist & dist < 800) { 
      retour <- (-0.9) / 800 * dist + 1
    } else if (dist >= 800) {
      retour <- 0.1
    }
  } else if (type == 'NonAppC') {
    if (0 <= dist & dist < 400) {
      retour <- 0
    } else if (400<= dist & dist < 800) {
      retour <- 0.8 / 400 * dist - 0.8
    } else if (dist >= 800) {
      retour <- 0.8
    }
  } else if (type == 'Tout') {
    if (0 <= dist & dist < 400) {
      retour <- 0.45/400 * dist
    } else if (400<= dist & dist < 800){
      retour <- (-0.35) / 400 * dist + 0.8
    } else if (dist >= 800) {
      retour <- 0.1
    }
  }
  return (retour)
}


# on recupere les jeux de donnees
jd1 <- readShapePoints("E:/Workspace/geoxygene/geoxygene-matching/data/colsiberie/jd1_col_siberie.shp", 
                       proj4string=CRS("+proj=utm +zone=33 +datum=WGS84"))
jd2 <- readShapePoints("E:/Workspace/geoxygene/geoxygene-matching/data/colsiberie/jd2_col_siberie.shp", 
                       proj4string=CRS("+proj=utm +zone=33 +datum=WGS84"))

# on affiche les points
plot(jd2, lwd=2, col="blue")
plot(jd1, add=TRUE, lwd=2, col="orange")

# on affiche les toponymes
text (jd1@coords, as.character(jd1$toponyme), cex = 1, pos = 2, col="orange")
text (jd2@coords, as.character(jd2$NOM), cex = 1, pos = 2, col="blue")

# construit l'environnement
stateSpace <- subset(jd2, select=c("NOM"))@data
stateSpace <- as.vector(stateSpace$NOM) 
str(stateSpace)

# Calcule les distances euclidiennes pour tous les candidats
tabDistEcuclidienne <- spDistsN1(jd2@coords, jd1@coords, longlat = FALSE)
tabDistEcuclidienne

# Calcule les distances topographiques pour tous les candidats
mot1 <- levels(jd1$toponyme)
tabDistTopo <- lapply(as.character(jd2$NOM), distanceTopo)
tabDistTopo <- unlist(tabDistTopo)
tabDistTopo


# Boucle sur les candidats : y'a surement mieux !
nCandidat <- length(jd2)
nCandidat
beliefs <- list()
for (i in 1:nCandidat) {
  
  nom <- as.character(jd2$NOM[i])
  
  ch1 <- ''
  ch2 <- ''
  ch3 <- ''
  for (j in 1:nCandidat) {
    nomTmp <- as.character(jd2$NOM[j])
    if (nom == nomTmp) {
      ch1 <- nomTmp
    } else {
      if (ch2 == '') {
        ch2 <- nomTmp
      } else {
        ch2 <- paste(ch2, nomTmp, sep="/")
      }
    }
    if (ch3 == '') {
      ch3 <- nomTmp
    } else {
      ch3 <- paste(ch3, nomTmp, sep="/")
    }
    
  }
  # names(tabDistEcuclidienne) <- c(ch1, ch2, ch3)
  
  # construct mass functions for Candidate i
  # critere 1 
  # m11 <- mass(list("TP"=0.723, "CS/GM"=0, "CS/TP/GM"=0.277), stateSpace)
  mH1 <- connaissanceDistEucli('AppC', tabDistEcuclidienne[1])
  mH2 <- connaissanceDistEucli('NonAppC', tabDistEcuclidienne[2])
  mH3 <- connaissanceDistEucli('Tout', tabDistEcuclidienne[3])
  list11 <- list(mH1, mH2, mH3)
  names(list11) <- c(ch1, ch2, ch3)
  # print(list11)
  # print(str(list11))
  print(list11)
  print(stateSpace)
  m11 <- mass(list11, stateSpace)
  # print(m11)
  
  # m12 <- mass(list("TP"=0.6975, "CS/GM"=0.1681, "CS/TP/GM"=0.1344), stateSpace)
  #list12 <- list(connaissanceDistTopo('AppC', tabDistTopo[1]), 
  #               connaissanceDistTopo('NonAppC', tabDistTopo[2]), 
  #               connaissanceDistTopo('Tout', tabDistTopo[3]))
  #names(list12) <- c(ch1, ch2, ch3)
  #print (list12)
  #m12 <- mass(list12, stateSpace)
  #print (m12)
  
  # fusion des critères 
  #m1 <- dComb(m11, m12)
  #print (m1)
  
  # beliefs <- c(beliefs, m1)
  
  # construct mass functions for Candidate 2
  # m21 <- mass(list("CS"=0.5753, "TP/GM"=0, "CS/TP/GM"=0.4247), stateSpace)
  # m22 <- mass(list("CS"=1, "TP/GM"=0, "CS/TP/GM"=0), stateSpace)
  # fusion des criteres
  # m2 <- dComb(m21, m22)
  # m2
  
  # construct mass functions for Candidate 3
  # m31 <- mass(list("GM"=0.4913, "CS/TP"=0.1044, "CS/TP/GM"=0.4043), stateSpace)
  # m32 <- mass(list("GM"=0.6975, "CS/TP"=0.1681, "CS/TP/GM"=0.1344), stateSpace)
  # fusion des criteres
  # m3 <- dComb(m31, m32)
  # m3
  
}

# fusion des candidats
# nFusion <- length(beliefs)
# nFusion

# mt <- dComb(m1, m2)
# m123 <- dComb(mt, m3)
# m123@"focal"
# str(m123@"focal")

# decision
# c2 <- pign(m123)


