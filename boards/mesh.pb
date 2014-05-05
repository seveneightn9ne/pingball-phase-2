board name=mesh gravity = 10.0

ball name=BallA x=1.8 y=4.5 xVelocity=10.4 yVelocity=10.3
ball name=BallB x=10.0 y=7.0 xVelocity=-3.4 yVelocity=-2.3
ball name=BallC x=6.0 y=2.0 xVelocity=1.2 yVelocity=-2.3
ball name=BallD x=15.0 y=3.5 xVelocity=-2.0 yVelocity=-2.3

triangleBumper name=Rebound x=15 y=2 orientation=90


circleBumper name=Circle1_1 x=1 y=8
circleBumper name=Circle1_2 x=6 y=8
circleBumper name=Circle1_3 x=12 y=8
circleBumper name=Circle1_6 x=19 y=8

circleBumper name=Circle2_1 x=2 y=10
circleBumper name=Circle2_2 x=5 y=12
circleBumper name=Circle2_3 x=10 y=9
circleBumper name=Circle2_6 x=18 y=13

circleBumper name=Circle3_1 x=3 y=13
circleBumper name=Circle3_2 x=7 y=15
circleBumper name=Circle3_3 x=8 y=10
circleBumper name=Circle3_6 x=19 y=14

absorber name=Abs x=4 y=17 width=12 height=2

fire trigger=Abs action=Abs
