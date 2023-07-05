/**
 * PicoGo by SkySLAM
**/

function picogo_move_to_direction( fx, fy, tx, ty, ang_deg ) {
  console.log( "**** sunabove ", "picogo_move_to_direction" ) ; 

  if( typeof Android !== "undefined" && Android !== null) {
       Android.moveToDirection( fx, fy, tx, ty, ang_deg ) ;
  }
  
}

function picogo_when_start() {
  console.log( "**** sunabove ", "picogo_when_start" ) ; 

  if( typeof Android !== "undefined" && Android !== null) {
      Android.whenStartEntry( ) ;
  }
}

function picogo_toggle_stop() {
  console.log( "**** sunabove ", "picogo_toggle_stop" ) ; 

  if( typeof Android !== "undefined" && Android !== null) {
      Android.toggleStop( ) ;
  }
}

function picogo_toggle_pause( b ) {
  console.log( "**** sunabove ", "picogo_toggle_pause b = " + b ) ; 

  if( typeof Android !== "undefined" && Android !== null) {
      Android.togglePause( b ) ;
  }
}