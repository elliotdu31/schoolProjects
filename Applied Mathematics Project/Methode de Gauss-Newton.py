# -*- coding: utf-8 -*-
"""
Created on Tue Jan 10 19:14:23 2017

@author: Elliot
"""

import numpy as np
import matplotlib.pyplot as plt
    
    
def g(x , b):
    k = len(b)//3
    s = 0
    for j in range(k):
        s = s +  b[j]*np.exp(-0.5*(x-b[2*k+j])**2/b[k+j]**2)
    return s
    
def f(x, y, b):
    m = len(y)
    s = np.zeros(m)
    for i in range(m):
        s[i] = y[i] - g(x[i], b)
    return s
    
def Df(x, y, b):
    k = len(b)//3
    m = len(y)
    s = np.zeros((m, 3*k))
    for i in range(m):
        for j in range(k):
            e = -np.exp(-0.5*(x[i]-b[2*k+j])**2/b[k+j]**2)
            s[i][j] = e
            s[i][j+k] = b[j]*e*(x[i]-b[2*k+j])**2/b[k+j]**3
            s[i][j+2*k] = b[j]*e*(x[i]-b[2*k+j])/b[k+j]**2
    return s
    
#met en oevure l'algorihme Gauss-Newton avec approximation de la Hessienne de J
def GaussNewton(x, y, b, iterations):
    plt.scatter(x, y, s = 200)
    plt.scatter(x, g(x, b), color="green", label="depart")
    for i in range(iterations):
        df = Df(x, y, b)
        dft = np.transpose(df)
        gradJ = np.dot(dft, f(x, y, b))
        hessJinv = np.linalg.inv(np.dot(dft, df))
        #processus itératif
        b = b - np.dot(hessJinv, gradJ)
        plt.scatter(x, g(x, b))

    plt.scatter(x, g(x, b), color="red", label="i="+str(i+1))
    plt.title("méthode de Gauss-Newton")
    plt.legend()
    plt.show()
    
x = np.array([1, 2, 3, 4, 5, 6, 7, 8])
y = np.array([0.127, 0.2, 0.3, 0.25, 0.32, 0.5, 0.7, 0.9])
b = np.array([1, 1, 1, 1, 3, 7])
GaussNewton(x, y, b, 100)

b = np.array([1, 1, 1, 1, 3, 6])
GaussNewton(x, y, b, 9)
#pour GaussNewton(x, y, b, 20) par exemple, 