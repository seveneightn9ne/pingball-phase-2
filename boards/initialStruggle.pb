board name=sampleBoard1 gravity=20.0 friction1=0.020 friction2=0.020

  # define a ball
  ball name=Ball x=0.5 y=0.5 xVelocity=2.5 yVelocity=2.5

  # define a series of square bumpers
  squareBumper name=Square0 x=3 y=6
  squareBumper name=Square1 x=4 y=6
  squareBumper name=Square2 x=5 y=6
  squareBumper name=Square3 x=13 y=6
  squareBumper name=Square4 x=14 y=6
  squareBumper name=Square5 x=15 y=6

  squareBumper name=Square12 x=18 y=3
  squareBumper name=Square13 x=17 y=3
  squareBumper name=Square14 x=16 y=3
  squareBumper name=Square15 x=12 y=3
  squareBumper name=Square16 x=11 y=3
  squareBumper name=Square17 x=10 y=3
  squareBumper name=Square18 x=8 y=3
  squareBumper name=Square19 x=7 y=3
  squareBumper name=Square20 x=6 y=3
  squareBumper name=Square21 x=2 y=3
  squareBumper name=Square22 x=1 y=3
  squareBumper name=Square23 x=0 y=3

  # define some triangular bumpers
  triangleBumper name=Tri1 x=19 y=0 orientation=90
  triangleBumper name=Tri2 x=1 y=4 orientation=90
  triangleBumper name=Tri3 x=2 y=5 orientation=90
  triangleBumper name=Tri4 x=18 y=4 orientation=0
  triangleBumper name=Tri5 x=17 y=5 orientation=0

  # define an absorber to catch the ball at the bottom
  absorber name=Abs x=0 y=17 width=20 height=1

  # make the absorber self-triggering
  fire trigger=Abs action=Abs

