board name=funwithportals2 gravity=20.0 friction1=0.020 friction2=0.020

  # define some balls
  ball name=Ball x=0.5 y=0.5 xVelocity=2.5 yVelocity=2.5
  
  # define some portals
  portal name=sweeney x=4 y=4 otherPortal=lucy
  portal name=lucy x=10 y=13 otherPortal=beadle
  portal name=beadle x=5 y=12 otherPortal=turpin
  portal name=turpin x=6 y=6 otherPortal=johanna
  portal name=johanna x=4 y=8 otherPortal=anthony
  portal name=anthony x=19 y=5 otherBoard=funwithportals otherPortal=johanna

  # define an absorber to catch the ball at the bottom
  absorber name=Abs x=0 y=17 width=20 height=1

  # make the absorber self-triggering
  fire trigger=Abs action=Abs