board name=flipland gravity = 20.0 friction1=0.0

ball name=BallA x=1.8 y=4.5 xVelocity=10.4 yVelocity=10.3
ball name=BallB x=10.0 y=7.0 xVelocity=-3.4 yVelocity=-2.3
ball name=BallC x=6.0 y=2.0 xVelocity=1.2 yVelocity=-2.3
ball name=BallD x=15.0 y=3.5 xVelocity=-2.0 yVelocity=-2.3

triangleBumper name=Rebound x=19 y=10 orientation=90
# leftFlipper name=Rebound2 x=17 y=5 orientation=90

leftFlipper name=Flip1 x=2 y=15 orientation=90
leftFlipper name=Flip2 x=5 y=15 orientation=90
leftFlipper name=Flip3 x=8 y=15 orientation=90
leftFlipper name=Flip4 x=12 y=15 orientation=90
leftFlipper name=Flip5 x=15 y=15 orientation=90

fire trigger=Flip1 action=Flip5
fire trigger=Flip4 action=Flip1
fire trigger=Flip3 action=Flip2
fire trigger=Flip2 action=Flip3
fire trigger=Flip5 action=Flip4

absorber name=Abs x=0 y=18 width=20 height=2
fire trigger=Flip1 action=Abs
fire trigger=Flip2 action=Abs
fire trigger=Flip3 action=Abs
fire trigger=Flip4 action=Abs
fire trigger=Flip5 action=Abs

# fire trigger=Abs action=Abs
