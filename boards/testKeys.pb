board name=flipland gravity = 20.0 friction1=0.0

leftFlipper name=Flip1 x=2 y=15 orientation=90
rightFlipper name=Flip2 x=5 y=15 orientation=90
leftFlipper name=Flip3 x=8 y=15 orientation=90
rightFlipper name=Flip4 x=12 y=15 orientation=90
leftFlipper name=Flip5 x=15 y=15 orientation=90

keydown key=space action=Flip1
keyup key=up action=Flip2
keydown key=right action=Flip3
keyup key=left action=Flip4
keydown key=down action=Flip5
