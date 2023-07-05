/**
 * PicoGo by SkySLAM
**/

function is_android() {
  return typeof Android !== "undefined" && Android !== null ; 
}
function picogo_move_to_direction( fx, fy, tx, ty, ang_deg ) {
  console.log( "**** sunabove ", "picogo_move_to_direction" ) ; 

  if( is_android() ) {
       Android.moveToDirection( fx, fy, tx, ty, ang_deg ) ;
  }  
}

function picogo_when_start() {
  console.log( "**** sunabove ", "picogo_when_start" ) ; 

  if( is_android() ) {
      Android.whenStartEntry( ) ;
  }
}

function picogo_toggle_stop() {
  console.log( "**** sunabove ", "picogo_toggle_stop" ) ; 

  if( is_android() ) {
      Android.toggleStop( ) ;
  }
}

function picogo_toggle_pause( b ) {
  console.log( "**** sunabove ", "picogo_toggle_pause b = " + b ) ; 

  if( is_android() ) {
      Android.togglePause( b ) ;
  }
}

function picogo_add_rotation( ang_deg ) {
  console.log( "**** sunabove ", "picogo_add_rotation ang_deg = " + ang_deg ) ; 

  if( is_android() ) {
      Android.addRotation( ang_deg ) ;
  }
}
