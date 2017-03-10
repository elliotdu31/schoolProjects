# -*- coding: utf-8 -*-
"""
Created on Tue Dec 27 14:38:07 2016

@author: Elliot
"""

import numpy as np
import matplotlib.pyplot as plt

#Met en oeuvre la méthode de gradient à pas optimal et affiche le tracé de r/ro
#A une matrice symétrique
#B un vecteur
#Calcule la sultion de Ax = B
def gradientOptimal(A, B, u0):
    title = "A = " + str(A) + ", B = " + str(B) + " et u0 = " + str(u0)
    
    r0 = np.dot(A,u0) - B
    r = r0
    y = [1]
    rapport = 1
    while(rapport > 10**-2):
        
        p = np.dot(r, r)/np.dot(np.dot(A,r), r)
        
        u0 = u0 - p*r
        
        #r = grad J
        r = np.dot(A,u0) - B
        
        rapport = np.linalg.norm(r)/np.linalg.norm(r0)
        
        y = y + [rapport]
    
    print(str(len(y)) + " itérations")
    x = np.arange(len(y))
    title =  title + ". - Résultat : " + str(u0)
    plt.title(title)
    plt.plot(x, y)
    plt.semilogy()
    plt.xlabel('itérations')
    plt.ylabel('|rk|/|ro|')
    plt.show()
    
    return u0

#même chose que précédement mais se contente de renvoyer le résultat
#Calcule Ax = b
def inverserMatrice(A, B, u0):
    r0 = np.dot(A,u0) - B
    r = r0

    rapport = 1
    while(rapport > 10**-2):
        
        p = np.dot(r, r)/np.dot(np.dot(A,r), r)
        if p < 0:
            print("caca" + str(p))
        u0 = u0 - p*r
        
        #r = grad J
        r = np.dot(A,u0) - B
        
        rapport = np.linalg.norm(r)/np.linalg.norm(r0)
            
    return u0
    
A = np.array([[1, 0], [0, 20] ])
B = np.array([1 , 1])
u0 = np.array([-2, 5])

gradientOptimal(A, B, u0)

A = np.array([[1, 0], [0, 10000] ])
B = np.array([1 , 1])
u0 = np.array([0, 0])

gradientOptimal(A, B, u0)

A = np.array([[1, 0], [0, 0.01] ])
B = np.array([1 , 1])
u0 = np.array([0, 5])

gradientOptimal(A, B, u0)

A = np.array([[1, 0], [0, 2] ])
B = np.array([1 , 1])
u0 = np.array([0, 10])

gradientOptimal(A, B, u0)
