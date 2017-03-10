# -*- coding: utf-8 -*-
"""
Created on Tue Jan 10 20:28:08 2017

@author: Elliot
"""
import numpy as np
import matplotlib.pyplot as plt

#Gradient optimal
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
   
#===================================================================

def G(u, b, deltaX, Q):
    M = np.zeros((len(u), len(u)))
    for i in range(len(u)):
        M[i][i] = 2*b/deltaX**2
        if(i < len(u)-1):
            M[i][i+1] = -b/deltaX**2
            M[i+1][i] = -b/deltaX**2
    s = np.dot(M, u)-Q
    for i in range(len(u)):
        s[i] = s[i] + u[i]**4
    return s

def gradG(u, b, deltaX):
    M = np.zeros((len(u), len(u)))
    for i in range(len(u)):
        M[i][i] = 2*b/deltaX**2
        if(i < len(u)-1):
            M[i][i+1] = -b/deltaX**2
            M[i+1][i] = -b/deltaX**2
    
    M = M + 4*np.diag(u**3)
    return M
    
def equationStationnaire():
    b = 1
    
    deltaX = 0.01
    imax = 1000
    x = np.linspace(-10, 10, 2*imax+1)
    Q = np.ones(2*imax+1)
    u = Q
    #met en oeuvre  la méthode de newton
    for i in range(10):
        g =  G(u, b, deltaX, Q)
        gradg = gradG(u, b, deltaX)
        A = gradg
        B = g
        print("pas " + str(i+1))
        u = u - inverserMatrice(A, B, u)
        
    plt.plot(x, u)
    
    plt.title("Equation de la chaleur stationnaire pour b = " + str(b))
    plt.legend()
    plt.show()


equationStationnaire()