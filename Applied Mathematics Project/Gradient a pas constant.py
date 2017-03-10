# -*- coding: utf-8 -*-
"""
Created on Tue Dec 27 14:00:18 2016

@author: Elliot
"""


import numpy as np
import matplotlib.pyplot as plt

def J(u1, u2):
    return (u1-1)**2 + 100*(u1**2-u2)**2

def gradJ(u1, u2):
    s = [0, 0]
    s[0] = 2*(u1-1) + 400*(u1**2-u2)*u1
    s[1] = -200*(u1**2-u2)
    return s

def gradientPasConstant(u0, p):
    x = [u0[0]]
    y = [u0[1]]
    while(J(u0[0], u0[1]) > 10**-3):
        w = gradJ(u0[0], u0[1])
        u0[0] = u0[0] - p*w[0]
        u0[1] = u0[1] - p*w[1]
        x = x + [u0[0]]
        y = y + [u0[1]]
    print("minimum en (" + str(u0[0]) + ", " + str(u0[1]) + ")")
    return (x, y)

x = np.linspace(-1.5, 1.5, 100)
y = np.linspace(-1.5, 1.5, 100)
X, Y = np.meshgrid(x, y)

#création des lignes de niveau de J
Z = J(X, Y)
plt.contourf(X, Y, Z, 50)

pathX, pathY = gradientPasConstant([1,0], 0.002)
plt.title("Gradient pas constant pour p=0.002. " + str(len(pathY)) + " itérations.")

plt.plot(pathX, pathY, 'r')
plt.scatter([pathX[0], pathX[len(pathX)-1]],[pathY[0], pathY[len(pathY)-1]])
plt.text(pathX[len(pathX)-1], pathY[len(pathY)-1], 'minimum')
plt.show()


"""
pour p = 0.002
minimum en (0.9684125664682152, 0.9376940768892983)
2764 itérations

pour p = 0.0045
le programme ne parvient pas à trouver de résultat (du moins en temps raisonnable)

pour p = 0.01
OverflowError: (34, 'Result too large')

"""

