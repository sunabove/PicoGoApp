/**
 * PicoGo by SkySLAM
**/

function is_android() {
  return typeof Android !== "undefined" && Android !== null ; 
}
function picogo_move_to_direction( dist ) {
  console.log( "**** sunabove ", "picogo_move_to_direction" ) ; 

  if( is_android() ) {
       Android.moveToDirection( dist ) ;
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

function picogo_add_direction( ang_deg ) {
  console.log( "**** sunabove ", "picogo_add_direction ang_deg = " + ang_deg ) ; 

  if( is_android() ) {
      Android.addDirection( ang_deg ) ;
  }
} 

function picogo_move_x( x ) {
  console.log( "**** sunabove ", "picogo_picogo_move_x x = " + x ) ; 

  if( is_android() ) {
      Android.moveX( x ) ;
  }
} 

function picogo_move_y( y ) {
  console.log( "**** sunabove ", "picogo_picogo_move_y y = " + y ) ; 

  if( is_android() ) {
      Android.moveY( y ) ;
  }
} 

function picogo_locate_x( x ) {
  console.log( "**** sunabove ", "picogo_picogo_locate_x x = " + x ) ; 

  if( is_android() ) {
      Android.locateX( x ) ;
  }
} 

function picogo_locate_y( y ) {
  console.log( "**** sunabove ", "picogo_picogo_locate_y y = " + y ) ; 

  if( is_android() ) {
      Android.locateY( y ) ;
  }
} 

function picogo_locate_xy( x, y ) {
  console.log( "**** sunabove ", "picogo_picogo_locate_xy x = " + x + ", y = " + y ) ; 

  if( is_android() ) {
      Android.locateXY( x, y ) ;
  }
} 
