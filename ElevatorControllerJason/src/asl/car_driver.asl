// Agent car_driver in project Elevator Simulator in Jason

/* Initial beliefs and rules */

is_extreme(F):- at_top(F) | at_bottom(F).

floor(F):-at_top(F) | at_bottom(F) | checkpoint(F,_) | destination(F).

in_direction(A,B):- (not A==B) & (direction(up) | (A>B)) & (direction(down) | (A<B)). //From A to B follow D

on_path(F):-at_top(F) | at_bottom(F) | (direction(D) & (checkpoint(F,D) | destination(F))).

nearest(N):-at(A) & on_path(N) & in_direction(A,N) & not (on_path(C) & in_direction(A,C)&in_direction(C,N)).

direction(up).

at_bottom(1).

//at(1)[source(percept)].

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").


-started:true
<-
.print("End of game");
-destination(_);
-request(_,_).

+started
: true
<-
//-at(0)[source(percept)];
+at(1)[source(percept)]. //This is not really set by the environment, need to find a way through

+started:true
<-
.print("Game started").

-at(X) : direction(Direction)
<-.print("Moved away from ",X," following ",Direction);
-remove_check_point(X,Direction).

+at(X):not (destination(_) | checkpoint(_,_))
<-
.print("I'm stopping now");
stop.

+at(X) : true
<-
.print("I'm now at ",X);
!reset_extremes;
!check_direction;
!move(X);
!update_checkpoint(X);
go.

-destination(X):true
<-.print("I don't care about ",X).

+destination(X):true
<-.print("New destination ",X).

+!update_checkpoint(X):direction(D)
<-
.print("Checkpoint removed: ",X,",",D);
-checkpoint(X,D).

+!update_checkpoint(X).

+!move(X):true
<-!determine_next_destination.

+!move(X).


//Check point-related features

+!add_check_point(Floor,Direction): true
<-
+checkpoint(Floor,Direction);
!reset_extremes.

+!remove_check_point(Floor,Direction): true
<-
-checkpoint(Floor,Direction);
!reset_extremes.

//Reseting extreme points (top & bottom)
+!reset_extremes:true
<-
!assign_temp_extremes;
!reset_top;
!reset_bottom.

+!assign_temp_extremes:checkpoint(F,_) | at(F)
<-!set_at_top(F);
!set_at_bottom(F).

+!reset_top:(destination(F)|checkpoint(F,_)) & at_top(T) & F>T
<-!set_at_top(F);
!reset_top.

+!reset_top:at(F) & at_top(T) & F>T
<-!set_at_top(F);
!reset_top.

+!reset_top.

+!reset_bottom:(checkpoint(F,_) | destination(F)) & at_bottom(B) & F<B
<-!set_at_bottom(F);
!reset_bottom.

+!reset_bottom:at(F) & at_bottom(B) & F<B
<-!set_at_bottom(F);
!reset_bottom.

+!reset_bottom.

+!set_at_top(F):true
<--at_top(_);
+at_top(F).

+!set_at_bottom(F):true
<--at_bottom(_);
+at_bottom(F).

//Determine next destination

+!determine_next_destination:nearest(N)
<-?direction_at(N,Direction);
.print("Direction at ",N," is ",Direction);
set_destination(N);
set_direction(N,Direction).

+!determine_next_destination:true
<-.print("Cannot determine the nearest floor");
stop.

+?direction_at(Floor,Direction):at_top(Floor) & (not checkpoint(Floor,up))
<-Direction=down.

+?direction_at(Floor,Direction):at_bottom(Floor) & (not checkpoint(Floor,down))
<-Direction=up.

+?direction_at(Floor,Direction):direction(D)
<-Direction=D.

+!set_destination(Floor,Direction):true
<-
-+destination(Floor,Direction);
set_direction(Floor,Direction);
set_destination(Floor).

//Change the direction we are going
+!check_direction: at(X) & at_top(X)
<-
.print("Will go down now");
-direction(_);
+direction(down).

+!check_direction: at(X) & at_bottom(X)
<-
.print("Will go up now");
-direction(_);
+direction(up).

+!check_direction. //Default case, nothing to do

+request(Floor):not destination(Floor)
<-
+destination(Floor);
.print("Someone inside me want to go to floor ",Floor);
//!determine_nearest;
!reset_extremes;
!check_direction;
!determine_next_destination;
//-request(Floor)[source(percept)];
go.

+request(Floor):true
<-.print("I'm already aware of the need to go to ",Floor).

-request(Floor):true
<--destination(Floor).

+request(Floor,Direction):true
<-!add_check_point(Floor,Direction).

-request(Floor,Direction):true
<-
.print("There is no need to go ",Direction," at floor ",Floor);
!remove_check_point(Floor,Direction).
