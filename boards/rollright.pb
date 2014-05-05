board name=rr
ball name=Ball x=0.5 y=0.5 xVelocity=0.0 yVelocity=0.0

triangleBumper name=Rebound x=19 y=2 orientation=90

triangleBumper name=Tri1_1 x=0 y=3 orientation=270
squareBumper name=Square1_1 x=1 y=4
squareBumper name=Square1_2 x=2 y=4
squareBumper name=Square1_3 x=3 y=4
squareBumper name=Square1_4 x=4 y=4
squareBumper name=Square1_5 x=5 y=4
triangleBumper name=Tri1_2 x=6 y=4 orientation=270
triangleBumper name=Tri1_3 x=7 y=5 orientation=270
squareBumper name=Square1_8 x=8 y=6
squareBumper name=Square1_9 x=9 y=6
squareBumper name=Square1_10 x=10 y=6
squareBumper name=Square1_11 x=11 y=6
squareBumper name=Square1_12 x=12 y=6
squareBumper name=Square1_13 x=13 y=6
squareBumper name=Square1_14 x=14 y=6
squareBumper name=Square1_15 x=15 y=6
squareBumper name=Square1_16 x=16 y=6

# define an absorber to catch the ball at the bottom
absorber name=Abs x=0 y=19 width=20 height=1

# make the absorber self-triggering
fire trigger=Abs action=Abs
