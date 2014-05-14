board name=triangleBumperHYPE gravity = 10.0

ball name=BallA x=1.8 y=4.5 xVelocity=10.4 yVelocity=10.3
ball name=BallB x=10.0 y=7.0 xVelocity=-3.4 yVelocity=-2.3

triangleBumper name=Tri1 x=1 y=4 orientation=90
triangleBumper name=Tri2 x=1 y=12 orientation=180
triangleBumper name=Tri3 x=2 y=5 orientation=90
triangleBumper name=Tri4 x=2 y=11 orientation=180
triangleBumper name=Tri5 x=6 y=4 orientation=270
triangleBumper name=Tri6 x=7 y=5 orientation=270
triangleBumper name=Tri7 x=8 y=9 orientation=270
triangleBumper name=Tri8 x=11 y=9 orientation=180
triangleBumper name=Tri9 x=15 y=2 orientation=90
triangleBumper name=Tri10 x=17 y=5 orientation=0
triangleBumper name=Tri11 x=17 y=11 orientation=270
triangleBumper name=Tri12 x=18 y=4 orientation=0
triangleBumper name=Tri13 x=18 y=12 orientation=270
triangleBumper name=Tri14 x=19 y=0 orientation=90
triangleBumper name=Tri15 x=19 y=3 orientation=90

# define an absorber to catch the ball at the bottom
absorber name=Abs x=0 y=19 width=20 height=1 

# make the absorber self-triggering
fire trigger=Abs action=Abs 

