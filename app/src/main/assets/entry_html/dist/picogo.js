/**
 * PicoGo by SkySLAM
**/

function picogo_move_to_direction( fx, fy, tx, ty, ang_deg ) {
  console.log( "**** sunabove ", "picogo_move_to_direction" ) ; 

  if( typeof Android !== "undefined" && Android !== null) {
       Android.moveToDirection( fx, fy, tx, ty, ang_deg ) ;
  }
  
}