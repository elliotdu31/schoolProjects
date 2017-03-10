# -*- coding: utf-8 -*-
"""
Created on Tue Jan  3 23:07:03 2017

@author: Elliot

"""

import numpy as np
import matplotlib.pyplot as plt

#Algorithmes implémentant divers méthodes d'optimisation
#Calcule Ax = b
def inverserMatrice(A, B, u0):
    r0 = np.dot(A,u0) - B
    r = r0

    rapport = 1
    while(rapport > 10**-2):
        
        p = np.dot(r, r)/np.dot(np.dot(A,r), r)
        
        u0 = u0 - p*r
        
        #r = grad J
        r = np.dot(A,u0) - B
        
        rapport = np.linalg.norm(r)/np.linalg.norm(r0)
            
    return u0
    
#===================================================================================
    
    
#ttable les temps auxquels ont souhaite afficher u(t) triés par orde chronologique
def convectionDiffusion(ttable): 
    a = 1
    b = 1
    imax = 50
    deltax = 10/(2*imax)
    deltat = 0.01
    
    x = np.linspace(-10, 10, 2*imax+1)
    u0 = np.exp(-0.5*x**2)/np.sqrt(2*np.pi)
    plt.plot(x, u0, '--', label="u0")    
    
    #calcul de M
    p = b*deltat/deltax**2
    q = 0.5*a*deltat/deltax
    M = np.zeros((2*imax+1, 2*imax+1))
    for i in range(2*imax+1):
        M[i][i] = 1 + 2*p
        if i < 2*imax-1:
            M[i][i+1] = q-p
            M[i+1][i] = -p-q
            
    Mt = np.transpose(M)
    MtM = np.dot(Mt, M)
    
    n = 0
    for t in ttable:
        while n*deltat < t:
            u0 = inverserMatrice(MtM, np.dot(Mt, u0), u0)
            n = n+1
        plt.plot(x, u0, label="u("+ str(t)+"s)")
    
    plt.title("Equation de convection-diffusion pour a = " + str(a) + " et b = " + str(b))
    plt.legend()
    plt.show()
    

def equationChaleur(ttable):
    b = 1
    imax = 50
    deltax = 10/(2*imax) #=0.1
    deltat = 0.01
    
    x = np.linspace(-10, 10, 2*imax+1)
    u0 = np.exp(-0.5*x**2)/np.sqrt(2*np.pi)
    plt.plot(x, u0, '--', label="u0")
    
    p = b*deltat/deltax**2
    A = np.zeros((2*imax+1, 2*imax+1))
    for i in range(2*imax+1):
        A[i][i] = 1 + 2*p
        if i < 2*imax-1:
            A[i][i+1] = -p
            A[i+1][i] = -p
            
    n = 0
    for t in ttable:
        while n*deltat < t:
            diag = np.zeros((2*imax+1, 2*imax+1))
            for i in range(2*imax+1):
                diag[i][i] = u0[i]**3
            M = A + diag*deltat
            Mt = np.transpose(M)
            MtM = np.dot(Mt, M)
            u0 = inverserMatrice(MtM, np.dot(Mt, u0), u0)
            n = n+1
        plt.plot(x, u0, label="u("+ str(t)+"s)")
    
    
    plt.title("Equation de la chaleur implicite pour b = " + str(b))
    plt.legend()
    plt.show()

convectionDiffusion([0.1, 1, 2, 5, 10])
equationChaleur([0.1, 1, 5, 10])

