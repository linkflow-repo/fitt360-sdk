# FITT360SDK

document url
http://54.180.46.204/api/android/index.html

android 360 viewer library
https://github.com/ashqal/MD360Player4Android


# New feature ( belong to Firmware 1.7.1 )
1. connect WIFI AP 
  - Supported DHCP and static ip address
  - If you want set static ip, you should fill gateway ip address.
2. USB Tethering ( RNDIS ) 
  - If FITT360 connected with SDK sample app as USB Tethering, RNDIS connect automatically within 10 second.
  - USB Tethering's RNDIS requires an available USIM chip. If smartphone and tablet doesn't have available USIM chip
    , can not use FITT360 as USB Tethering. 
    
# New feature - 05/29/20
1. enabled Stabilization
  - you can enable or disable this function in preview page.

# New feature - 06/22/20
1. enabled Stitching filter
  - you can check this feature in Setting -> Other settings
  - first, click Stitching Fitler Enable and make on.
  - click Stitching Filter Type and input 4096 ( 4096 : seperate mode )
  - Stitching Filter dual scale H and V can change horizontal, vertical scale size. ( 1.0 is full size )
  
# New feature - 06/25/20
1. enabled RTMP Streaming mute
  - you can make mute or unmute as realtime.
  - If you muted and started playing RTMP and started playing with VLC, you can't unmute it. 
  However, if you stop VLC playback and run it again, you will hear a sound.
  
# Fixed - 11/02/20
1. rndis cannot works when usb connect and disconnect immediately
2. bluetooth helper module support mulitiple listener
3. removed, request bluetooth pairing during bluetooth discovery after bonded none 
    - it unnessary step, because FITT360 device does not pairing mode. 
    
# Fixed - 11/17/20
1. bluetooth connect - sometimes bluetooth re-connect after connected wifi direct
2. supported bluetooth connect by device's address without discovery ( but the deivce have to be paired )
