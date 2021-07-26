# FITT360SDK

document url
http://54.180.46.204/api/android/index.html

android 360 viewer library
https://github.com/ashqal/MD360Player4Android

* if you want use this over android 11, rndis will not work because rndis's ip change every time. and it fixed by latest firmware but not released so if you have to use rndis, please wait to release latest firmware.

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

# Fixed - 11/23/20
1. sometimes app crashed by null point exception
2. sometimes rndis does not connect 

# Fixed - 01/08/21
1. bluetooth bonded checker has problem so it can make not show pairing dialog sometimes 

# Fixed - 02/10/21
1. removed unnessary code in sdk

# Changed - 03/05/21
1. rtspStreamerManager can start without surface -> will not start media codec ( video, audio )

# Fixed - 04/05/21
1. direct rtmp does not send video's resolution and frame rate, so some of rtmp server does not accept the stream. 

# New feature - 07/26/21
1. you can make single mode looks like cropped.
 - select single mode
 - select 1600x1600 sensor resolution
 - select 16:9 resolution ( 1920x1080, 3840x2160 )
2. direct rtmp does not support old firmware ( 1.8.0 or 2.2.0 ).
3. media contents list api has json format problem so added new api.

