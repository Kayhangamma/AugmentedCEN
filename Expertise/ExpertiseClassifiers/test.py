import pylab
import numpy as np
from scipy.stats import norm

x = np.linspace(-0.5,0.5,10)
y = norm.pdf(x, loc=0.2511, scale=0.003309)    # for example
pylab.plot(x,y)
pylab.show()