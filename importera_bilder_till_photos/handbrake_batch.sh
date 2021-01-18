#!/bin/bash
# Batch convert videos with HandBrake CLI
# By Ralph Crisostomo - 2016.04.17
#
# Usage :
#   'sudo ./handbrake.sh /source /destination'
#
# Reference :
#   https://forum.handbrake.fr/viewtopic.php?f=6&t=19426
#   https://gist.github.com/czj/1263872
#   https://trac.handbrake.fr/wiki/BuiltInPresets#universal
#

SOURCE=$1
DESTINATION=$2


# Universal
# ref: https://trac.handbrake.fr/wiki/BuiltInPresets#universal
#
PRESET="--encoder x264 f --quality 20.0 --rate 15 --pfr  --audio 1,1 --aencoder faac,copy:ac3 --ab 160,160 -6 dpl2,none --arate Au to,Auto --drc 0.0,0.0 --audio-copy-mask aac,ac3,dtshd,dts,mp3 --audio-fallback ffac3 --format mp4 --maxWidth 720 --maxHeight 576 --loose-anamorphic --modulus 2 --markers --x264-preset fast --h264-profile baseline --h264-level 3.0 --optimize --subtitle 1 --subtitle-burned"


while IFS= read -d '' -r ITEM
do

  echo $ITEM

  FILE=${ITEM##*/}
  EXT=${ITEM##*.}
  EXT=$(echo $EXT | tr "[:upper:]" "[:lower:]")
  OUTPUT="$DESTINATION/${FILE%.*}.$EXT"

  # Create directory
  [[ -d $DESTINATION ]] || mkdir -p $DESTINATION

  echo "" | HandBrakeCLI -i "$ITEM" -o "$OUTPUT" $PRESET


done< <(find "$SOURCE" \( -iname '*.mp4' -or -iname '*.avi'  -or -iname '*.mkv' -or -iname '*.mts' \) -print0)
