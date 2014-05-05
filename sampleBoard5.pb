#this board is cray
board
  ball name=Ball x=0.5 y=0.5 xVelocity=2.5 yVelocity=2.5
  squareBumper name=Square0 x=0 y=2
  squareBumper name=Square2 x=2 y=2
  circleBumper name=Circle4 x=4 y=3
  circleBumper name=Circle5 x=5 y=4
  circleBumper name=Circle7 x=7 y=6
  circleBumper name=Circle12 x=12 y=6
  circleBumper name=Circle15 x=15 y=3
  triangleBumper name=Tri1 x=8 y=9 orientation=270
  leftFlipper name=FlipL1 x=8 y=2 orientation=0
  rightFlipper name=FlipR1 x=11 y=2 orientation=0
  leftFlipper name=FlipL2 x=8 y=7 orientation=0
  rightFlipper name=FlipR2 x=11 y=7 orientation=0
  absorber name=Abs x=10 y=17 width=10 height=2 
  squareBumper name=Square13 x=13 y=2
  squareBumper name=Square7 x=7 y=2
  squareBumper name=Square14 x=14 y=2
  circleBumper name=Circle13 x=13 y=5
  fire trigger=Square7 action=FlipL1
  triangleBumper name=Tri2 x=11 y=9 orientation=180
  circleBumper name=Circle6 x=6 y=5
  
  